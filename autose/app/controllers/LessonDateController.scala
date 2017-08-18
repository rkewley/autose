
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
import java.util.Date
import AnormExtension._

object LessonDateController extends Base {

  val formLessonDate = Form[MdlLessonDate](
    mapping (
	"fidLessonDate" -> of[Long],
	"fLesson" -> of[Long],
	"fAcademicYear" -> of[Long],
	"fAcademicTerm" -> of[Long],
	"fDay1" -> of[Date],
	"fDay2" -> of[Date]
    )(MdlLessonDate.apply)(MdlLessonDate.unapply)
  )
      

  def listLessonDate = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewlist.html.listLessonDate(SqlLessonDate.all))
  }

   def editLessonDate(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formLessonDate(formLessonDate.fill(SqlLessonDate.select(id)), 0))
  }

   def showLessonDate(id: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewshow.html.showLessonDate(SqlLessonDate.select(id)))
  }

   def deleteLessonDate(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    SqlLessonDate.delete(id)
    Ok(viewlist.html.listLessonDate(SqlLessonDate.all))
  }

  def createLessonDate = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formLessonDate(formLessonDate.fill(new MdlLessonDate(0, 0, 2013, 2, new Date(), new Date())), 1))
  }

  def saveLessonDate(newEntry: Int) = Action { implicit request =>
  	formLessonDate.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonDate => {
        if (vLessonDate.validate) {
          newEntry match {
            case 0 => SqlLessonDate.update(vLessonDate)
            case _ => SqlLessonDate.insert(vLessonDate)
          }
          Redirect(routes.LessonDateController.listLessonDate)
        } else {
          val validationErrors = vLessonDate.validationErrors
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
    errMess("Error Messages:\n", formLessonDate.errors.toList)
  }
}
