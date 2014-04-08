
package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object MappingGradedEventController extends ControllerTrait[Long, MdlMappingGradedEvent, Long] with Base with OptionalAuthElement {

  val form = Form[MdlMappingGradedEvent](
    mapping (
	"fidMappingGradedEvent" -> optional(of[Long]),
	"fGradedEvent" -> of[Long],
	"fGradedEventAMS" -> of[Long]
    )(MdlMappingGradedEvent.apply)(MdlMappingGradedEvent.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listMappingGradedEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlMappingGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listMappingGradedEvent(getAll(item), getCourseForItem(item))
 
	override def showFunction(vMappingGradedEvent: MdlMappingGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showMappingGradedEvent(vMappingGradedEvent)
	
	override def editFunction(mdlMappingGradedEventForm: Form[MdlMappingGradedEvent]): Html = 
	  views.html.viewforms.formMappingGradedEvent(mdlMappingGradedEventForm, 0, 0)
	
	override def createFunction(mdlMappingGradedEventForm: Form[MdlMappingGradedEvent]): Html = 
	  views.html.viewforms.formMappingGradedEvent(mdlMappingGradedEventForm, 1, 0)
	  
	def createNewMapping(course: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
	  Ok(views.html.viewforms.formMappingGradedEvent(form, 1, course))
	}
	  
	def crud = slick.AppDB.dal.MappingGradedEvent

	override def getAll(course: Long): List[MdlMappingGradedEvent] = crud.all.filter { mapping =>
      val gradedRequirement = AppDB.dal.GradedRequirements.select(mapping.vGradedEvent)	
      gradedRequirement match {
        case Some(x) => x.vCourse == course
        case None => false
      }
  	}
	
	override def getAll(item: MdlMappingGradedEvent) = getAll(getCourseForItem(item))
  
    def getCourseForItem(item: MdlMappingGradedEvent) = AppDB.dal.GradedRequirements.select(item.vGradedEvent).get.vCourse

	def newItem(fkId: Long) = new MdlMappingGradedEvent
  }