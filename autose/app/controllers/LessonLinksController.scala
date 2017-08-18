
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._
import java.io.FileInputStream

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult
import jp.t2v.lab.play2.auth._
import play.Logger

import scala.util.{Failure, Success, Try}

object LessonLinksController extends ControllerTrait[Long, MdlLessonLinks, Long] with Base with OptionalAuthElement {

  val form = Form[MdlLessonLinks](
    mapping (
	"fLessonLinkNumber" -> optional(of[Long]),
	"fLink" -> text,
	"fDescription" -> text,
	"fIsFileLiink" -> of[Boolean],
	"fLesson" -> of[Long],
	"fFaculty" -> of[Long]
    )(MdlLessonLinks.apply)(MdlLessonLinks.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listLessonLinks(getAll(ffk), ffk)
 
	override def listFunction(item: MdlLessonLinks)(implicit user: MdlUser): Html =
	  views.html.viewlist.listLessonLinks(getAll(item), item.vLesson)
 
	override def showFunction(vLessonLinks: MdlLessonLinks): Html =
	  views.html.viewshow.showLessonLinks(vLessonLinks)
	
	override def editFunction(mdlLessonLinksForm: Form[MdlLessonLinks]): Html = 
	  views.html.viewforms.formLessonLinks(mdlLessonLinksForm, 0)
	
	override def createFunction(mdlLessonLinksForm: Form[MdlLessonLinks]): Html = 
	  views.html.viewforms.formLessonLinks(mdlLessonLinksForm, 1)
	  
  override def delete(id: Long) = {
    val vLessonLinks = crud.select(id).get
    val returnVal = super.delete(id)
    /*
      if (vLessonLinks.vIsFileLiink) {
        val sardine = SardineFactory.begin("seweb", "G0Systems!")
        try {
          if (sardine.exists(vLessonLinks.vLink)) {
            sardine.delete(vLessonLinks.vLink)
          }
        } catch {
          case e: Exception => {
            println("Could not delete file " + vLessonLinks.vLink)
          }
        }
      }
      * 
      */
    returnVal
  }

  def uploadLessonFile(idLessons: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vLessonLinks = new MdlLessonLinks(Option(0), "", "", true, idLessons, -1)
    Ok(views.html.viewforms.formLessonFiles(form.fill(vLessonLinks), 1))
  }

  def postLessonFile(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    form.bindFromRequest().fold(
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
            val vLesson = AppDB.dal.Lessons.select(lessonId).get
            val lessonNumber = vLesson.vLessonNumber
            val lessonString = lessonNumber match {
              case x if x < 10 => "0" + lessonNumber.toString
              case _ => lessonNumber.toString
            }
            val course = slick.AppDB.dal.Courses.select(vLesson.vidCourse).get
            val courseIdNumber = course.vCourseIDNumber
            val term = "AT" + (course.vAcademicYear - 2000) + "-" + course.vAcademicTerm
            val path = "Courses/" + term + "/" +
            		courseIdNumber + "/Lessons/Lesson" + lessonString + "/" + lessonFile.filename
            //val path = filename.replaceAll(" ", "%20")
            //Logger.debug(path)
            val contentType = lessonFile.contentType.getOrElse(Globals.defaultContentType)
            /*
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
            }*/
            val upload: Try[CompleteMultipartUploadResult] = AmazonS3Controller.uploadS3FileFuture(path, lessonFile.ref.file, contentType)
            upload match { // Only update the database if there is no file error
              case Success(m) =>
                val v2LessonLinks = new MdlLessonLinks(
                  vLessonLinks.vLessonLinkNumber,
                  path,
                  vLessonLinks.vDescription,
                  vLessonLinks.vIsFileLiink,
                  vLessonLinks.vLesson,
                  vLessonLinks.vFaculty)
                println(vLessonLinks.vLink)
                newEntry match {
                  case 0 => crud.update(v2LessonLinks)
                  case _ => crud.insert(v2LessonLinks)
                }           
              Redirect(routes.LessonLinksController.list(vLessonLinks.vLesson))
              case Failure(ex) =>
                Logger.debug(ex.getMessage)
                Results.NotImplemented(ex.getMessage)
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

	  
	def crud = slick.AppDB.dal.LessonLinks


    def newItem(fkId: Long): MdlLessonLinks = new MdlLessonLinks(Option(0), "", "", false, fkId, 0)
    
    override def getAll(fkId: Long): List[MdlLessonLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.LessonLinks.allQuery.filter(v1LessonLinks => v1LessonLinks.vLesson === fkId).elements.toList
    }
    
    override def getAll(vLessonLinks: MdlLessonLinks): List[MdlLessonLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.LessonLinks.allQuery.filter(v1LessonLinks => v1LessonLinks.vLesson === vLessonLinks.vLesson).elements.toList
    }
  }
