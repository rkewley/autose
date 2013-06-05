
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

object GradedRequirementsController extends ControllerTrait[Long, MdlGradedRequirements, Long] with Base {

  val form = Form[MdlGradedRequirements](
    mapping (
	"fGradedEventIndex" -> optional(of[Long]),
	"fCourse" -> of[Long],
	"fGradedEventName" -> text,
	"fEventDescription" -> text,
	"fTypeOfEvent" -> of[Long],
	"fPoints" -> of[Double],
	"fLessonassigned" -> of[Long],
	"fLessoncompleted" -> of[Long]
    )(MdlGradedRequirements.apply)(MdlGradedRequirements.unapply)
  )
      

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listGradedRequirements(getAll(ffk), ffk)
 
	override def listFunction(item: MdlGradedRequirements): Html = 
	  views.html.viewlist.listGradedRequirements(getAll(item), item.vCourse)
 
	override def showFunction(vGradedRequirements: MdlGradedRequirements): Html = 
	  views.html.viewshow.showGradedRequirements(vGradedRequirements)
	
	override def editFunction(mdlGradedRequirementsForm: Form[MdlGradedRequirements]): Html = 
	  views.html.viewforms.formGradedRequirements(mdlGradedRequirementsForm, 0)
	
	override def createFunction(mdlGradedRequirementsForm: Form[MdlGradedRequirements]): Html = 
	  views.html.viewforms.formGradedRequirements(mdlGradedRequirementsForm, 1)
	  
	def crud = slick.AppDB.dal.GradedRequirements


    def newItem(fkId: Long): MdlGradedRequirements = new MdlGradedRequirements(Option(0), fkId, "", "", 0, 0.0, 0, 0)
    
    override def getAll(fkId: Long): List[MdlGradedRequirements] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradedRequirements.allQuery.filter(v1GradedRequirements => v1GradedRequirements.vCourse === fkId).elements.toList
    }
    
    override def getAll(vGradedRequirements: MdlGradedRequirements): List[MdlGradedRequirements] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.GradedRequirements.allQuery.filter(v1GradedRequirements => v1GradedRequirements.vCourse === vGradedRequirements.vCourse).elements.toList
    }
  }
