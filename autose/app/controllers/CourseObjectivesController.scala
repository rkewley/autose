
package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.{MdlCourseObjectives, _}
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object CourseObjectivesController extends ControllerTrait[Long, MdlCourseObjectives, Long] with Base with OptionalAuthElement {

  val form = Form[MdlCourseObjectives](
    mapping (
	"fObjectiveID" -> optional(of[Long]),
	"fCourseIDNumber" -> of[Long],
	"fObjective" -> text
    )(MdlCourseObjectives.apply)(MdlCourseObjectives.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listCourseObjectives(getAll(ffk), ffk)
 
	override def listFunction(item: MdlCourseObjectives)(implicit user: MdlUser): Html =
	  views.html.viewlist.listCourseObjectives(getAll(item), item.vCourseIDNumber)
 
	override def showFunction(vCourseObjectives: MdlCourseObjectives): Html =
	  views.html.viewshow.showCourseObjectives(vCourseObjectives)
	
	override def editFunction(mdlCourseObjectivesForm: Form[MdlCourseObjectives]): Html = 
	  views.html.viewforms.formCourseObjectives(mdlCourseObjectivesForm, 0)
	
	override def createFunction(mdlCourseObjectivesForm: Form[MdlCourseObjectives]): Html = 
	  views.html.viewforms.formCourseObjectives(mdlCourseObjectivesForm, 1)

	override def redirect(item: MdlCourseObjectives) = routes.CourseObjectivesController.list(item.vCourseIDNumber)
	  
	def crud = slick.AppDB.dal.CourseObjectives


    def newItem(fkId: Long): MdlCourseObjectives = new MdlCourseObjectives(Option(0), fkId, "")
    
    override def getAll(fkId: Long): List[MdlCourseObjectives] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.CourseObjectives.allQuery.filter(v1CourseObjectives => v1CourseObjectives.vCourseIDNumber === fkId).elements.toList
    }
    
    override def getAll(vCourseObjectives: MdlCourseObjectives): List[MdlCourseObjectives] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.CourseObjectives.allQuery.filter(v1CourseObjectives => v1CourseObjectives.vCourseIDNumber === vCourseObjectives.vCourseIDNumber).elements.toList
    }
  }
