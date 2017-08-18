
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB.dal._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json._
import persistence._
import jp.t2v.lab.play2.auth._

object KSAPerfIndicatorController extends ControllerTrait[Long, MdlKSAPerfIndicator, Long] with Base with OptionalAuthElement {

  val form = Form[MdlKSAPerfIndicator](
    mapping (
	"fidKSAPerfIndicator" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fPerformanceIndicator" -> of[Long]
    )(MdlKSAPerfIndicator.apply)(MdlKSAPerfIndicator.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listKSAPerfIndicator(getAll(ffk), ffk)
 
	override def listFunction(item: MdlKSAPerfIndicator)(implicit user: MdlUser): Html =
	  views.html.viewlist.listKSAPerfIndicator(getAll(item), item.vPerformanceIndicator)
 
	override def showFunction(vKSAPerfIndicator: MdlKSAPerfIndicator): Html =
	  views.html.viewshow.showKSAPerfIndicator(vKSAPerfIndicator)
	
	override def editFunction(mdlKSAPerfIndicatorForm: Form[MdlKSAPerfIndicator]): Html = 
	  views.html.viewforms.formKSAPerfIndicator(mdlKSAPerfIndicatorForm, 0)
	
	override def createFunction(mdlKSAPerfIndicatorForm: Form[MdlKSAPerfIndicator]): Html = 
	  views.html.viewforms.formKSAPerfIndicator(mdlKSAPerfIndicatorForm, 1)
	  
	def crud = slick.AppDB.dal.KSAPerfIndicator

	
    def newItem(fkId: Long): MdlKSAPerfIndicator = new MdlKSAPerfIndicator(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlKSAPerfIndicator] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSAPerfIndicator.allQuery.filter(v1KSAPerfIndicator => v1KSAPerfIndicator.vPerformanceIndicator === fkId).elements.toList
    }
    
    override def getAll(vKSAPerfIndicator: MdlKSAPerfIndicator): List[MdlKSAPerfIndicator] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSAPerfIndicator.allQuery.filter(v1KSAPerfIndicator => v1KSAPerfIndicator.vPerformanceIndicator === vKSAPerfIndicator.vPerformanceIndicator).elements.toList
    }
    
	def getKSAJsonByTopic(idPerfIndicator: Long, idTopic: Long) = Action {
	  AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
	    val currentlyAssociated = AppDB.dal.KSAPerfIndicator.selectByPerfIndicator(idPerfIndicator).map(_.vKSA)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	  }
	}
    
    
  def saveList = Action { implicit request =>
  	MdlKSAPerfIndicatorList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vKSAPerfIndicatorList => {
        vKSAPerfIndicatorList.getList.foreach (vKSAPerfIndicator =>
          if (vKSAPerfIndicator.validate) {
            AppDB.dal.KSAPerfIndicator.insert(vKSAPerfIndicator)
          } else {
            val validationErrors = vKSAPerfIndicator.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.KSAPerfIndicatorController.list(vKSAPerfIndicatorList.vPerformanceIndicator))
      })
  }

  }
