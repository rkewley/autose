
package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object PEOtoSLOController extends ControllerTrait[Long, MdlPEOtoSLO, Long] with Base with OptionalAuthElement {

  val form = Form[MdlPEOtoSLO](
    mapping (
	"fidPEOtoSLO" -> optional(of[Long]),
	"fPEO" -> of[Long],
	"fSLO" -> of[Long]
    )(MdlPEOtoSLO.apply)(MdlPEOtoSLO.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listPEOtoSLO(getAll(ffk), ffk)
 
	override def listFunction(item: MdlPEOtoSLO)(implicit user: MdlUser): Html =
	  views.html.viewlist.listPEOtoSLO(getAll(item), item.vPEO)
 
	override def showFunction(vPEOtoSLO: MdlPEOtoSLO): Html =
	  views.html.viewshow.showPEOtoSLO(vPEOtoSLO)
	
	override def editFunction(mdlPEOtoSLOForm: Form[MdlPEOtoSLO]): Html = 
	  views.html.viewforms.formPEOtoSLO(mdlPEOtoSLOForm, 0)
	
	override def createFunction(mdlPEOtoSLOForm: Form[MdlPEOtoSLO]): Html = 
	  views.html.viewforms.formPEOtoSLO(mdlPEOtoSLOForm, 1)
	  
	def crud = slick.AppDB.dal.PEOtoSLO


    def newItem(fkId: Long): MdlPEOtoSLO = new MdlPEOtoSLO(Option(0), fkId, 0)
    
    override def getAll(fkId: Long): List[MdlPEOtoSLO] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PEOtoSLO.allQuery.filter(v1PEOtoSLO => v1PEOtoSLO.vPEO === fkId).elements.toList
    }
    
    override def getAll(vPEOtoSLO: MdlPEOtoSLO): List[MdlPEOtoSLO] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PEOtoSLO.allQuery.filter(v1PEOtoSLO => v1PEOtoSLO.vPEO === vPEOtoSLO.vPEO).elements.toList
    }
    
	def getSLOJsonByPEOandProgram(idPEO: Long, idProgram: Long) = Action {
	  AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
	    val currentlyAssociated = AppDB.dal.PEOtoSLO.selectByPEO(idPEO).map(_.vSLO)
	    val slo1 = AppDB.dal.ProgramOutcomes.selectByProgram(idProgram)
	    val slo = slo1.filter(outcome => !currentlyAssociated.contains(outcome.vProgramOutcomeNumber.get))
	    val resultJson = JsObject(slo.map(slo =>
	        slo.vProgramOutcomeNumber.get.toString -> JsString(slo.vProgramOutcome)))
	    Ok(resultJson)
	  }
	}
    
    
  def saveList = Action { implicit request =>
  	MdlPEOtoSLOList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug("There was a form error: " + errorMessage)
        println(form.toString)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vPEOtoSLOList => {
        vPEOtoSLOList.getList.foreach (vPEOtoSLO =>
          if (vPEOtoSLO.validate) {
            AppDB.dal.PEOtoSLO.insert(vPEOtoSLO)
          } else {
            val validationErrors = vPEOtoSLO.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.PEOtoSLOController.list(vPEOtoSLOList.vPEO))
      })
  }
    
  }
