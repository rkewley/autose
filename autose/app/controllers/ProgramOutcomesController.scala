
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

object ProgramOutcomesController extends ControllerTrait[Long, MdlProgramOutcomes, Long] with Base {

  val form = Form[MdlProgramOutcomes](
    mapping (
	"fProgramOutcomeNumber" -> optional(of[Long]),
	"fProgram" -> of[Long],
	"fProgramOutcome" -> text
    )(MdlProgramOutcomes.apply)(MdlProgramOutcomes.unapply)
  )
      

	override def listFunction(listMdlProgramOutcomes: List[MdlProgramOutcomes]): Html = {
	  val idPrograms = listMdlProgramOutcomes.headOption match {
	  	case Some(vMdlProgramOutcomes) => vMdlProgramOutcomes.vProgram
	  	case None => 0
  	  }
	  views.html.viewlist.listProgramOutcomes(listMdlProgramOutcomes, 0)
  	}
 
	override def showFunction(vProgramOutcomes: MdlProgramOutcomes): Html = 
	  views.html.viewshow.showProgramOutcomes(vProgramOutcomes)
	
	override def editFunction(mdlProgramOutcomesForm: Form[MdlProgramOutcomes]): Html = 
	  views.html.viewforms.formProgramOutcomes(mdlProgramOutcomesForm, 0)
	
	override def createFunction(mdlProgramOutcomesForm: Form[MdlProgramOutcomes]): Html = 
	  views.html.viewforms.formProgramOutcomes(mdlProgramOutcomesForm, 1)
	  
	def crud = slick.AppDB.dal.ProgramOutcomes


    def newItem(fkId: Long): MdlProgramOutcomes = new MdlProgramOutcomes(Some(0), fkId, "")
    override def getAll(fkId: Long): List[MdlProgramOutcomes] = AppDB.database.withSession { implicit session: Session =>
	    AppDB.dal.ProgramOutcomes.allQuery.filter(po => po.vProgram === fkId).elements.toList
	}

    override def getAll(vProgramOutcomes: MdlProgramOutcomes): List[MdlProgramOutcomes] = AppDB.database.withSession { implicit session: Session =>
	    AppDB.dal.ProgramOutcomes.allQuery.filter(po => po.vProgram === vProgramOutcomes.vProgram).elements.toList
	}
}