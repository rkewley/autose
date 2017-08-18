
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

object TopicLinkController extends Base {

  val formTopicLink = Form[MdlTopicLink](
    mapping (
	"fidTopicLink" -> of[Long],
	"fTopic" -> of[Long],
	"fLink" -> text,
	"fDescription" -> text
    )(MdlTopicLink.apply)(MdlTopicLink.unapply)
  )

  def listSelectedTopicLink(topicId: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewlist.html.listTopicLink(SqlTopicLink.selectWhere("Topic = " + topicId), topicId))
  }

   def editTopicLink(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formTopicLink(formTopicLink.fill(SqlTopicLink.select(id)), 0))
  }

   def showTopicLink(id: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewshow.html.showTopicLink(SqlTopicLink.select(id)))
  }

   def deleteTopicLink(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vTopicLink = SqlTopicLink.select(id)
    SqlTopicLink.delete(id)
    Redirect(routes.TopicLinkController.listSelectedTopicLink(vTopicLink.vTopic))
  }

  def createTopicLink(topicId: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vTopicLink = new MdlTopicLink(0, topicId, "http://", "")
    Ok(viewforms.html.formTopicLink(formTopicLink.fill(vTopicLink), 1))
  }

  def saveTopicLink(newEntry: Int) = Action { implicit request =>
  	formTopicLink.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTopicLink => {
        if (vTopicLink.validate) {
          newEntry match {
            case 0 => SqlTopicLink.update(vTopicLink)
            case _ => SqlTopicLink.insert(vTopicLink)
          }
          Redirect(routes.TopicLinkController.listSelectedTopicLink(vTopicLink.vTopic))
        } else {
          val validationErrors = vTopicLink.validationErrors
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
    errMess("Error Messages:\n", formTopicLink.errors.toList)
  }
}
