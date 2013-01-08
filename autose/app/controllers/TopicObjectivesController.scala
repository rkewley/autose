
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
import FormFieldImplicits._

object TopicObjectivesController extends Base {

  val formTopicObjectives = Form[MdlTopicObjectives](
    mapping (
	"fTopicObjectiveNumber" -> of[Long],
	"fObjective" -> text,
	"fTopic" -> of[Long],
	"fKSAB" -> text
    )(MdlTopicObjectives.apply)(MdlTopicObjectives.unapply)
  )
      

//  def listTopicObjectives = Action {
//    Ok(viewlist.html.listTopicObjectives(SqlTopicObjectives.all))
//  }

  def listSelectedTopicObjectives(topicId: Long) = Action {
    Ok(viewlist.html.listTopicObjectives(SqlTopicObjectives.selectWhere("Topic = " + topicId), topicId))
  }

   def editTopicObjectives(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formTopicObjectives(formTopicObjectives.fill(SqlTopicObjectives.select(id)), 0))
  }

   def showTopicObjectives(id: Long) = Action {
    Ok(viewshow.html.showTopicObjectives(SqlTopicObjectives.select(id)))
  }

   def deleteTopicObjectives(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicObjectives = SqlTopicObjectives.select(id)
    SqlTopicObjectives.delete(id)
    Redirect(routes.TopicObjectivesController.listSelectedTopicObjectives(vTopicObjectives.vTopic))
  }

  def createTopicObjectives(topicId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicObjectives = new MdlTopicObjectives(0, "", topicId, "Skill")
    Ok(viewforms.html.formTopicObjectives(formTopicObjectives.fill(vTopicObjectives), 1))
  }

  def saveTopicObjectives(newEntry: Int) = Action { implicit request =>
  	formTopicObjectives.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTopicObjectives => {
        if (vTopicObjectives.validate) {
          newEntry match {
            case 0 => SqlTopicObjectives.update(vTopicObjectives)
            case _ => SqlTopicObjectives.insert(vTopicObjectives)
          }
          Redirect(routes.TopicObjectivesController.listSelectedTopicObjectives(vTopicObjectives.vTopic))
        } else {
          val validationErrors = vTopicObjectives.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
    
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", formTopicObjectives.errors.toList)
  }
}
