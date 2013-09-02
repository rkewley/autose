
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
import java.io._

object ReferenceController extends Base {

  val formReference = Form[MdlReference](
    mapping (
	"fidReference" -> of[Long],
	"fTitle" -> text,
	"fText" -> text,
	"fLink" -> text
    )(MdlReference.apply)(MdlReference.unapply)
  )
      

  def listReference = Action {
    Ok(viewlist.html.listReference(SqlReference.all.sortWith(MdlReference.compare)))
  }

   def editReference(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formReference(formReference.fill(SqlReference.select(id)), 0))
  }

   def showReference(id: Long) = Action {
    Ok(viewshow.html.showReference(SqlReference.select(id)))
  }

   def deleteReference(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    SqlReference.delete(id)
    Ok(viewlist.html.listReference(SqlReference.all))
  }

  def createReference = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(viewforms.html.formReference(formReference.fill(new MdlReference(0, "", "", "http://")), 1))
  }
  
  def uploadReferenceFile = compositeAction(NormalUser) { user => implicit template => implicit request =>
    val vReference = new MdlReference()
    Ok(viewforms.html.formReferenceFile(formReference.fill(vReference), 1))
  }

  def postReferenceFile(newEntry: Int) = Action(parse.multipartFormData) { implicit request =>
    formReference.bindFromRequest().fold(
      errFrm => {
        Logger.debug("Got a form error")
        val fErrorMessage = "Form Error:" + formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vReference => {
        request.body.file("referenceFile").map { referenceFile =>
          if (vReference.validate) {
            val filename = Globals.webDavServer + "References/" + referenceFile.filename
            val path = filename.replaceAll(" ", "%20")
            Logger.debug(path)
            val contentType = referenceFile.contentType
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              val inputStream = new FileInputStream(referenceFile.ref.file)
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
                val v2Reference = new MdlReference(
                  vReference.vidReference,
                  vReference.vTitle,
                  vReference.vText,
                  path)
                newEntry match {
                  case 0 => SqlReference.update(v2Reference)
                  case _ => SqlReference.insert(v2Reference)
                }           
              Redirect(routes.ReferenceController.listReference)
              case Some(badResult) =>
                Logger.debug("WebDav upload error")
                badResult
            }
          } else {
        	  Logger.debug("Got a validation error")
              val validationErrors = "Reference validation error:" + vReference.validationErrors
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

  

  def saveReference(newEntry: Int) = Action { implicit request =>
  	formReference.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vReference => {
        if (vReference.validate) {
          newEntry match {
            case 0 => SqlReference.update(vReference)
            case _ => SqlReference.insert(vReference)
          }
          Redirect(routes.ReferenceController.listReference)
        } else {
          val validationErrors = vReference.validationErrors
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
    errMess("Error Messages:\n", formReference.errors.toList)
  }
}
