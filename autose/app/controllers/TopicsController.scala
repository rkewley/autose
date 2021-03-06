
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
import com.googlecode.sardine._
import jp.t2v.lab.play2.auth._


object TopicsController extends Base with OptionalAuthElement{

  val formTopics = Form[MdlTopics](
    mapping (
	"fTopicsIndex" -> of[Long],
	"fTopic" -> text,
	"fTopicShortDescription" -> text,
	"fTopicDetailedDescription" -> text
    )(MdlTopics.apply)(MdlTopics.unapply)
  )
      

  def listTopics = Action {
    Ok(viewlist.html.listTopics(SqlTopics.all.sortWith(MdlTopics.compare)))
  }

   def editTopics(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formTopics(formTopics.fill(SqlTopics.select(id)), 0))
  }

   def showTopics(id: Long) = Action {
    Ok(viewshow.html.showTopics(SqlTopics.select(id)))
  }

   def homeTopics(id: Long) = StackAction { implicit request => 
    Ok(viewhome.html.homeTopics(SqlTopics.select(id), !(loggedIn.isEmpty)))
  }

   def deleteTopics(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    SqlTopics.delete(id)
    Ok(viewlist.html.listTopics(SqlTopics.all))
  }

  def createTopics = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formTopics(formTopics.fill(new MdlTopics()), 1))
  }

  def saveTopics(newEntry: Int) = Action { implicit request =>
  	formTopics.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vTopics => {
        if (vTopics.validate) {
          val directory = Globals.webDavServer + "Topics/" + vTopics.vTopic
          val dirPath = directory.replaceAll(" ", "%20")
          val sardine = SardineFactory.begin("seweb", "G0Systems!")
          try {
            if (!sardine.exists(dirPath)) {
              sardine.createDirectory(dirPath)
              Logger.debug("Creating directory " + dirPath)
            }
          }
          newEntry match {
            case 0 => SqlTopics.update(vTopics)
            case _ => SqlTopics.insert(vTopics)
          }
          Redirect(routes.TopicsController.listTopics)
        } else {
          val validationErrors = vTopics.validationErrors
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
    errMess("Error Messages:\n", formTopics.errors.toList)
  }
}
