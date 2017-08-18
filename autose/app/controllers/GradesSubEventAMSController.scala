
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

object GradesSubEventAMSController extends ControllerTrait[Long, MdlGradesSubEventAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlGradesSubEventAMS](
    mapping (
	"fidGradesSubEventAMS" -> optional(of[Long]),
	"fStudent" -> of[Long],
	"fSubEventAMS" -> of[Long],
	"fPoints" -> of[Double],
	"fStudentID" -> text
    )(MdlGradesSubEventAMS.apply)(MdlGradesSubEventAMS.unapply)
  )

  	def listAll(errors: String)  = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
  	  val e = errors match {
  	    case "noerrors" => ""
  	    case _ => errors
  	  }
	  Ok(views.html.viewlist.listGradesSubEventAMS(crud.all, 0, e))
	}

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listGradesSubEventAMS(getAll(ffk), ffk, "")
 
	override def listFunction(item: MdlGradesSubEventAMS)(implicit user: MdlUser): Html =
	  views.html.viewlist.listGradesSubEventAMS(getAll(item), item.vSubEventAMS, "")
 
	override def showFunction(vGradesSubEventAMS: MdlGradesSubEventAMS): Html =
	  views.html.viewshow.showGradesSubEventAMS(vGradesSubEventAMS)
	
	override def editFunction(mdlGradesSubEventAMSForm: Form[MdlGradesSubEventAMS]): Html = 
	  views.html.viewforms.formGradesSubEventAMS(mdlGradesSubEventAMSForm, 0)
	
	override def createFunction(mdlGradesSubEventAMSForm: Form[MdlGradesSubEventAMS]): Html = 
	  views.html.viewforms.formGradesSubEventAMS(mdlGradesSubEventAMSForm, 1)
	  
	def crud = slick.AppDB.dal.GradesSubEventAMS


    def newItem(fkId: Long): MdlGradesSubEventAMS = new MdlGradesSubEventAMS(Option(0), 0, fkId, 0.0, "")
    
    override def getAll(fkId: Long): List[MdlGradesSubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradesSubEventAMS.allQuery.filter(v1GradesSubEventAMS => v1GradesSubEventAMS.vSubEventAMS === fkId).elements.toList
    }
    
    override def getAll(vGradesSubEventAMS: MdlGradesSubEventAMS): List[MdlGradesSubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradesSubEventAMS.allQuery.filter(v1GradesSubEventAMS => v1GradesSubEventAMS.vSubEventAMS === vGradesSubEventAMS.vSubEventAMS).elements.toList
    }
    
  def uploadGradesSubEventAMS = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewforms.formGradesSubEventAMSUpload(form.fill(newItem(0))))
  }

  def saveGradesSubEventAMSUpload = Action(parse.multipartFormData) { implicit request =>
  	request.body.file("gradesSubEventAMSFile").map { gradesSubEventAMSFile =>
  	  	println("Adding sub-event grades")
  	  	val errors = addGradesSubEventAMSFromFile(gradesSubEventAMSFile.ref.file)
  		Redirect(routes.GradesSubEventAMSController.listAll(errors.mkString("<br>")))
  	}.getOrElse {
  		BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
  	}
  }
  //0 USMA_ID,1 evnt_nbr,2 sub_evnt_nbr,3 sub_evnt_descr,4 crse_nbr,5 mrks_ernd
  def addGradesSubEventAMSFromFile(gradesSubEventAMSFile: java.io.File): List[String] = { 
    val lines = scala.io.Source.fromFile(gradesSubEventAMSFile).getLines.toList.tail
    val courses = AppDB.dal.Courses.all
    val gradedEvents = AppDB.dal.GradedEventAMS.all
    val subEvents = AppDB.dal.SubEventAMS.all
    val students = AppDB.dal.Students.all
    val errorsAndGrades: (List[String], List[MdlGradesSubEventAMS]) = lines.foldLeft(List[String](), List[MdlGradesSubEventAMS]()){(errorsAndGradesList, line) => 
      val values = line.split(",")
      val cadetId = values(0)
      val courseData = values(4).trim
      val courseIdNumber = courseData match {
        case "SE300" => "SE300-SE301"
        case "SE301" => "SE300-SE301"  
        case _ => courseData
      }
      val year: Int = values(5).toInt
      val term: Int = values(6).toInt
      val gradedEventNumberAMS = values(1).toInt
      val subEventNumberAMS = values(2).toInt
      val description = values(3)
      val pointsEarned = values(7).toDouble
      val courseId = courses.find { course =>
        course.vCourseIDNumber == courseIdNumber && course.vAcademicYear == year && course.vAcademicTerm == term 
      }
      //println("Event Data: " + courseIdNumber + ", " + name + ", AT" + year + " - " + term)
      val newErrorsAndGrades: (List[String], List[MdlGradesSubEventAMS]) = courseId match {
        case Some(course) => {
          val courseIdDatabase = course.vidCourse.get
          val gradedEvent: Option[MdlGradedEventAMS] = gradedEvents.find { event =>
          	event.vCourse == courseIdDatabase && event.vEventNumberAMS == gradedEventNumberAMS
          }
          gradedEvent match {
            case Some(event: MdlGradedEventAMS) => {
              println("Graded Event: " + event)
              val eventIdDatabase = event.vidGradedEventAMS.get
              subEvents.find { subEvent =>
          		subEvent.vGradedEvent == eventIdDatabase && subEvent.vSubEventNumberAMS == subEventNumberAMS
              } match {
                case Some(subEventAMS: MdlSubEventAMS) => {
                  students.find { aStudent =>
                    aStudent.vStudentId == cadetId
                  } match {
                    case Some(student: MdlStudents) => {
                      val mdlGradesSubEventAMS = MdlGradesSubEventAMS(Option(0), student.vidStudents.get, subEventAMS.vidSubEventAMS.get, pointsEarned, cadetId)
                      println("Sub Event Grade: " + mdlGradesSubEventAMS)
                      //if (AppDB.dal.GradesSubEventAMS.all.find { gradesSubEvent =>
                      //  gradesSubEvent.vStudentID == mdlGradesSubEventAMS.vStudentID && gradesSubEvent.vSubEventAMS == mdlGradesSubEventAMS.vSubEventAMS
                      //}.isEmpty) {
                      // println("Inserting subEvent")
                      // AppDB.dal.GradesSubEventAMS.insert(mdlGradesSubEventAMS)
                      //}
                      (errorsAndGradesList._1, mdlGradesSubEventAMS :: errorsAndGradesList._2)         
                    }
                    case None => {
                      println("Could not find student " + cadetId)
                      (("Could not find student " + cadetId) :: errorsAndGradesList._1, errorsAndGradesList._2)       
                    }
                  }
                }
                case None => {
                  println("Could not find sub event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description)
                  (("Could not find sub event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description) :: errorsAndGradesList._1, errorsAndGradesList._2)   
                }
              }
            }
            case None => {
              println("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description)
              (("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description) :: errorsAndGradesList._1, errorsAndGradesList._2)  
            }
          }
        }
        case None => {
          println("Could not find course for " + courseIdNumber + " AT" + year + "-" + term)
          (("Could not find course for " + courseIdNumber + " AT" + year + "-" + term) :: errorsAndGradesList._1, errorsAndGradesList._2)  
        }
      }
      newErrorsAndGrades
    }
    // Make sure all entries in the grades list are unique
    val grades: List[MdlGradesSubEventAMS] = errorsAndGrades._2.foldLeft(List[MdlGradesSubEventAMS]()) { (gradesList, mdlGrades) =>
      if (gradesList.find { aGradesEntry =>
        aGradesEntry.vStudentID == mdlGrades.vStudentID && aGradesEntry.vSubEventAMS == mdlGrades.vSubEventAMS
      }.isEmpty) mdlGrades :: gradesList else gradesList
    }
    val allGrades = AppDB.dal.GradesSubEventAMS.all
    grades.foreach { grade => 
      // If there is not already a grade for this student and this event in the database
      if (AppDB.dal.GradesSubEventAMS.all.find { gradesSubEvent =>
         gradesSubEvent.vStudentID == grade.vStudentID && gradesSubEvent.vSubEventAMS == grade.vSubEventAMS
      }.isEmpty) {
        println("Inserting subEvent")
        AppDB.dal.GradesSubEventAMS.insert(grade)
      } else {
        AppDB.dal.GradesSubEventAMS.update(grade)
      }
    }
    errorsAndGrades._1
  }
    
  }
