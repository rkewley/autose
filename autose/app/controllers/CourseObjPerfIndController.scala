
package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.{MdlCourseObjectives, _}
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.Action
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._

object CourseObjPerfIndController extends ControllerTrait[Long, MdlCourseObjPerfInd, Long] with Base with OptionalAuthElement {

  val form = Form[MdlCourseObjPerfInd](
    mapping (
	"fidCourseObjPerfInd" -> optional(of[Long]),
	"fCourseObjective" -> of[Long],
	"fPerformanceIndicator" -> of[Long]
    )(MdlCourseObjPerfInd.apply)(MdlCourseObjPerfInd.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listCourseObjPerfInd(getAll(ffk), ffk)
 
	override def listFunction(item: MdlCourseObjPerfInd)(implicit user: MdlUser): Html =
	  views.html.viewlist.listCourseObjPerfInd(getAll(item), item.vPerformanceIndicator)
 
	override def showFunction(vCourseObjPerfInd: MdlCourseObjPerfInd): Html =
	  views.html.viewshow.showCourseObjPerfInd(vCourseObjPerfInd)
	
	override def editFunction(mdlCourseObjPerfIndForm: Form[MdlCourseObjPerfInd]): Html = 
	  views.html.viewforms.formCourseObjPerfInd(mdlCourseObjPerfIndForm, 0)
	
	override def createFunction(mdlCourseObjPerfIndForm: Form[MdlCourseObjPerfInd]): Html = 
	  views.html.viewforms.formCourseObjPerfInd(mdlCourseObjPerfIndForm, 1)
	  
	def crud = slick.AppDB.dal.CourseObjPerfInd

	override def redirect(item: MdlCourseObjPerfInd) = routes.CourseObjPerfIndController.list(item.vPerformanceIndicator)


	def newItem(fkId: Long): MdlCourseObjPerfInd = new MdlCourseObjPerfInd(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlCourseObjPerfInd] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.CourseObjPerfInd.allQuery.filter(v1CourseObjPerfInd => v1CourseObjPerfInd.vPerformanceIndicator === fkId).elements.toList
    }
    
    override def getAll(vCourseObjPerfInd: MdlCourseObjPerfInd): List[MdlCourseObjPerfInd] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.CourseObjPerfInd.allQuery.filter(v1CourseObjPerfInd => v1CourseObjPerfInd.vPerformanceIndicator === vCourseObjPerfInd.vPerformanceIndicator).elements.toList
    }

	def getCourseObjJsonByCourse(idPerfIndicator: Long, idCourse: Long) = Action {
		AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
			val currentlyAssociated = AppDB.dal.CourseObjPerfInd.selectByPerfIndicator(idPerfIndicator).map(_.vCourseObjective)
			println(currentlyAssociated.toString)
			val courseObj1: List[MdlCourseObjectives] = slick.AppDB.dal.CourseObjectives.courseObjectivesForCourse(idCourse)
			val courseObj: List[MdlCourseObjectives] = courseObj1.filter(co => !currentlyAssociated.contains(co.vObjectiveID.get))
			val resultJson = JsObject(courseObj.map(co =>
				co.vObjectiveID.get.toString -> JsString(co.vObjective)))
			Ok(resultJson)
		}
	}
  }
