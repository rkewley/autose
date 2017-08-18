
package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._

object EvaluationController extends ControllerTrait[Long, MdlEvaluation, Long] with Base with OptionalAuthElement {

  val form = Form[MdlEvaluation](
    mapping (
	"fidEvaluation" -> optional(of[Long]),
	"fName" -> text,
	"fProgram" -> of[Long],
	"fYear" -> of[Long]
    )(MdlEvaluation.apply)(MdlEvaluation.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listEvaluation(getAll(ffk), ffk)
 
	override def listFunction(item: MdlEvaluation)(implicit user: MdlUser): Html =
	  views.html.viewlist.listEvaluation(getAll(item), item.vProgram)
 
	override def showFunction(vEvaluation: MdlEvaluation): Html =
	  views.html.viewshow.showEvaluation(vEvaluation)
	
	override def editFunction(mdlEvaluationForm: Form[MdlEvaluation]): Html = 
	  views.html.viewforms.formEvaluation(mdlEvaluationForm, 0)
	
	override def createFunction(mdlEvaluationForm: Form[MdlEvaluation]): Html = 
	  views.html.viewforms.formEvaluation(mdlEvaluationForm, 1)
	  
	def crud = slick.AppDB.dal.Evaluation




    def newItem(fkId: Long): MdlEvaluation = new MdlEvaluation(Option(0), "", fkId, 0)
    
    override def getAll(fkId: Long): List[MdlEvaluation] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.Evaluation.allQuery.filter(v1Evaluation => v1Evaluation.vProgram === fkId).elements.toList
    }
    
    override def getAll(vEvaluation: MdlEvaluation): List[MdlEvaluation] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.Evaluation.allQuery.filter(v1Evaluation => v1Evaluation.vProgram === vEvaluation.vProgram).elements.toList
    }
  }
