
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

object MappingSubEventController extends ControllerTrait[Long, MdlMappingSubEvent, Long] with Base with OptionalAuthElement {

  val form = Form[MdlMappingSubEvent](
    mapping (
	"fidMappingSubEvent" -> optional(of[Long]),
	"fSubEvent" -> of[Long],
	"fSubEventAMS" -> of[Long]
    )(MdlMappingSubEvent.apply)(MdlMappingSubEvent.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listMappingSubEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlMappingSubEvent)(implicit user: MdlUser): Html =
	  views.html.viewlist.listMappingSubEvent(getAll(item), getCourseForItem(item))
 
	override def showFunction(vMappingSubEvent: MdlMappingSubEvent): Html =
	  views.html.viewshow.showMappingSubEvent(vMappingSubEvent)
	
	override def editFunction(mdlMappingSubEventForm: Form[MdlMappingSubEvent]): Html = 
	  views.html.viewforms.formMappingSubEvent(mdlMappingSubEventForm, 0, 0)
	
	override def createFunction(mdlMappingSubEventForm: Form[MdlMappingSubEvent]): Html = 
	  views.html.viewforms.formMappingSubEvent(mdlMappingSubEventForm, 1, 0)
	  
	def createNewSubEventMapping(course: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
	  Ok(views.html.viewforms.formMappingSubEvent(form, 1, course))
	}
	def crud = slick.AppDB.dal.MappingSubEvent
	
	def getCourseForItem(item: MdlMappingSubEvent) = {
	  val subEventAMS = AppDB.dal.SubEventAMS.select(item.vSubEventAMS).get
	  val gradedEventAMS = AppDB.dal.GradedEventAMS.select(subEventAMS.vGradedEvent).get
	  gradedEventAMS.vCourse
	}

	override def getAll(course: Long): List[MdlMappingSubEvent] = crud.all.filter { mapping =>
      val subEvent = AppDB.dal.SubEventAMS.select(mapping.vSubEventAMS).get
      val gradedRequirement = AppDB.dal.GradedEventAMS.select(subEvent.vGradedEvent)
      gradedRequirement match {
        case Some(x) => x.vCourse == course
        case None => false
      }
  	}

	override def getAll(item: MdlMappingSubEvent) = getAll(getCourseForItem(item))
	
	def newItem(fkId: Long) = new MdlMappingSubEvent
  }