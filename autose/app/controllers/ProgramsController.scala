
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import slick._
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import FormFieldImplicits._
import scala.slick.driver.ExtendedProfile
import scala.slick.lifted.Query

object ProgramsController extends Base {

  val formPrograms = Form[MdlPrograms](
    mapping (
    "fidPrograms" -> optional(of[Long]),
	"fProgram" -> text,
	"fName" -> text,
	"fSlogan" -> text,
	"fInformation" -> text,
	"fProgramDirector" -> of[Long],
	"fDepartment" -> of[Long]
    )(MdlPrograms.apply)(MdlPrograms.unapply) 
  )
      

  def listPrograms = Action {
    Ok(viewlist.html.listPrograms(AppDB.dal.Programs.all))
  }

   def editPrograms(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
      AppDB.dal.Programs.select(id) match {
        case vPrograms: Some[MdlPrograms] =>
          Ok(viewforms.html.formPrograms(formPrograms.fill(vPrograms.get), 0))
        case None =>
          BadRequest(viewforms.html.formError("Programs with key " + id + " not found in database", request.headers("REFERER")))
      }
    
  }

   def showPrograms(id: Long) = Action { implicit request =>
	   AppDB.dal.Programs.select(id) match {
        case vPrograms: Some[MdlPrograms] =>
          Ok(viewshow.html.showPrograms(vPrograms.get))
        case None =>
          BadRequest(viewforms.html.formError("Programs with key " + id + " not found in database", request.headers("REFERER")))
      }
  }
        

   def deletePrograms(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    AppDB.dal.Programs.delete(id)
    Ok(viewlist.html.listPrograms(AppDB.dal.Programs.all))
  }

  def createPrograms = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formPrograms(formPrograms.fill(new MdlPrograms()), 1))
  }

  def savePrograms(newEntry: Int) = Action { implicit request =>
  	formPrograms.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vPrograms => {
        if (vPrograms.validate) {
          newEntry match {
            case 0 => AppDB.dal.Programs.update(vPrograms)
            case _ => AppDB.dal.Programs.insert(vPrograms)
          }
          Redirect(routes.ProgramsController.listPrograms)
        } else {
          val validationErrors = vPrograms.validationErrors
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
    errMess("Error Messages:\n", formPrograms.errors.toList)
  }
}
