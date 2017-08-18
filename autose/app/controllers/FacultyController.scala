
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
import java.io._

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult

import scala.util.{Failure, Success, Try}

object FacultyController extends Base {

  val formFaculty = Form[MdlFaculty](
    mapping (
	"fidFaculty" -> of[Long],
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
      

  def listFaculty = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewlist.html.listFaculty(SqlFaculty.all))
  }

   def editFaculty(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formFaculty(formFaculty.fill(SqlFaculty.select(id)), 0))
  }

   def showFaculty(id: Long) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    Ok(viewshow.html.showFaculty(SqlFaculty.select(id)))
  }

   def deleteFaculty(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    SqlFaculty.delete(id)
    Ok(viewlist.html.listFaculty(SqlFaculty.all))
  }

  def createFaculty = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
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
  
  def uploadFaculty = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    val vFaculty = new MdlFaculty
    Ok(viewforms.html.formFacultyFile(formFaculty.fill(vFaculty), 1))
  }
  
  def newFacultyPhoto(vidFaculty: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(viewforms.html.formNewFacultyPicture(vidFaculty))
  }
  
  def postNewFacultyPicture(vidFaculty: Long)= Action(parse.multipartFormData) { implicit request =>
      request.body.file("facultyPictureFile").map { facultyPictureFile =>
            val vFaculty = SqlFaculty.select(vidFaculty)
            val fileExtension = facultyPictureFile.filename.substring(facultyPictureFile.filename.lastIndexOf("."))
            val path = "FacultyPhotos/" + vFaculty.vLastName + vFaculty.vFirstName + fileExtension
            //val path = filename.replaceAll(" ", "%20")
            val contentType = facultyPictureFile.contentType.getOrElse(Globals.defaultContentType)

        /*
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              Logger.debug("Path is " + path)
              if (sardine.exists(path)) sardine.delete(path)
              Logger.debug("Path delete successful")
              val inputStream = new FileInputStream(facultyPictureFile.ref.file)
              Logger.debug("Uploading new photo")              
              contentType match  {
                case Some(cType) => sardine.put(path, inputStream, cType)
                case None => sardine.put(path, inputStream)
              }
              None
            } 
            catch {
              case e: Exception => Some(BadRequest(viewforms.html.formError(e.getMessage, request.headers("REFERER"))))
            }*/
        val upload: Try[CompleteMultipartUploadResult] = AmazonS3Controller.uploadS3FileFuture(path, facultyPictureFile.ref.file, contentType)

        upload match { // Only update the database if there is no file error
          case Success(m) =>
                val newFaculty = vFaculty.updatePhotoFile(path)
                SqlFaculty.update(newFaculty)     
                Redirect(routes.FacultyController.listFaculty)
          case Failure(ex) =>
            Logger.debug(ex.getMessage)
            Results.NotImplemented(ex.getMessage)
        }

    }.getOrElse {
        Logger.debug("File upload error")
        Results.NotImplemented("File upload error")
    }
  }

  def postFaculty(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    formFaculty.bindFromRequest().fold(
      errFrm => {
        Logger.debug("Got a form error")
        val fErrorMessage = "Form Error:" + formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vFaculty => {
        request.body.file("facultyPictureFile").map { facultyPictureFile =>
          if (vFaculty.validate) {
            val fileExtension = facultyPictureFile.filename.substring(facultyPictureFile.filename.lastIndexOf("."))
            val path = "FacultyPhotos/" + vFaculty.vLastName + vFaculty.vFirstName + fileExtension
            //val path = filename.replaceAll(" ", "%20")
            val contentType = facultyPictureFile.contentType.getOrElse(Globals.defaultContentType)
            /*
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              val inputStream = new FileInputStream(facultyPictureFile.ref.file)
              contentType match  {
                case Some(cType) => sardine.put(path, inputStream, cType)
                case None => sardine.put(path, inputStream)
              }
              None
            } 
            catch {
              case e: Exception => Some(BadRequest(viewforms.html.formError(e.getMessage, request.headers("REFERER"))))
            }*/
            val upload: Try[CompleteMultipartUploadResult] = AmazonS3Controller.uploadS3FileFuture(path, facultyPictureFile.ref.file, contentType)

            upload match { // Only update the database if there is no file error
              case Success(m) =>
              val v2Faculty: MdlFaculty = new MdlFaculty(
                  vFaculty.vidFaculty,
                  vFaculty.vLastName,
                  vFaculty.vFirstName,
                  vFaculty.vTitle,
                  vFaculty.vOfficeNumber,
                  vFaculty.vOfficePhone,
                  vFaculty.vBranchofService,
                  vFaculty.vEmail,
                  path,
                  vFaculty.vBiography)
                newEntry match {
                  case 0 => SqlFaculty.update(v2Faculty)
                  case _ => SqlFaculty.insert(v2Faculty)
                }           
              Redirect(routes.FacultyController.listFaculty)
              case Failure(ex) =>
                Logger.debug(ex.getMessage)
                Results.NotImplemented(ex.getMessage)
            }
          } else {
        	  Logger.debug("Got a validation error")
              val validationErrors = "Lesson Link validation error:" + vFaculty.validationErrors
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

    
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", formFaculty.errors.toList)
  }
  
  def getFacultyListForCourse(idCourse: Long): List[(String, String)] = {
    val courseOfferingList1 = SqlCourseOfferings.selectWhere("`Course` = " + idCourse).map(_.vInstructor)
    val courseOfferingList = courseOfferingList1.distinct
    val identifierList: List[(String, String)] = courseOfferingList.isEmpty match {
      case false => {
        val inClause = courseOfferingList.mkString(", ")
        //println("Faculty Query: " + inClause)
        val facultyList = SqlFaculty.selectWhere("idFaculty IN (" + inClause + ")").sortWith((a:MdlFaculty, b:MdlFaculty) => a.vLastName < b.vLastName)
        val identifierList = facultyList.map(_.selectIdentifier)
        identifierList.+:(("-1", "Course level file"))
      }
      case true => List(("-1", "Course level file"))
    }
    identifierList
  }

}
