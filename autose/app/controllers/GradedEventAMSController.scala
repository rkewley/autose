
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

object GradedEventAMSController extends ControllerTrait[Long, MdlGradedEventAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlGradedEventAMS](
    mapping (
	"fidGradedEventAMS" -> optional(of[Long]),
	"fEventNumberAMS" -> of[Long],
	"fCourse" -> of[Long],
	"fName" -> text,
	"fDescription" -> text,
	"fDetailedDescription" -> text,
	"fType" -> text,
	"fMaxPoints" -> of[Double],
	"fLesson" -> of[Long]
    )(MdlGradedEventAMS.apply)(MdlGradedEventAMS.unapply)
  )
  
  val fileUploadForm = CoursesController.form
  
  	def listAll  = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
	  Ok(views.html.viewlist.listAllGradedEventAMS(crud.all.sortWith(GradedEventAMS.compare)))
	}

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html = {
	  views.html.viewlist.listGradedEventAMS(getAll(ffk), ffk)
  }
 
	override def listFunction(item: MdlGradedEventAMS)(implicit user: MdlUser): Html =
	  views.html.viewlist.listGradedEventAMS(getAll(item), item.vCourse)
 
	override def showFunction(vGradedEventAMS: MdlGradedEventAMS): Html = {
    views.html.viewshow.showGradedEventAMS(vGradedEventAMS)
  }
	
	override def editFunction(mdlGradedEventAMSForm: Form[MdlGradedEventAMS]): Html = 
	  views.html.viewforms.formGradedEventAMS(mdlGradedEventAMSForm, 0)
	
	override def createFunction(mdlGradedEventAMSForm: Form[MdlGradedEventAMS]): Html = 
	  views.html.viewforms.formGradedEventAMS(mdlGradedEventAMSForm, 1)
	  
	def crud = slick.AppDB.dal.GradedEventAMS


    def newItem(fkId: Long): MdlGradedEventAMS = new MdlGradedEventAMS(Option(0), 0, fkId, "", "", "", "", 0.0, 0)
    
    override def getAll(fkId: Long): List[MdlGradedEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradedEventAMS.allQuery.filter(v1GradedEventAMS => v1GradedEventAMS.vCourse === fkId).elements.toList
    }
    
    override def getAll(vGradedEventAMS: MdlGradedEventAMS): List[MdlGradedEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradedEventAMS.allQuery.filter(v1GradedEventAMS => v1GradedEventAMS.vCourse === vGradedEventAMS.vCourse).elements.toList
    }
    
  def uploadGradedEventsAMS = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewforms.formGradedEventAMSUpload(fileUploadForm.fill(CoursesController.newItem(0))))
  }

  def saveGradedEventAMSUpload = Action(parse.multipartFormData) { implicit request =>
  	request.body.file("gradedEventsFile").map { gradedEventsFile =>
  	  	addGradedEventsFromFile(gradedEventsFile.ref.file)
  		Redirect(routes.GradedEventAMSController.listAll)
  	}.getOrElse {
  		BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
  	}
  }
  
    def importDescriptionsFromOnlineGradedEvents = compositeAction(Faculty){ implicit user => implicit template => implicit request =>
      // pull all graded event mappings
      val gradedEventMappings = AppDB.dal.MappingGradedEvent.all
      val gradedEvents = AppDB.dal.GradedRequirements.all
      val gradedEventsAMS = AppDB.dal.GradedEventAMS.all

      
      gradedEventMappings.foreach { gradedEventMapping =>
        // see if there is a mapping for this graded event in the mappings file
        gradedEvents.find {ge => ge.vGradedEventIndex.get == gradedEventMapping.vGradedEvent} match {
          case Some(gradedEvent) => {
            // Get the associated AMS graded event
            gradedEventsAMS.find(ge => ge.vidGradedEventAMS.get == gradedEventMapping.vGradedEventAMS) match {
              case Some(gradedEventAMS) => {
                val update = new MdlGradedEventAMS(gradedEventAMS.vidGradedEventAMS, gradedEventAMS.vEventNumberAMS, gradedEventAMS.vCourse, gradedEventAMS.vName,
                    gradedEventAMS.vDescription, gradedEvent.vEventDescription, gradedEventAMS.vType, gradedEventAMS.vMaxPoints, gradedEventAMS.vLesson)
                println(gradedEvent.selectIdentifier._2 + " = " + gradedEventAMS.selectIdentifier)
                AppDB.dal.GradedEventAMS.update(update)
              }
              case None =>
            }
          }
          case None =>
        }
      }
      Redirect(routes.GradedEventAMSController.listAll)
    }
  
  

  def addGradedEventsFromFile(gradedEventsFile: java.io.File): List[String] = { 
    val lines = scala.io.Source.fromFile(gradedEventsFile).getLines.toList.tail
    val allEvents = AppDB.dal.GradedEventAMS.all
    val courses = AppDB.dal.Courses.all
    val errors: List[String] = lines.foldLeft(List[String]()){(errorList, line) => 
      val values = line.split(",")
      val courseData = values(0).trim
      val courseIdNumber = courseData match {
        case "SE300" => "SE300-SE301"
        case "SE301" => "SE300-SE301"
        case _ => courseData
      }
      val date = new java.util.Date
      //val (year,term)  = date.getMonth match {
      //  case x if x >= 7 => (date.getYear()+1, 1)
      //  case _ => (date.getYear, 2)
      //}
      val year: Int = values(1).toInt
      val term: Int = values(2).toInt
      val eventNumberAMS = values(3).toInt
      val name = values(5)
      val description = values(4)
      val gradedEventType = values(6)
      val lesson = values(7).toInt
      val maxPoints = values(8).toDouble
      val courseId = courses.filter { course =>
        course.vCourseIDNumber == courseIdNumber && course.vAcademicYear == year && course.vAcademicTerm == term 
      }.headOption
      //println("Event Data: " + courseIdNumber + ", " + name + ", AT" + year + " - " + term)
      val newErrors: List[String] = courseId match {
        case Some(course) => {
          val courseIdDatabase = course.vidCourse.get
          val mdlGradedEvent = MdlGradedEventAMS(Option(0), eventNumberAMS, courseIdDatabase, name, description, "",
              gradedEventType, maxPoints, lesson)
          println(mdlGradedEvent)
          println("Events:")
          //allEvents.foreach(println(_))
          val event = allEvents.find { event =>
            event.vCourse == courseIdDatabase && event.vEventNumberAMS == eventNumberAMS
          }
          if(event.isEmpty) {  // if there is not already a record for this event in the database
            //println("Inserting data")
            AppDB.dal.GradedEventAMS.insert(mdlGradedEvent)
          } else {
            val currentEvent = event.get
            val newEvent = MdlGradedEventAMS(currentEvent.vidGradedEventAMS, mdlGradedEvent.vEventNumberAMS, mdlGradedEvent.vCourse, mdlGradedEvent.vName, mdlGradedEvent.vDescription, 
                currentEvent.vDetailedDescription, mdlGradedEvent.vType, mdlGradedEvent.vMaxPoints, mdlGradedEvent.vLesson)
            AppDB.dal.GradedEventAMS.update(newEvent)
          }
          errorList
        }
        case None =>
          println("Could not find course for " + courseIdNumber + " AT" + year + "-" + term)
          ("Could not find course for " + courseIdNumber + " AT" + year + "-" + term) :: errorList
      }
      newErrors
    }
    errors
  }

  }
