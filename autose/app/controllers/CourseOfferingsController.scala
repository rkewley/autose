
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

object CourseOfferingsController extends Base {

  val formCourseOfferings = Form[MdlCourseOfferings](
    mapping (
	"fidCourseOfferings" -> of[Long],
	"fCourse" -> of[Long],
	"fClassHour" -> text,
	"fSection" -> of[Long],
	"fLocation" -> text,
	"fInstructorEmail" -> text
    )(MdlCourseOfferings.apply)(MdlCourseOfferings.unapply)
  )
      

  def listCourseOfferings = Action {
    Ok(viewlist.html.listCourseOfferings(SqlCourseOfferings.all))
  }

   def editCourseOfferings(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formCourseOfferings(formCourseOfferings.fill(SqlCourseOfferings.select(id)), 0))
  }

   def showCourseOfferings(id: Long) = Action {
    Ok(viewshow.html.showCourseOfferings(SqlCourseOfferings.select(id)))
  }

   def deleteCourseOfferings(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    SqlCourseOfferings.delete(id)
    Ok(viewlist.html.listCourseOfferings(SqlCourseOfferings.all))
  }

  def createCourseOfferings = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formCourseOfferings(formCourseOfferings.fill(new MdlCourseOfferings()), 1))
  }

  def saveCourseOfferings(newEntry: Int) = Action { implicit request =>
  	formCourseOfferings.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vCourseOfferings => {
        if (vCourseOfferings.validate) {
          newEntry match {
            case 0 => SqlCourseOfferings.update(vCourseOfferings)
            case _ => SqlCourseOfferings.insert(vCourseOfferings)
          }
          Redirect(routes.CourseOfferingsController.listCourseOfferings)
        } else {
          val validationErrors = vCourseOfferings.validationErrors
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
    errMess("Error Messages:\n", formCourseOfferings.errors.toList)
  }
}
