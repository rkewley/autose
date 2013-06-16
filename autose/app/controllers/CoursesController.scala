
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

object CoursesController extends Base {

  val formCourses = Form[MdlCourses](
    mapping (
	"fidCourse" -> of[Long],
	"fCourseIDNumber" -> text,
	"fAcademicYear" -> of[Long],
	"fAcademicTerm" -> of[Long],
	"fCourseName" -> text,
	"fCourseDirector" -> of[Long],
	"fProgramDirector" -> of[Long],
	"fCourseDescriptionRedbook" -> text,
	"fCreditHours" -> of[Double],
	"fPrerequisites" -> text,
	"fCorequisites" -> text,
	"fDisqualifiers" -> text,
	"fCourseStrategy" -> text,
	"fCriteriaForPassing" -> text,
	"fAdminInstructions" -> text,
	"fDepartmentID" -> of[Long],
	"fCourseWebsite" -> of[Boolean],
	"fCourseDescriptionWebsite" -> text
    )(MdlCourses.apply)(MdlCourses.unapply)
  )
      

  def listCourses = Action {
    Ok(viewlist.html.listCourses(SqlCourses.all))
  }

   def editCourses(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formCourses(formCourses.fill(SqlCourses.select(id)), 0))
  }

   def showCourses(id: Long) = Action {
    Ok(viewshow.html.showCourses(SqlCourses.select(id)))
  }
   
  def homeCourses(id: Long) = Action {
    Ok(viewhome.html.homeCourses(SqlCourses.select(id)))
  }

   def deleteCourses(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    SqlCourses.delete(id)
    Ok(viewlist.html.listCourses(SqlCourses.all))
  }

  def createCourses = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formCourses(formCourses.fill(new MdlCourses()), 1))
  }

  def createCourseDirectoryStructure(vCourses: MdlCourses) {
    val directory = Globals.webDavServer + "Courses/AT" + String.valueOf(vCourses.vAcademicYear).substring(2) + "-" +
      String.valueOf(vCourses.vAcademicTerm) + "/" + vCourses.vCourseIDNumber
    val dirPath = directory.replaceAll(" ", "%20")
    val sardine = SardineFactory.begin("seweb", "G0Systems!")
    try {
      if (!sardine.exists(dirPath)) {
        Logger.debug("Creating directory " + dirPath)
        sardine.createDirectory(dirPath)

        sardine.createDirectory(dirPath + "/CourseFiles")
        sardine.createDirectory(dirPath + "/CoursePhotos")
        sardine.createDirectory(dirPath + "/Lessons")
        sardine.createDirectory(dirPath + "/GradedRequirementFiles")
        def createLessonDirectory(i: Int) {
          if (i <= 40) {
            val lessonPath = i match {
              case j if j <= 9 => "0" + String.valueOf(i)
              case _ => String.valueOf(i)
            }
            sardine.createDirectory(dirPath + "/Lessons/Lesson" + lessonPath)
            createLessonDirectory(i+1)
          }
        }
        createLessonDirectory(1)
      }
    }

  }

  def saveCourses(newEntry: Int) = Action { implicit request =>
  	formCourses.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vCourses => {
        if (vCourses.validate) {
          createCourseDirectoryStructure(vCourses)
          newEntry match {
            case 0 => SqlCourses.update(vCourses)
            case _ => SqlCourses.insert(vCourses)
          }
          Redirect(routes.CoursesController.listCourses)
        } else {
          val validationErrors = vCourses.validationErrors
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
    errMess("Error Messages:\n", formCourses.errors.toList)
  }
}
