
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

object TopicPictureController extends Base {

  val formTopicPicture = Form[MdlTopicPicture](
    mapping (
	"fidTopicPicture" -> of[Long],
	"fTopic" -> of[Long],
	"fHyperlink" -> text,
	"fAlternateText" -> text,
	"fCaption" -> text
    )(MdlTopicPicture.apply)(MdlTopicPicture.unapply)
  )
      

  def listSelectedTopicPicture(topicId: Long) = Action {
    Ok(viewlist.html.listTopicPicture(SqlTopicPicture.selectWhere("Topic = " + topicId), topicId))
  }

   def editTopicPicture(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formTopicPicture(formTopicPicture.fill(SqlTopicPicture.select(id)), 0))
  }

   def showTopicPicture(id: Long) = Action {
    Ok(viewshow.html.showTopicPicture(SqlTopicPicture.select(id)))
  }

   def deleteTopicPicture(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicPicture = SqlTopicPicture.select(id)
    SqlTopicPicture.delete(id)
    Redirect(routes.TopicPictureController.listSelectedTopicPicture(vTopicPicture.vTopic))
  }

  def createTopicPicture(topicId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicPicture = new MdlTopicPicture(0, topicId, "", "", "")
    Ok(viewforms.html.formTopicPicture(formTopicPicture.fill(vTopicPicture), 1))
  }

  def saveTopicPicture(newEntry: Int) = Action { implicit request =>
  	formTopicPicture.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTopicPicture => {
        if (vTopicPicture.validate) {
          newEntry match {
            case 0 => SqlTopicPicture.update(vTopicPicture)
            case _ => SqlTopicPicture.insert(vTopicPicture)
          }
          Redirect(routes.TopicPictureController.listSelectedTopicPicture(vTopicPicture.vTopic))
        } else {
          val validationErrors = vTopicPicture.validationErrors
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
    errMess("Error Messages:\n", formTopicPicture.errors.toList)
  }
}
