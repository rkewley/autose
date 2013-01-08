
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

object TopicReferencesController extends Base {

  val formTopicReferences = Form[MdlTopicReferences](
    mapping (
	"fidTopicReferences" -> of[Long],
	"fTopic" -> of[Long],
	"fReference" -> of[Long]
    )(MdlTopicReferences.apply)(MdlTopicReferences.unapply)
  )
  

  def listSelectedTopicReferences(topicId: Long) = Action {
    Ok(viewlist.html.listTopicReferences(SqlTopicReferences.selectWhere("Topic = " + topicId), topicId))
  }

  def deleteTopicReferences(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicReferences = SqlTopicReferences.select(id)
    SqlTopicReferences.delete(id)
    Redirect(routes.TopicReferencesController.listSelectedTopicReferences(vTopicReferences.vTopic))
  }

  def editTopicReferences(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicReferences = SqlTopicReferences.select(id)
    Ok(viewforms.html.formTopicReferences(formTopicReferences.fill(vTopicReferences), 0))
  }

  def showTopicReferences(id: Long) = Action {
    Ok(viewshow.html.showTopicReferences(SqlTopicReferences.select(id)))
  }

  def createTopicReferences(topicId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val mdlTopicReferences = new MdlTopicReferences(0, topicId, 0)
    Ok(viewforms.html.formTopicReferences(formTopicReferences.fill(mdlTopicReferences), 1))
  }

  def saveTopicReferences(newEntry: Int) = Action { implicit request =>
  	formTopicReferences.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTopicReferences => {
        if (vTopicReferences.validate) {
          newEntry match {
            case 0 => SqlTopicReferences.update(vTopicReferences)
            case _ => SqlTopicReferences.insert(vTopicReferences)
          }
          Redirect(routes.TopicReferencesController.listSelectedTopicReferences(vTopicReferences.vTopic))
        } else {
          val validationErrors = vTopicReferences.validationErrors
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
    errMess("Error Messages:\n", formTopicReferences.errors.toList)
  }
}
