
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

object SubGradedEventController extends ControllerTrait[Long, MdlSubGradedEvent, Long] with Base with OptionalAuthElement {

  val form = Form[MdlSubGradedEvent](
    mapping (
	"fidSubGradedEvent" -> optional(of[Long]),
	"fGradedEvent" -> of[Long],
	"fDescription" -> text,
	"fPoints" -> of[Double]
    )(MdlSubGradedEvent.apply)(MdlSubGradedEvent.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listSubGradedEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlSubGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listSubGradedEvent(getAll(item), item.vGradedEvent)
 
	override def showFunction(vSubGradedEvent: MdlSubGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showSubGradedEvent(vSubGradedEvent)
	
	override def editFunction(mdlSubGradedEventForm: Form[MdlSubGradedEvent]): Html = 
	  views.html.viewforms.formSubGradedEvent(mdlSubGradedEventForm, 0)
	
	override def createFunction(mdlSubGradedEventForm: Form[MdlSubGradedEvent]): Html = 
	  views.html.viewforms.formSubGradedEvent(mdlSubGradedEventForm, 1)
	  
	def crud = slick.AppDB.dal.SubGradedEvent


    def newItem(fkId: Long): MdlSubGradedEvent = new MdlSubGradedEvent(Option(0), fkId, "", 0.0)
    
    override def getAll(fkId: Long): List[MdlSubGradedEvent] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubGradedEvent.allQuery.filter(v1SubGradedEvent => v1SubGradedEvent.vGradedEvent === fkId).elements.toList
    }
    
    override def getAll(vSubGradedEvent: MdlSubGradedEvent): List[MdlSubGradedEvent] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubGradedEvent.allQuery.filter(v1SubGradedEvent => v1SubGradedEvent.vGradedEvent === vSubGradedEvent.vGradedEvent).elements.toList
    }
  }
