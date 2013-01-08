
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

object CourseLinksController extends Base {

  val formCourseLinks = Form[MdlCourseLinks](
    mapping (
	"fidCourseLinks" -> of[Long],
	"fCourse" -> of[Long],
	"fLink" -> text,
	"fDisplayDescription" -> text,
	"fIsFileLink" -> of[Boolean]
    )(MdlCourseLinks.apply)(MdlCourseLinks.unapply)
  )
      

  def listCourseLinks(courseId: Long) = Action {
    Ok(viewlist.html.listCourseLinks(SqlCourseLinks.selectWhere("Course = " + courseId), courseId))
  }

   def editCourseLinks(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formCourseLinks(formCourseLinks.fill(SqlCourseLinks.select(id)), 0))
  }

   def showCourseLinks(id: Long) = Action {
    Ok(viewshow.html.showCourseLinks(SqlCourseLinks.select(id)))
  }

   def deleteCourseLinks(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vCourseLinks = SqlCourseLinks.select(id)
    SqlCourseLinks.delete(id)
    Redirect(routes.CourseLinksController.listCourseLinks(vCourseLinks.vCourse))
  }

  def createCourseLinks(courseId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vCourseLinks = new MdlCourseLinks(0, courseId, "http://", "", true)
    Ok(viewforms.html.formCourseLinks(formCourseLinks.fill(vCourseLinks), 1))
  }

  def saveCourseLinks(newEntry: Int) = Action { implicit request =>
  	formCourseLinks.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vCourseLinks => {
        if (vCourseLinks.validate) {
          newEntry match {
            case 0 => SqlCourseLinks.update(vCourseLinks)
            case _ => SqlCourseLinks.insert(vCourseLinks)
          }
          Redirect(routes.CourseLinksController.listCourseLinks(vCourseLinks.vCourse))
        } else {
          val validationErrors = vCourseLinks.validationErrors
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
    errMess("Error Messages:\n", formCourseLinks.errors.toList)
  }
}
