
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.json._
import models._
import views._
import slick.AppDB
import persistence._
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object KSAGradedEventController extends ControllerTrait[Long, MdlKSAGradedEvent, Long] with Base with OptionalAuthElement {

  val form = Form[MdlKSAGradedEvent](
    mapping (
	"fidKSAGradedEvent" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fGradedEvent" -> of[Long]
    )(MdlKSAGradedEvent.apply)(MdlKSAGradedEvent.unapply)
  )

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listKSAGradedEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlKSAGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listKSAGradedEvent(getAll(item), item.vGradedEvent)
 
	override def showFunction(vKSAGradedEvent: MdlKSAGradedEvent)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showKSAGradedEvent(vKSAGradedEvent)
	
	override def editFunction(mdlKSAGradedEventForm: Form[MdlKSAGradedEvent]): Html = 
	  views.html.viewforms.formKSAGradedEvent(mdlKSAGradedEventForm, 0)
	
	override def createFunction(mdlKSAGradedEventForm: Form[MdlKSAGradedEvent]): Html = 
	  views.html.viewforms.formKSAGradedEvent(mdlKSAGradedEventForm, 1)
	  
	def crud = slick.AppDB.dal.KSAGradedEvent


    def newItem(fkId: Long): MdlKSAGradedEvent = new MdlKSAGradedEvent(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlKSAGradedEvent] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSAGradedEvent.allQuery.filter(v1KSAGradedEvent => v1KSAGradedEvent.vGradedEvent === fkId).elements.toList
    }
    
    override def getAll(vKSAGradedEvent: MdlKSAGradedEvent): List[MdlKSAGradedEvent] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSAGradedEvent.allQuery.filter(v1KSAGradedEvent => v1KSAGradedEvent.vGradedEvent === vKSAGradedEvent.vGradedEvent).elements.toList
    }
    
	def getGradedEventKSAJsonByTopic(idGradedEvent: Long, idTopic: Long) = Action {
	    val currentlyAssociated = AppDB.dal.KSAGradedEvent.selectByGradedEvent(idGradedEvent).map(_.vKSA)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	}
    
  def saveList = Action { implicit request =>
  	MdlKSAGradedEventList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vKSAGradedEventList => {
        vKSAGradedEventList.getList.foreach (vKSAGradedEvent =>
          if (vKSAGradedEvent.validate) {
            AppDB.dal.KSAGradedEvent.insert(vKSAGradedEvent)
          } else {
            val validationErrors = vKSAGradedEvent.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.KSAGradedEventController.list(vKSAGradedEventList.vGradedEvent))
      })
  }

  }
