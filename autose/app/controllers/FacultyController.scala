
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

object FacultyController extends Base {

  val formFaculty = Form[MdlFaculty](
    mapping (
	"fLastName" -> text,
	"fFirstName" -> text,
	"fTitle" -> text,
	"fOfficeNumber" -> text,
	"fOfficePhone" -> text,
	"fBranchofService" -> text,
	"fEmail" -> text,
	"fFacultyPhotoFile" -> text,
	"fBiography" -> text
    )(MdlFaculty.apply)(MdlFaculty.unapply)
  )
      

  def listFaculty = Action {
    Ok(viewlist.html.listFaculty(SqlFaculty.all))
  }

   def editFaculty(id: String) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formFaculty(formFaculty.fill(SqlFaculty.select(id)), 0))
  }

   def showFaculty(id: String) = Action {
    Ok(viewshow.html.showFaculty(SqlFaculty.select(id)))
  }

   def deleteFaculty(id: String) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    SqlFaculty.delete(id)
    Ok(viewlist.html.listFaculty(SqlFaculty.all))
  }

  def createFaculty = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formFaculty(formFaculty.fill(new MdlFaculty()), 1))
  }

  def saveFaculty(newEntry: Int) = Action { implicit request =>
  	formFaculty.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vFaculty => {
        if (vFaculty.validate) {
          newEntry match {
            case 0 => SqlFaculty.update(vFaculty)
            case _ => SqlFaculty.insert(vFaculty)
          }
          Redirect(routes.FacultyController.listFaculty)
        } else {
          val validationErrors = vFaculty.validationErrors
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
    errMess("Error Messages:\n", formFaculty.errors.toList)
  }
  
  def getFacultyListForCourse(idCourse: Long) = {
    val courseOfferingList1 = SqlCourseOfferings.selectWhere("`Course` = " + idCourse).map(_.vInstructorEmail)
    val courseOfferingList = courseOfferingList1.distinct
    val inClause = courseOfferingList.reduceLeft[String] { (acc, name) =>
        acc + " or " + ("Email = '" + name + "'")
    }
    println("Faculty Query: " + inClause)
    val facultyList = SqlFaculty.selectWhere(inClause).sortWith((a:MdlFaculty, b:MdlFaculty) => a.vLastName < b.vLastName)
    val identifierList = facultyList.map(_.selectIdentifier)
    identifierList.+:(("all", "all"))
  }
}
