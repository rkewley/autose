
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
import com.googlecode.sardine._
import java.io._

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
  
  def uploadTopicPicture(topicId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vTopicPicture = new MdlTopicPicture(0, topicId, "", "", "")
    Ok(viewforms.html.formTopicPictureFile(formTopicPicture.fill(vTopicPicture), 1))
  }

  def postTopicPicture(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    formTopicPicture.bindFromRequest().fold(
      errFrm => {
        Logger.debug("Got a form error")
        val fErrorMessage = "Form Error:" + formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vTopicPicture => {
        request.body.file("topicPictureFile").map { topicPictureFile =>
          if (vTopicPicture.validate) {
        	val vTopics = SqlTopics.select(vTopicPicture.vTopic)
        	val directory = Globals.webDavServer + "Topics/" + vTopics.vTopic
            val filename = Globals.webDavServer + "Topics/" + vTopics.vTopic + "/" + topicPictureFile.filename
            val path = filename.replaceAll(" ", "%20")
            val dirPath = directory.replaceAll(" ", "%20")
            val contentType = topicPictureFile.contentType
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              if (!sardine.exists(dirPath)) {
                sardine.createDirectory(dirPath)
                Logger.debug("Creating directory " + dirPath)
              }
              val inputStream = new FileInputStream(topicPictureFile.ref.file)
              contentType match  {
                case Some(cType) => sardine.put(path, inputStream, cType)
                case None => sardine.put(path, inputStream)
              }
              None
            } 
            catch {
              case e: Exception => Some(BadRequest(viewforms.html.formError(e.getMessage, request.headers("REFERER"))))
            }
            simpleResult match { // Only update the database if there is no file error
              case None =>
                val v2TopicPicture = new MdlTopicPicture(
                  vTopicPicture.vidTopicPicture,
                  vTopicPicture.vTopic,
                  path,
                  vTopicPicture.vAlternateText,
                 vTopicPicture.vCaption)
                newEntry match {
                  case 0 => SqlTopicPicture.update(v2TopicPicture)
                  case _ => SqlTopicPicture.insert(v2TopicPicture)
                }           
              Redirect(routes.TopicPictureController.listSelectedTopicPicture(vTopicPicture.vTopic))
              case Some(badResult) =>
                Logger.debug("WebDav upload error")
                badResult
            }
          } else {
        	  Logger.debug("Got a validation error")
              val validationErrors = "Lesson Link validation error:" + vTopicPicture.validationErrors
               Logger.debug(validationErrors)
               BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        }.getOrElse {
          Logger.debug("File upload error")
          BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
        }
      }
    )
  }
}
