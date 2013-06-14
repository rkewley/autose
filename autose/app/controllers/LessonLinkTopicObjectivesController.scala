
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import persistence._
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.json._

object LessonLinkTopicObjectivesController extends Base {

  val formLessonLinkTopicObjectives = Form[MdlLessonLinkTopicObjectives](
    mapping (
	"fidLessonLinkTopicObjective" -> of[Long],
	"fLessonLink" -> of[Long],
	"fTopicObjective" -> of[Long]
    )(MdlLessonLinkTopicObjectives.apply)(MdlLessonLinkTopicObjectives.unapply)
  )

  val formLessonLinkTopicObjectivesList = Form[MdlLessonLinkTopicObjectivesList](
    mapping (
	"fidLessonLinkTopicObjective" -> of[Long],
	"fLessonLink" -> of[Long],
	"fTopicObjectives" -> list(of[Long])
    )(MdlLessonLinkTopicObjectivesList.apply)(MdlLessonLinkTopicObjectivesList.unapply)
  )
  
  
  def listLessonLinkTopicObjectives(idLessonLinks: Long) = Action {
     Ok(viewlist.html.listLessonLinkTopicObjectives(SqlLessonLinkTopicObjectives.selectWhere("`LessonLink` = " + idLessonLinks), idLessonLinks))
  }

   def editLessonLinkTopicObjectives(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formLessonLinkTopicObjectives(formLessonLinkTopicObjectives.fill(SqlLessonLinkTopicObjectives.select(id)), 0))
  }

   def showLessonLinkTopicObjectives(id: Long) = Action {
    Ok(viewshow.html.showLessonLinkTopicObjectives(SqlLessonLinkTopicObjectives.select(id)))
  }

   def deleteLessonLinkTopicObjectives(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinkTopicObjectives = SqlLessonLinkTopicObjectives.select(id)
    SqlLessonLinkTopicObjectives.delete(id)
    Redirect(routes.LessonLinkTopicObjectivesController.listLessonLinkTopicObjectives(vLessonLinkTopicObjectives.vLessonLink))
  }

  def createLessonLinkTopicObjectives(idLessonLinks: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinkTopicObjectives = new MdlLessonLinkTopicObjectives(0, idLessonLinks, 0)
    Ok(viewforms.html.formLessonLinkTopicObjectives(formLessonLinkTopicObjectives.fill(vLessonLinkTopicObjectives), 1))
  }

  def createLessonLinkTopicObjectivesList(idLessonLinks: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinkTopicObjectivesList = new MdlLessonLinkTopicObjectivesList(0, idLessonLinks, List())
    Ok(viewforms.html.formLessonLinkTopicObjectivesList(formLessonLinkTopicObjectivesList.fill(vLessonLinkTopicObjectivesList)))
  }
  
	def getLessonLinkKSAJsonByTopic(idLessonLink: Long, idTopic: Long) = Action {
	    val currentlyAssociated = SqlLessonLinkTopicObjectives.selectWhere("LessonLink = " + idLessonLink).map(_.vTopicObjective)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	}

  def saveLessonLinkTopicObjectives(newEntry: Int) = Action { implicit request =>
  	formLessonLinkTopicObjectives.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonLinkTopicObjectives => {
        if (vLessonLinkTopicObjectives.validate) {
          newEntry match {
            case 0 => SqlLessonLinkTopicObjectives.update(vLessonLinkTopicObjectives)
            case _ => SqlLessonLinkTopicObjectives.insert(vLessonLinkTopicObjectives)
          }
          Redirect(routes.LessonLinkTopicObjectivesController.listLessonLinkTopicObjectives(vLessonLinkTopicObjectives.vLessonLink))
        } else {
          val validationErrors = vLessonLinkTopicObjectives.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
  
  def saveLessonLinkTopicObjectivesList = Action { implicit request =>
  	formLessonLinkTopicObjectivesList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonLinkTopicObjectivesList => {
        Logger.debug("Topic Objective List: " + vLessonLinkTopicObjectivesList.getList.size + " " + vLessonLinkTopicObjectivesList)
        vLessonLinkTopicObjectivesList.getList.foreach (vLessonLinkTopicObjectives =>
          if (vLessonLinkTopicObjectives.validate) {
            SqlLessonLinkTopicObjectives.insert(vLessonLinkTopicObjectives)
          } else {
            val validationErrors = vLessonLinkTopicObjectives.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.LessonLinkTopicObjectivesController.listLessonLinkTopicObjectives(vLessonLinkTopicObjectivesList.vLessonLink))
      })
  }

  
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", formLessonLinkTopicObjectives.errors.toList)
  }
}
