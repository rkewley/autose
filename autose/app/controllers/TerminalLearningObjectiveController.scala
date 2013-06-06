
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

object TerminalLearningObjectiveController extends Base {

  val formTerminalLearningObjective = Form[MdlTerminalLearningObjective](
    mapping (
	"fidTerminalLearningObjective" -> of[Long],
	"fTerminalLearningObjective" -> text,
	"fTopic" -> of[Long],
	"fProgram" -> of[Long]
    )(MdlTerminalLearningObjective.apply)(MdlTerminalLearningObjective.unapply)
  )
      

//  def listTerminalLearningObjective = Action {
//    Ok(viewlist.html.listTerminalLearningObjective(SqlTerminalLearningObjective.all))
//  }

  def listSelectedTerminalLearningObjective(topicId: Long) = Action {
    Ok(viewlist.html.listTerminalLearningObjective(SqlTerminalLearningObjective.selectWhere("Topic = " + topicId), topicId))
  }

   def editTerminalLearningObjective(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formTerminalLearningObjective(formTerminalLearningObjective.fill(SqlTerminalLearningObjective.select(id)), 0))
  }

   def showTerminalLearningObjective(id: Long) = Action {
    Ok(viewshow.html.showTerminalLearningObjective(SqlTerminalLearningObjective.select(id)))
  }

   def deleteTerminalLearningObjective(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTerminalLearningObjective = SqlTerminalLearningObjective.select(id)
    SqlTerminalLearningObjective.delete(id)
    Redirect(routes.TerminalLearningObjectiveController.listSelectedTerminalLearningObjective(vTerminalLearningObjective.vTopic))
  }

  def createTerminalLearningObjective(topicId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTerminalLearningObjective = new MdlTerminalLearningObjective(0, "", topicId, 0)
    Ok(viewforms.html.formTerminalLearningObjective(formTerminalLearningObjective.fill(vTerminalLearningObjective), 1))
  }

  def saveTerminalLearningObjective(newEntry: Int) = Action { implicit request =>
  	formTerminalLearningObjective.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTerminalLearningObjective => {
        if (vTerminalLearningObjective.validate) {
          newEntry match {
            case 0 => SqlTerminalLearningObjective.update(vTerminalLearningObjective)
            case _ => SqlTerminalLearningObjective.insert(vTerminalLearningObjective)
          }
          Redirect(routes.TerminalLearningObjectiveController.listSelectedTerminalLearningObjective(vTerminalLearningObjective.vTopic))
        } else {
          val validationErrors = vTerminalLearningObjective.validationErrors
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
    errMess("Error Messages:\n", formTerminalLearningObjective.errors.toList)
  }
}
