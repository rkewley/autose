
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import persistence._
import play.Logger

object ControllerCourses extends Controller {

  val formCourses = Form[MdlCourses](
    mapping (
	"fCourseIDNumber" -> text,
	"fCourseName" -> text,
	"fCourseDirectorEmail" -> text,
	"fProgramDirectorEmail" -> text,
	"fCourseDescriptionRedbook" -> text,
	"fCreditHours" -> of[Double],
	"fPrerequisites" -> text,
	"fCorequisites" -> text,
	"fDisqualifiers" -> text,
	"fCourseStrategy" -> text,
	"fCriteriaForPassing" -> text,
	"fAdminInstructions" -> text,
	"fDepartmentID" -> of[Int],
	"fCourseWebsite" -> of[Boolean],
	"fCourseDescriptionWebsite" -> text
    )(Course.apply)(Course.unapply)
  )
      

  def listCourses = Action {
    Ok(views.html.listCourses(SqlCourses.all))
  }

   def editCourses(id: Int) = Action {
    Ok(views.html.formCourses(formCourses.fill(SqlCourses.select(id)), 0))
  }

  def createCourses = Action {
    Ok(views.html.formCourses(formCourses.fill(new MdlCourses()), 1))
  }

  def saveCourses(newEntry: Int) = Action { implicit request =>
  	formCourses.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(views.html.formError(errorMessage, request.headers("REFERER")))
      },
      vCourses => {
        if (vCourses.validate) {
          newEntry match {
            case 0 => SqlCourses.update(course)
            case _ => SqlCourses.insert(course)
          }
          Redirect(routes.CoursesController.listCourses)
        } else {
          val validationErrors = vCourses.validationErrors
          Logger.debug(validationErrors)
          BadRequest(views.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
    
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", formCourses.errors.toList)
  }
}
