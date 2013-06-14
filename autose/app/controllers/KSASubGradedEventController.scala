
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import persistence._
import play.api.libs.json._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._

object KSASubGradedEventController extends ControllerTrait[Long, MdlKSASubGradedEvent, Long] with Base {

  val form = Form[MdlKSASubGradedEvent](
    mapping (
	"fidKSASubGradedEvent" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fSubGradedEvent" -> of[Long]
    )(MdlKSASubGradedEvent.apply)(MdlKSASubGradedEvent.unapply)
  )
      

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listKSASubGradedEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlKSASubGradedEvent): Html = 
	  views.html.viewlist.listKSASubGradedEvent(getAll(item), item.vSubGradedEvent)
 
	override def showFunction(vKSASubGradedEvent: MdlKSASubGradedEvent): Html = 
	  views.html.viewshow.showKSASubGradedEvent(vKSASubGradedEvent)
	
	override def editFunction(mdlKSASubGradedEventForm: Form[MdlKSASubGradedEvent]): Html = 
	  views.html.viewforms.formKSASubGradedEvent(mdlKSASubGradedEventForm, 0)
	
	override def createFunction(mdlKSASubGradedEventForm: Form[MdlKSASubGradedEvent]): Html = 
	  views.html.viewforms.formKSASubGradedEvent(mdlKSASubGradedEventForm, 1)
	  
	def getSubGradedEventKSAJsonByTopic(idSubGradedEvent: Long, idTopic: Long) = Action {
	    val currentlyAssociated = AppDB.dal.KSASubGradedEvent.selectBySubGradedEvent(idSubGradedEvent).map(_.vKSA)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	}
    
	def crud = slick.AppDB.dal.KSASubGradedEvent


    def newItem(fkId: Long): MdlKSASubGradedEvent = new MdlKSASubGradedEvent(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlKSASubGradedEvent] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSASubGradedEvent.allQuery.filter(v1KSASubGradedEvent => v1KSASubGradedEvent.vSubGradedEvent === fkId).elements.toList
    }
    
    override def getAll(vKSASubGradedEvent: MdlKSASubGradedEvent): List[MdlKSASubGradedEvent] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSASubGradedEvent.allQuery.filter(v1KSASubGradedEvent => v1KSASubGradedEvent.vSubGradedEvent === vKSASubGradedEvent.vSubGradedEvent).elements.toList
    }
    
  def saveList = Action { implicit request =>
    Logger.debug("saving")
  	MdlKSASubGradedEventList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        println("errors in form")
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vKSASubGradedEventList => {
        println("number items is " + vKSASubGradedEventList.getList.size)
        vKSASubGradedEventList.getList.foreach (vKSASubGradedEvent =>
          if (vKSASubGradedEvent.validate) {
            println("inserting event")
            AppDB.dal.KSASubGradedEvent.insert(vKSASubGradedEvent)
          } else {
            val validationErrors = vKSASubGradedEvent.validationErrors
            println("validation errors")
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.KSASubGradedEventController.list(vKSASubGradedEventList.vSubGradedEvent))
      })
  }

  }
