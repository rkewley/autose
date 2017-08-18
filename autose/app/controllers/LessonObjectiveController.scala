
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

object LessonObjectiveController extends ControllerTrait[Long, MdlLessonObjective, Long] with Base with OptionalAuthElement {

  val form = Form[MdlLessonObjective](
    mapping (
	"fidLessonObjective" -> optional(of[Long]),
	"fLesson" -> of[Long],
	"fSupportedCourseObjective" -> of[Long],
	"fLessonObjective" -> text
    )(MdlLessonObjective.apply)(MdlLessonObjective.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listLessonObjective(getAll(ffk), ffk)
 
	override def listFunction(item: MdlLessonObjective)(implicit user: MdlUser): Html =
	  views.html.viewlist.listLessonObjective(getAll(item), item.vLesson)
 
	override def showFunction(vLessonObjective: MdlLessonObjective): Html =
	  views.html.viewshow.showLessonObjective(vLessonObjective)
	
	override def editFunction(mdlLessonObjectiveForm: Form[MdlLessonObjective]): Html = 
	  views.html.viewforms.formLessonObjective(mdlLessonObjectiveForm, 0)
	
	override def createFunction(mdlLessonObjectiveForm: Form[MdlLessonObjective]): Html = 
	  views.html.viewforms.formLessonObjective(mdlLessonObjectiveForm, 1)
	  
	def crud = slick.AppDB.dal.LessonObjective

	override def redirect(item: MdlLessonObjective) = routes.LessonObjectiveController.list(item.vLesson)

    def newItem(fkId: Long): MdlLessonObjective = new MdlLessonObjective(Option(0), fkId, 0, "")
    
    override def getAll(fkId: Long): List[MdlLessonObjective] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.LessonObjective.allQuery.filter(v1LessonObjective => v1LessonObjective.vLesson === fkId).elements.toList
    }
    
    override def getAll(vLessonObjective: MdlLessonObjective): List[MdlLessonObjective] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.LessonObjective.allQuery.filter(v1LessonObjective => v1LessonObjective.vLesson === vLessonObjective.vLesson).elements.toList
    }
  }
