
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

object CourseLinksController extends Base {

  val formCourseLinks = Form[MdlCourseLinks](
    mapping (
	"fidCourseLinks" -> of[Long],
	"fCourse" -> of[Long],
	"fLink" -> text,
	"fDisplayDescription" -> text,
	"fIsFileLink" -> of[Boolean],
	"fFaculty" -> of[Long]
    )(MdlCourseLinks.apply)(MdlCourseLinks.unapply)
  )
      

  def listCourseLinks(idCourses: Long) = Action {
     Ok(viewlist.html.listCourseLinks(SqlCourseLinks.selectWhere("`Course` = " + idCourses), idCourses))
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
    val vCourseLinks = new MdlCourseLinks(0, courseId, "http://", "", false, -1)
    Ok(viewforms.html.formCourseLinks(formCourseLinks.fill(vCourseLinks), 1))
  }

  def uploadCourseFile(courseId: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vCourseLinks = new MdlCourseLinks(0, courseId, "http://", "", true, -1)
    Ok(viewforms.html.formCourseFiles(formCourseLinks.fill(vCourseLinks), 1))
  }

  def postCourseFile(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    formCourseLinks.bindFromRequest().fold(
      errFrm => {
        val fErrorMessage = formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vCourseLinks => {
        request.body.file("courseFile").map { courseFile =>
          if (vCourseLinks.validate) {
            val courseId = vCourseLinks.vCourse
            val courseIdNumber = SqlCourses.select(courseId).vCourseIDNumber
            val filename = Globals.webDavServer + "Courses/" + Globals.term + "/" + courseIdNumber + "/CourseFiles/" + courseFile.filename
            val path = filename.replaceAll(" ", "%20")
            Logger.debug(path)
            val contentType = courseFile.contentType
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              val inputStream = new FileInputStream(courseFile.ref.file)
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
                val v2CourseLinks = new MdlCourseLinks(
                  vCourseLinks.vidCourseLinks,
                  vCourseLinks.vCourse,
                  path,
                  vCourseLinks.vDisplayDescription,
                  vCourseLinks.vIsFileLink,
                  vCourseLinks.vFaculty)
                newEntry match {
                  case 0 => SqlCourseLinks.update(v2CourseLinks)
                  case _ => SqlCourseLinks.insert(v2CourseLinks)
                }           
              Redirect(routes.CourseLinksController.listCourseLinks(vCourseLinks.vCourse))
              case Some(badResult) =>
                badResult
            }
          } else {
              val validationErrors = vCourseLinks.validationErrors
               Logger.debug(validationErrors)
               BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        }.getOrElse {
          BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
        }
      }
    )
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
