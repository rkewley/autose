
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

object SubEventAMSController extends ControllerTrait[Long, MdlSubEventAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlSubEventAMS](
    mapping (
	"fidSubEventAMS" -> optional(of[Long]),
	"fGradedEventNumberAMS" -> of[Long],
	"fSubEventNumberAMS" -> of[Long],
	"fGradedEvent" -> of[Long],
	"fName" -> text,
	"fDescription" -> text,
	"fPoints" -> of[Double]
    )(MdlSubEventAMS.apply)(MdlSubEventAMS.unapply)
  )
      
  val fileUploadForm = CoursesController.form

	override def list(ffk: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
	  Ok(views.html.viewlist.listSubEventAMS(getAll(ffk), ffk))
	}
  
    def listAll  = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
	  Ok(views.html.viewlist.listAllSubEventAMS(crud.all))
	}

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listSubEventAMS(getAll(ffk), ffk)
 
	override def listFunction(item: MdlSubEventAMS)(implicit user: MdlUser): Html =
	  views.html.viewlist.listSubEventAMS(getAll(item), item.vGradedEvent)
 
	override def showFunction(vSubEventAMS: MdlSubEventAMS): Html =
	  views.html.viewshow.showSubEventAMS(vSubEventAMS)
	
	override def editFunction(mdlSubEventAMSForm: Form[MdlSubEventAMS]): Html = 
	  views.html.viewforms.formSubEventAMS(mdlSubEventAMSForm, 0)
	
	override def createFunction(mdlSubEventAMSForm: Form[MdlSubEventAMS]): Html = 
	  views.html.viewforms.formSubEventAMS(mdlSubEventAMSForm, 1)
	  
	def crud = slick.AppDB.dal.SubEventAMS


    def newItem(fkId: Long): MdlSubEventAMS = new MdlSubEventAMS(Option(0), 0, 0, fkId, "", "", 0.0)
    
    override def getAll(fkId: Long): List[MdlSubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubEventAMS.allQuery.filter(v1SubEventAMS => v1SubEventAMS.vGradedEvent === fkId).elements.toList
    }
    
    override def getAll(vSubEventAMS: MdlSubEventAMS): List[MdlSubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubEventAMS.allQuery.filter(v1SubEventAMS => v1SubEventAMS.vGradedEvent === vSubEventAMS.vGradedEvent).elements.toList
    }
    
  def uploadSubEventsAMS = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewforms.formSubEventAMSUpload(fileUploadForm.fill(CoursesController.newItem(0))))
  }

  def saveSubEventAMSUpload = Action(parse.multipartFormData) { implicit request =>
  	request.body.file("subEventsFile").map { subEventsFile =>
  	  	addSubEventsFromFile(subEventsFile.ref.file)
  		Redirect(routes.SubEventAMSController.listAll)
  	}.getOrElse {
  		BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
  	}
  }

  def addSubEventsFromFile(subEventsFile: java.io.File): List[String] = { 
    val lines = scala.io.Source.fromFile(subEventsFile).getLines.toList.tail
    val courses = AppDB.dal.Courses.all
    val gradedEvents = AppDB.dal.GradedEventAMS.all
    val errors: List[String] = lines.foldLeft(List[String]()){(errorList, line) => 
      val values = line.split(",")
      val courseData = values(0).trim
      val courseIdNumber = courseData match {
        case "SE300" => "SE300-SE301"
        case "SE301" => "SE300-SE301"
        case _ => courseData
      }
      val year: Int = values(1).toInt
      val term: Int = values(2).toInt
      val gradedEventNumberAMS = values(4).toInt
      val subEventNumberAMS = values(3).toInt
      val name = values(5)
      val description = values(6)
      val maxPoints = values(7).toDouble
      val courseId = courses.filter { course =>
        course.vCourseIDNumber == courseIdNumber && course.vAcademicYear == year && course.vAcademicTerm == term 
      }.headOption
      //println("Event Data: " + courseIdNumber + ", " + name + ", AT" + year + " - " + term)
      val newErrors: List[String] = courseId match {
        case Some(course) => {
          val courseIdDatabase = course.vidCourse.get
          val gradedEvent: Option[MdlGradedEventAMS] = gradedEvents.find { event =>
          	event.vCourse == courseIdDatabase && event.vEventNumberAMS == gradedEventNumberAMS
          }
          gradedEvent match {
            case Some(event) => {
              //println("Graded Event: " + event)
              val eventIdDatabase = event.vidGradedEventAMS.get
              val mdlSubEvent = MdlSubEventAMS(Option(0), gradedEventNumberAMS, subEventNumberAMS, eventIdDatabase, name, description, maxPoints)
              //println("Sub Event: " + mdlSubEvent)
              val subEvent = AppDB.dal.SubEventAMS.all.find { subEvent =>
                subEvent.vGradedEvent == mdlSubEvent.vGradedEvent && subEvent.vSubEventNumberAMS == mdlSubEvent.vSubEventNumberAMS
              }
              if (subEvent.isEmpty) {
                //println("Inserting subEvent")
                AppDB.dal.SubEventAMS.insert(mdlSubEvent)
              } else {
                val currentSubEvent = subEvent.get
                val newSubEvent = MdlSubEventAMS(currentSubEvent.primaryKey, mdlSubEvent.vGradedEventNumberAMS, mdlSubEvent.vSubEventNumberAMS, mdlSubEvent.vGradedEvent,
                									mdlSubEvent.vName, mdlSubEvent.vDescription, mdlSubEvent.vPoints)
                AppDB.dal.SubEventAMS.update(newSubEvent)
              }
              errorList
            }
            case None => {
              println("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description)
              ("Could not find graded event for " + courseIdNumber + " AT" + year + "-" + term + ", " + description) :: errorList
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
