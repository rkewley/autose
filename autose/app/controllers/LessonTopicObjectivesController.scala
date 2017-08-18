
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import persistence._
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.data.format.Formats._

object LessonTopicObjectivesController extends Base {

  val formLessonTopicObjectives = Form[MdlLessonTopicObjectives](
    mapping (
	"fidLessonTopicObjectives" -> of[Long],
	"fLesson" -> of[Long],
	"fTopicObjective" -> of[Long]
    )(MdlLessonTopicObjectives.apply)(MdlLessonTopicObjectives.unapply)
  )
      
  val formLessonTopicObjectivesList = Form[MdlLessonTopicObjectivesList](
    mapping (
	"fidLessonTopicObjectives" -> of[Long],
	"fLesson" -> of[Long],
	"fTopicObjectives" -> list(of[Long])
    )(MdlLessonTopicObjectivesList.apply)(MdlLessonTopicObjectivesList.unapply)
  )

  def listLessonTopicObjectives(idLessons: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
     Ok(viewlist.html.listLessonTopicObjectives(SqlLessonTopicObjectives.selectWhere("`Lesson` = " + idLessons), idLessons))
  }

   //def editLessonTopicObjectives(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
   // Ok(viewforms.html.formLessonTopicObjectives(formLessonTopicObjectives.fill(SqlLessonTopicObjectives.select(id)), 0)).withHeaders(CACHE_CONTROL -> "no-chache")
  //}
   
   def editLessonTopicObjectives(id: Long) = compositeAction(Faculty) {implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formLessonTopicObjectives(formLessonTopicObjectives.fill(SqlLessonTopicObjectives.select(id)), 0))
  }
    
   def showLessonTopicObjectives(id: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewshow.html.showLessonTopicObjectives(SqlLessonTopicObjectives.select(id)))
  }

   def deleteLessonTopicObjectives(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vLessonTopicObjectives = SqlLessonTopicObjectives.select(id)
    SqlLessonTopicObjectives.delete(id)
    Redirect(routes.LessonTopicObjectivesController.listLessonTopicObjectives(vLessonTopicObjectives.vLesson))
  }

  def createLessonTopicObjectives(idLessons: Long) = compositeAction(Faculty) {implicit user => implicit template => implicit request =>
    val vLessonTopicObjectives = new MdlLessonTopicObjectives(0, idLessons, 0)
    Ok(viewforms.html.formLessonTopicObjectives(formLessonTopicObjectives.fill(vLessonTopicObjectives), 1))
  }
  
  def createLessonTopicObjectivesList(idLessons: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vLessonTopicObjectivesList = new MdlLessonTopicObjectivesList(0, idLessons, List())
    Ok(viewforms.html.formLessonTopicObjectivesList(formLessonTopicObjectivesList.fill(vLessonTopicObjectivesList)))
  }
  
  def getTopicObjectivesJson(id: Long) = Action {
    val topicObjectives = SqlTopicObjectives.selectWhere("`Topic` = " + id)
    val resultJson = JsObject(topicObjectives.map(topicObjective =>
          topicObjective.vTopicObjectiveNumber.toString -> JsString(topicObjective.vObjective)))
    Ok(resultJson)
  }
  
	def getLessonKSAJsonByTopic(idLesson: Long, idTopic: Long) = Action {
	    val currentlyAssociated = SqlLessonTopicObjectives.selectWhere("Lesson = " + idLesson).map(_.vTopicObjective)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	}
  
  
  def saveLessonTopicObjectives(newEntry: Int) = Action { implicit request =>
  	formLessonTopicObjectives.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonTopicObjectives => {
        if (vLessonTopicObjectives.validate) {
          newEntry match {
            case 0 => SqlLessonTopicObjectives.update(vLessonTopicObjectives)
            case _ => SqlLessonTopicObjectives.insert(vLessonTopicObjectives)
          }
          Redirect(routes.LessonsController.showLessons(vLessonTopicObjectives.vLesson))
        } else {
          val validationErrors = vLessonTopicObjectives.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
  
  def saveLessonTopicObjectivesList = Action { implicit request =>
  	formLessonTopicObjectivesList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonTopicObjectivesList => {
        Logger.debug("Topic Objectives List: " + vLessonTopicObjectivesList.vTopicObjective.size + ": " + vLessonTopicObjectivesList.vTopicObjective)
        vLessonTopicObjectivesList.getList.foreach (vLessonTopicObjectives =>
          if (vLessonTopicObjectives.validate) {
            SqlLessonTopicObjectives.insert(vLessonTopicObjectives)
          } else {
            val validationErrors = vLessonTopicObjectives.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.LessonsController.showLessons(vLessonTopicObjectivesList.vLesson))
      })
  }

    
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", formLessonTopicObjectives.errors.toList)
  }
}
