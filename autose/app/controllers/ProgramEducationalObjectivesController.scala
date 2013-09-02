
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

object ProgramEducationalObjectivesController extends ControllerTrait[Long, MdlProgramEducationalObjectives, Long] with Base with OptionalAuthElement {

  val form = Form[MdlProgramEducationalObjectives](
    mapping (
	"fProgObjNumber" -> optional(of[Long]),
	"fProgram" -> of[Long],
	"fProgramEducationalObjective" -> text
    )(MdlProgramEducationalObjectives.apply)(MdlProgramEducationalObjectives.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listProgramEducationalObjectives(getAll(ffk), ffk)
 
	override def listFunction(item: MdlProgramEducationalObjectives)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listProgramEducationalObjectives(getAll(item), item.vProgram)
 
	override def showFunction(vProgramEducationalObjectives: MdlProgramEducationalObjectives)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showProgramEducationalObjectives(vProgramEducationalObjectives)
	
	override def editFunction(mdlProgramEducationalObjectivesForm: Form[MdlProgramEducationalObjectives]): Html = 
	  views.html.viewforms.formProgramEducationalObjectives(mdlProgramEducationalObjectivesForm, 0)
	
	override def createFunction(mdlProgramEducationalObjectivesForm: Form[MdlProgramEducationalObjectives]): Html = 
	  views.html.viewforms.formProgramEducationalObjectives(mdlProgramEducationalObjectivesForm, 1)
	  
	def crud = slick.AppDB.dal.ProgramEducationalObjectives


    def newItem(fkId: Long): MdlProgramEducationalObjectives = new MdlProgramEducationalObjectives(Option(0), fkId, "")
    
    override def getAll(fkId: Long): List[MdlProgramEducationalObjectives] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ProgramEducationalObjectives.allQuery.filter(v1ProgramEducationalObjectives => v1ProgramEducationalObjectives.vProgram === fkId).elements.toList
    }
    
    override def getAll(vProgramEducationalObjectives: MdlProgramEducationalObjectives): List[MdlProgramEducationalObjectives] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ProgramEducationalObjectives.allQuery.filter(v1ProgramEducationalObjectives => v1ProgramEducationalObjectives.vProgram === vProgramEducationalObjectives.vProgram).elements.toList
    }
  }
