
package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.{MdlEvalCourseObjSubEvent, _}
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._

object EvalCourseObjSubEventController extends ControllerTrait[Long, MdlEvalCourseObjSubEvent, Long] with Base with OptionalAuthElement {

  val form = Form[MdlEvalCourseObjSubEvent](
    mapping (
	"fidEvalCourseObjSubEvent" -> optional(of[Long]),
	"fEvaluation" -> of[Long],
	"fCourseObjSubEvent" -> of[Long]
    )(MdlEvalCourseObjSubEvent.apply)(MdlEvalCourseObjSubEvent.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listEvalCourseObjSubEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlEvalCourseObjSubEvent)(implicit user: MdlUser): Html =
	  views.html.viewlist.listEvalCourseObjSubEvent(getAll(item), item.vEvaluation)
 
	override def showFunction(vEvalCourseObjSubEvent: MdlEvalCourseObjSubEvent): Html =
	  views.html.viewshow.showEvalCourseObjSubEvent(vEvalCourseObjSubEvent)
	
	override def editFunction(mdlEvalCourseObjSubEventForm: Form[MdlEvalCourseObjSubEvent]): Html = 
	  views.html.viewforms.formEvalCourseObjSubEvent(mdlEvalCourseObjSubEventForm, 0)
	
	override def createFunction(mdlEvalCourseObjSubEventForm: Form[MdlEvalCourseObjSubEvent]): Html = 
	  views.html.viewforms.formEvalCourseObjSubEvent(mdlEvalCourseObjSubEventForm, 1)
	  
	def crud = slick.AppDB.dal.EvalCourseObjSubEvent

	override def redirect(item: MdlEvalCourseObjSubEvent) = routes.EvalCourseObjSubEventController.list(item.vEvaluation)



	def newItem(fkId: Long): MdlEvalCourseObjSubEvent = new MdlEvalCourseObjSubEvent(Option(0), fkId, 0)
    
    override def getAll(fkId: Long): List[MdlEvalCourseObjSubEvent] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.EvalCourseObjSubEvent.allQuery.filter(v1EvalCourseObjSubEvent => v1EvalCourseObjSubEvent.vEvaluation === fkId).elements.toList
    }
    
    override def getAll(vEvalCourseObjSubEvent: MdlEvalCourseObjSubEvent): List[MdlEvalCourseObjSubEvent] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.EvalCourseObjSubEvent.allQuery.filter(v1EvalCourseObjSubEvent => v1EvalCourseObjSubEvent.vEvaluation === vEvalCourseObjSubEvent.vEvaluation).elements.toList
    }
  }
