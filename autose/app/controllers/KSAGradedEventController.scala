
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
import scala.slick.driver.MySQLDriver.simple._

object KSAGradedEventController extends ControllerTrait[Long, MdlKSAGradedEvent, Long] with Base {

  val form = Form[MdlKSAGradedEvent](
    mapping (
	"fidKSAGradedEvent" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fGradedEvent" -> of[Long]
    )(MdlKSAGradedEvent.apply)(MdlKSAGradedEvent.unapply)
  )

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listKSAGradedEvent(getAll(ffk), ffk)
 
	override def listFunction(item: MdlKSAGradedEvent): Html = 
	  views.html.viewlist.listKSAGradedEvent(getAll(item), item.vGradedEvent)
 
	override def showFunction(vKSAGradedEvent: MdlKSAGradedEvent): Html = 
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
