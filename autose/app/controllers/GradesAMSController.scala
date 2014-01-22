
package controllers

import play.api._
import play.api.mvc.Action
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object GradesAMSController extends ControllerTrait[Long, MdlGradesAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlGradesAMS](
    mapping (
	"fidGradesAMS" -> optional(of[Long]),
	"fStudent" -> of[Long],
	"fGradedEventAMS" -> of[Long],
	"fPoints" -> of[Double],
	"fStudentId" -> text
    )(MdlGradesAMS.apply)(MdlGradesAMS.unapply)
  )
      
  	def listAll  = StackAction { implicit request => 
	  Ok(views.html.viewlist.listGradesAMS(crud.all, 0))
	}

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listGradesAMS(getAll(ffk), ffk)
 
	override def listFunction(item: MdlGradesAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listGradesAMS(getAll(item), item.vGradedEventAMS)
 
	override def showFunction(vGradesAMS: MdlGradesAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showGradesAMS(vGradesAMS)
	
	override def editFunction(mdlGradesAMSForm: Form[MdlGradesAMS]): Html = 
	  views.html.viewforms.formGradesAMS(mdlGradesAMSForm, 0)
	
	override def createFunction(mdlGradesAMSForm: Form[MdlGradesAMS]): Html = 
	  views.html.viewforms.formGradesAMS(mdlGradesAMSForm, 1)
	  
	def crud = slick.AppDB.dal.GradesAMS


    def newItem(fkId: Long): MdlGradesAMS = new MdlGradesAMS(Option(0), 0, fkId, 0.0, "")
    
    override def getAll(fkId: Long): List[MdlGradesAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradesAMS.allQuery.filter(v1GradesAMS => v1GradesAMS.vGradedEventAMS === fkId).elements.toList
    }
    
    override def getAll(vGradesAMS: MdlGradesAMS): List[MdlGradesAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradesAMS.allQuery.filter(v1GradesAMS => v1GradesAMS.vGradedEventAMS === vGradesAMS.vGradedEventAMS).elements.toList
    }
    
  def uploadGradesAMS = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(views.html.viewforms.formGradesAMSUpload(form.fill(newItem(0))))
  }

  def saveGradesAMSUpload = Action(parse.multipartFormData) { implicit request =>
  	request.body.file("gradesAMSFile").map { gradesAMSFile =>
  	  	println("Adding event grades")
  	  	addGradesAMSFromFile(gradesAMSFile.ref.file)
  		Redirect(routes.GradesAMSController.listAll)
  	}.getOrElse {
  		BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
  	}
  }
  //0 USMA_ID, 1 evnt_nbr, 2 descr_shrt, 3 crse_nbr, 4 mrks_ernd
  def addGradesAMSFromFile(gradesAMSFile: java.io.File): List[String] = { 
    val lines = scala.io.Source.fromFile(gradesAMSFile).getLines.toList.tail
    val courses = AppDB.dal.Courses.all
    val gradedEvents = AppDB.dal.GradedEventAMS.all
    val students = AppDB.dal.Students.all
    val errors: List[String] = lines.foldLeft(List[String]()){(errorList, line) => 
      val values = line.split(",")
      val cadetId = values(0)
      val courseData = values(3).trim
      val courseIdNumber = courseData match {
        case "SE300" => "SE300-SE301"
        case _ => courseData
      }
      val year: Int = 2014
      val term: Int = 1
      val gradedEventNumberAMS = values(1).toInt
      val name = values(2)
      val pointsEarned = values(4).toDouble
      val courseId = courses.find { course =>
        course.vCourseIDNumber == courseIdNumber && course.vAcademicYear == year && course.vAcademicTerm == term 
      }
      //println("Event Data: " + courseIdNumber + ", " + name + ", AT" + year + " - " + term)
      val newErrors: List[String] = courseId match {
        case Some(course) => {
          val courseIdDatabase = course.vidCourse.get
          val gradedEvent: Option[MdlGradedEventAMS] = gradedEvents.find { event =>
          	event.vCourse == courseIdDatabase && event.vEventNumberAMS == gradedEventNumberAMS
          }
          gradedEvent match {
            case Some(event: MdlGradedEventAMS) => {
              println("Graded Event: " + event)
              val eventIdDatabase = event.vidGradedEventAMS.get
                  students.find { aStudent =>
                    aStudent.vStudentId == cadetId
                  } match {
                    case Some(student: MdlStudents) => {
                      val mdlGradesAMS = MdlGradesAMS(Option(0), student.vidStudents.get, eventIdDatabase, pointsEarned, cadetId)
                      println("Event Grade: " + mdlGradesAMS)
                      if (AppDB.dal.GradesAMS.all.find { grades =>
                        grades.vStudentId == mdlGradesAMS.vStudentId && grades.vGradedEventAMS == mdlGradesAMS.vGradedEventAMS
                      }.isEmpty) {
                       println("Inserting Grades")
                       AppDB.dal.GradesAMS.insert(mdlGradesAMS)
                     }
                     errorList         
                    }
                    case None => {
                      println("Could not find student " + cadetId)
                      ("Could not find student " + cadetId) :: errorList       
                    }
                  }
            }
            case None => {
              println("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + name)
              ("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + name) :: errorList
            }
          }
        }
        case None => {
          println("Could not find course for " + courseIdNumber + " AT" + year + "-" + term)
          ("Could not find course for " + courseIdNumber + " AT" + year + "-" + term) :: errorList
        }
      }
      newErrors
    }
    errors
  }
    
  }
