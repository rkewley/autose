
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
import slick.AppDB.dal._
import scala.slick.driver.MySQLDriver.simple._
import play.api.libs.json._

object PerfIndProgramOutcomesController extends ControllerTrait[Long, MdlPerfIndProgramOutcomes, Long] with Base {

  val form = Form[MdlPerfIndProgramOutcomes](
    mapping (
	"fidPerfIndProgramOutcomes" -> optional(of[Long]),
	"fPerformanceIndicator" -> of[Long],
	"fProgramOutcome" -> of[Long]
    )(MdlPerfIndProgramOutcomes.apply)(MdlPerfIndProgramOutcomes.unapply)
  )
      

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listPerfIndProgramOutcomes(getAll(ffk), ffk)
 
	override def listFunction(item: MdlPerfIndProgramOutcomes): Html = 
	  views.html.viewlist.listPerfIndProgramOutcomes(getAll(item), item.vProgramOutcome)
 
	override def showFunction(vPerfIndProgramOutcomes: MdlPerfIndProgramOutcomes): Html = 
	  views.html.viewshow.showPerfIndProgramOutcomes(vPerfIndProgramOutcomes)
	
	override def editFunction(mdlPerfIndProgramOutcomesForm: Form[MdlPerfIndProgramOutcomes]): Html = 
	  views.html.viewforms.formPerfIndProgramOutcomes(mdlPerfIndProgramOutcomesForm, 0)
	
	override def createFunction(mdlPerfIndProgramOutcomesForm: Form[MdlPerfIndProgramOutcomes]): Html = 
	  views.html.viewforms.formPerfIndProgramOutcomes(mdlPerfIndProgramOutcomesForm, 1)
	  
	def getPerformanceIndicatorsJsonByProgramAndOutcome(idProgram: Long, idProgramOutcome: Long) = Action {
	  AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
	    val currentlyAssociated = AppDB.dal.PerfIndProgramOutcomes.selectByProgramOutcome(idProgramOutcome).map(_.vPerformanceIndicator)
	    val performanceIndicators = AppDB.dal.PerformanceIndicator.selectByProgram(idProgram).filter(pi => !currentlyAssociated.contains(pi.vidTerminalLearningObjective.get))
	    val resultJson = JsObject(performanceIndicators.map(pi =>
	        pi.vidTerminalLearningObjective.get.toString -> JsString(pi.vTerminalLearningObjective)))
	    Ok(resultJson)
	  }
	}
	  
	def crud = slick.AppDB.dal.PerfIndProgramOutcomes


    def newItem(fkId: Long): MdlPerfIndProgramOutcomes = new MdlPerfIndProgramOutcomes(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlPerfIndProgramOutcomes] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PerfIndProgramOutcomes.allQuery.filter(v1PerfIndProgramOutcomes => v1PerfIndProgramOutcomes.vProgramOutcome === fkId).elements.toList
    }
    
    override def getAll(vPerfIndProgramOutcomes: MdlPerfIndProgramOutcomes): List[MdlPerfIndProgramOutcomes] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.PerfIndProgramOutcomes.allQuery.filter(v1PerfIndProgramOutcomes => v1PerfIndProgramOutcomes.vProgramOutcome === vPerfIndProgramOutcomes.vProgramOutcome).elements.toList
    }
    
  def saveList = Action { implicit request =>
    Logger.debug("saving")
  	MdlPerfIndProgramOutcomesList.formList.bindFromRequest.fold(
  	  form => {
  	    println(form.data.toString)
  	    println(form.toString)
        val errorMessage = formErrorMessage(form.errors)
        println("errors in form")
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vPerfIndProgramOutcomesList => {
        println("number items is " + vPerfIndProgramOutcomesList.getList.size)
        vPerfIndProgramOutcomesList.getList.foreach (vPerfIndProgramOutcomes =>
          if (vPerfIndProgramOutcomes.validate) {
            println("inserting event")
            AppDB.dal.PerfIndProgramOutcomes.insert(vPerfIndProgramOutcomes)
          } else {
            val validationErrors = vPerfIndProgramOutcomes.validationErrors
            println("validation errors")
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.PerfIndProgramOutcomesController.list(vPerfIndProgramOutcomesList.vProgramOutcome))
      })
  }

  }
