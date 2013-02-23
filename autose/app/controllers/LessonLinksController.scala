
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


object LessonLinksController extends Base {

  val formLessonLinks = Form[MdlLessonLinks](
    mapping (
	"fLessonLinkNumber" -> of[Long],
	"fLink" -> text,
	"fDescription" -> text,
	"fIsFileLiink" -> of[Boolean],
	"fLesson" -> of[Long],
	"fFacultyEmail" -> text
    )(MdlLessonLinks.apply)(MdlLessonLinks.unapply)
  )
      

  def listLessonLinks(idLessons: Long) = Action {
     val vLesson = SqlLessons.select(idLessons)
     Ok(viewlist.html.listLessonLinks(SqlLessonLinks.selectWhere("`Lesson` = " + idLessons), vLesson))
  }

   def editLessonLinks(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formLessonLinks(formLessonLinks.fill(SqlLessonLinks.select(id)), 0))
  }

   def showLessonLinks(id: Long) = Action {
    Ok(viewshow.html.showLessonLinks(SqlLessonLinks.select(id)))
  }

   def deleteLessonLinks(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinks = SqlLessonLinks.select(id)
    SqlLessonLinks.delete(id)
    Redirect(routes.LessonLinksController.listLessonLinks(vLessonLinks.vLesson))
  }

  def createLessonLinks(idLessons: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinks = new MdlLessonLinks(0, "", "", true, idLessons, "all")
    Ok(viewforms.html.formLessonLinks(formLessonLinks.fill(vLessonLinks), 1))
  }

  def uploadLessonFile(idLessons: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vLessonLinks = new MdlLessonLinks(0, "", "", true, idLessons, "all")
    Ok(viewforms.html.formLessonFiles(formLessonLinks.fill(vLessonLinks), 1))
  }

  def postLessonFile(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    formLessonLinks.bindFromRequest().fold(
      errFrm => {
        Logger.debug("Got a form error")
        val fErrorMessage = "Form Error:" + formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vLessonLinks => {
        request.body.file("lessonFile").map { lessonFile =>
          if (vLessonLinks.validate) {
            val lessonId = vLessonLinks.vLesson
            val vLesson = SqlLessons.select(lessonId)
            val lessonNumber = vLesson.vLessonNumber
            val lessonString = lessonNumber match {
              case x if x < 10 => "0" + lessonNumber.toString
              case _ => lessonNumber.toString
            }
            val courseId = vLesson.vidCourse
            val courseIdNumber = SqlCourses.select(courseId).vCourseIDNumber
            val filename = Globals.webDavServer + "Courses/" + Globals.term + "/" + 
            		courseIdNumber + "/Lessons/Lesson" + lessonString + "/" + lessonFile.filename
            val path = filename.replaceAll(" ", "%20")
            Logger.debug(path)
            val contentType = lessonFile.contentType
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              val inputStream = new FileInputStream(lessonFile.ref.file)
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
                val v2LessonLinks = new MdlLessonLinks(
                  vLessonLinks.vLessonLinkNumber,
                  path,
                  vLessonLinks.vDescription,
                  vLessonLinks.vIsFileLiink,
                  vLessonLinks.vLesson,
                  vLessonLinks.vFacultyEmail)
                println(vLessonLinks.vLink)
                newEntry match {
                  case 0 => SqlLessonLinks.update(v2LessonLinks)
                  case _ => SqlLessonLinks.insert(v2LessonLinks)
                }           
              Redirect(routes.LessonLinksController.listLessonLinks(vLessonLinks.vLesson))
              case Some(badResult) =>
                Logger.debug("WebDav upload error")
                badResult
            }
          } else {
        	  Logger.debug("Got a validation error")
              val validationErrors = "Lesson Link validation error:" + vLessonLinks.validationErrors
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

  def saveLessonLinks(newEntry: Int) = Action { implicit request =>
  	formLessonLinks.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vLessonLinks => {
        if (vLessonLinks.validate) {
          newEntry match {
            case 0 => SqlLessonLinks.update(vLessonLinks)
            case _ => SqlLessonLinks.insert(vLessonLinks)
          }
          Redirect(routes.LessonLinksController.listLessonLinks(vLessonLinks.vLesson))
        } else {
          val validationErrors = vLessonLinks.validationErrors
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
    errMess("Error Messages:\n", formLessonLinks.errors.toList)
  }
}
