
package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import persistence._
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object ProgramOutcomesController extends ControllerTrait[Long, MdlProgramOutcomes, Long] with Base with OptionalAuthElement {

  val form = Form[MdlProgramOutcomes](
    mapping (
	"fProgramOutcomeNumber" -> optional(of[Long]),
	"fProgram" -> of[Long],
	"fProgramOutcome" -> text,
      "fEvaluation" -> of[Long]
    )(MdlProgramOutcomes.apply)(MdlProgramOutcomes.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html = {
	  views.html.viewlist.listProgramOutcomes(getAll(ffk), ffk)
  	}
 
	override def listFunction(item: MdlProgramOutcomes)(implicit user: MdlUser): Html = {
	  views.html.viewlist.listProgramOutcomes(getAll(item), item.vEvaluation)
  	}
 
	override def showFunction(vProgramOutcomes: MdlProgramOutcomes): Html =
	  views.html.viewshow.showProgramOutcomes(vProgramOutcomes)
	
	override def editFunction(mdlProgramOutcomesForm: Form[MdlProgramOutcomes]): Html = 
	  views.html.viewforms.formProgramOutcomes(mdlProgramOutcomesForm, 0)
	
	override def createFunction(mdlProgramOutcomesForm: Form[MdlProgramOutcomes]): Html = 
	  views.html.viewforms.formProgramOutcomes(mdlProgramOutcomesForm, 1)
	  
	def crud = slick.AppDB.dal.ProgramOutcomes

  override def redirect(item: MdlProgramOutcomes) = routes.PerformanceIndicatorController.list(item.vEvaluation)

  def newItem(fkId: Long): MdlProgramOutcomes = {
	  println("Creating a new ProgramOutcome with Evaluation index: " + fkId)
	  new MdlProgramOutcomes(Some(0), 0, "", fkId)
	}
    override def getAll(fkId: Long): List[MdlProgramOutcomes] = AppDB.database.withSession { implicit session: Session =>
	    AppDB.dal.ProgramOutcomes.allQuery.filter(po => po.vEvaluation === fkId).elements.toList
	}

    override def getAll(vProgramOutcomes: MdlProgramOutcomes): List[MdlProgramOutcomes] = AppDB.database.withSession { implicit session: Session =>
	    AppDB.dal.ProgramOutcomes.allQuery.filter(po => po.vEvaluation === vProgramOutcomes.vEvaluation).elements.toList
	}
    
    def ksaForOutcomeAndCourse(outcomeId: Long, courseId: Long): Long = {
      // find all the KSA for the program by iterating throught the performance indicators and adding distinct KSA to the list
      val programKsa = AppDB.dal.PerformanceIndicator.selectByProgramOutcome(outcomeId).flatMap { pi =>
        AppDB.dal.KSAPerfIndicator.selectByPerfIndicator(pi.vidPerformanceIndicator.get)
      }.map(_.vKSA)
      val distinctProgramKsa = programKsa.distinct
      

      courseKSA(courseId).distinct.filter(ksa => distinctProgramKsa.exists(pKsa => ksa == pKsa)).length
    }
    
    // find all the KSA for the course by iterating trhough the lessons and adding distinct KSA to the list
    def courseKSA(courseId: Long): List[Long] = {
      AppDB.dal.Lessons.lessonsForCourse(courseId).flatMap { lesson =>
        val cksa = AppDB.dal.LessonKSA.selectByLesson(lesson.vLessonIndex.get).map(_.vTopicObjective)
        cksa.distinct
      }
    }
    
    def courseTopics(courseId: Long): List[MdlTopics] = {
      val courseKsa = courseKSA(courseId)
      SqlTopics.all.filter( topic =>
        AppDB.dal.KSA.selectByTopic(topic.vTopicsIndex).exists(ksa => courseKsa.contains(ksa.vTopicObjectiveNumber.get))
      )
    }
}