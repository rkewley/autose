
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._
import play.api.libs.json._


object PerfIndKSASubEventAMSController extends ControllerTrait[Long, MdlPerfIndKSASubEventAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlPerfIndKSASubEventAMS](
    mapping (
	"fidPerfIndKSASubEventAMS" -> optional(of[Long]),
	"fPerformanceIndicator" -> of[Long],
	"fKSASubEventAMS" -> of[Long]
    )(MdlPerfIndKSASubEventAMS.apply)(MdlPerfIndKSASubEventAMS.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPerfIndKSASubEventAMS(getAll(ffk), ffk)
 
	override def listFunction(item: MdlPerfIndKSASubEventAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPerfIndKSASubEventAMS(getAll(item), item.vPerformanceIndicator)
 
	override def showFunction(vPerfIndKSASubEventAMS: MdlPerfIndKSASubEventAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showPerfIndKSASubEventAMS(vPerfIndKSASubEventAMS)
	
	override def editFunction(mdlPerfIndKSASubEventAMSForm: Form[MdlPerfIndKSASubEventAMS]): Html = 
	  views.html.viewforms.formPerfIndKSASubEventAMS(mdlPerfIndKSASubEventAMSForm, 0, 0)
	
	override def createFunction(mdlPerfIndKSASubEventAMSForm: Form[MdlPerfIndKSASubEventAMS]): Html = 
	  views.html.viewforms.formPerfIndKSASubEventAMS(mdlPerfIndKSASubEventAMSForm, 1, 0)
	  
	def crud = slick.AppDB.dal.PerfIndKSASubEventAMS

	def create(perfIndId: Long, ksaId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    	Ok(views.html.viewforms.formPerfIndKSASubEventAMS(form.fill(newItem(perfIndId)), 0, ksaId))
  	}
	

    def newItem(fkId: Long): MdlPerfIndKSASubEventAMS = new MdlPerfIndKSASubEventAMS(Option(0), fkId, 0)
    
    override def getAll(fkId: Long): List[MdlPerfIndKSASubEventAMS] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PerfIndKSASubEventAMS.allQuery.filter(v1PerfIndKSASubEventAMS => v1PerfIndKSASubEventAMS.vPerformanceIndicator === fkId).elements.toList
    }
    
    override def getAll(vPerfIndKSASubEventAMS: MdlPerfIndKSASubEventAMS): List[MdlPerfIndKSASubEventAMS] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PerfIndKSASubEventAMS.allQuery.filter(v1PerfIndKSASubEventAMS => v1PerfIndKSASubEventAMS.vPerformanceIndicator === vPerfIndKSASubEventAMS.vPerformanceIndicator).elements.toList
    }
    
	def getKSASubEventJsonByKSA(idKSA: Long) = Action {
		val selectvKSASubEventAMS = AppDB.dal.KSASubEventAMS.selectByKSA(idKSA).map(vKSASubEventAMS => vKSASubEventAMS.vidKSASubEventAMS.get.toString -> AppDB.dal.SubEventAMS.getSelectionString(vKSASubEventAMS.vSubEventAMS))
	    val resultJson = JsObject(selectvKSASubEventAMS.map(ksaSubEvent =>
	        ksaSubEvent._1 -> JsString(ksaSubEvent._2)))
	    //println("Returning JSON: " + resultJson)    
	    Ok(resultJson)
	}

   override def delete(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[MdlPerfIndKSASubEventAMS] =>
          crud.delete(id)
          implicit val userOption = Some(user)
          Redirect(routes.KSAPerfIndicatorController.list(item.get.vPerformanceIndicator))
        case None =>
          badRequest("Programs with key " + id + " not found in database", request)
      }
  }
   
  def saveList = Action { implicit request =>
  	MdlPerfIndKSASubEventAMSList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vPerfIndKSASubEventAMSList => {
        vPerfIndKSASubEventAMSList.getList.foreach (vPerfIndKSASubEventAMS =>
          if (vPerfIndKSASubEventAMS.validate) {
            //println(vPerfIndKSASubEventAMS)
            AppDB.dal.PerfIndKSASubEventAMS.insert(vPerfIndKSASubEventAMS)
          } else {
            val validationErrors = vPerfIndKSASubEventAMS.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.KSAPerfIndicatorController.list(vPerfIndKSASubEventAMSList.vPerformanceIndicator))
      })
  }

    
  }
