
package controllers

import play.api._
import play.api.templates._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import play.api.libs.Files._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._
import java.io._

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult
import play.Logger

import scala.util.{Failure, Success, Try}

object GradedEventAMSLinksController extends ControllerTrait[Long, MdlGradedEventAMSLinks, Long] with Base with OptionalAuthElement {

  val form = Form[MdlGradedEventAMSLinks](
    mapping (
	"fidGradedEventAMSLinks" -> optional(of[Long]),
	"fLink" -> text,
	"fDescription" -> text,
	"fIsFileLink" -> of[Boolean],
	"fvGradedEventAMS" -> of[Long]
    )(MdlGradedEventAMSLinks.apply)(MdlGradedEventAMSLinks.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listGradedEventAMSLinks(getAll(ffk), ffk)
 
	override def listFunction(item: MdlGradedEventAMSLinks)(implicit user: MdlUser): Html =
	  views.html.viewlist.listGradedEventAMSLinks(getAll(item), item.vvGradedEventAMS)
 
	override def showFunction(vGradedEventAMSLinks: MdlGradedEventAMSLinks): Html =
	  views.html.viewshow.showGradedEventAMSLinks(vGradedEventAMSLinks)
	
	override def editFunction(mdlGradedEventAMSLinksForm: Form[MdlGradedEventAMSLinks]): Html = 
	  views.html.viewforms.formGradedEventAMSLinks(mdlGradedEventAMSLinksForm, 0)
	
	override def createFunction(mdlGradedEventAMSLinksForm: Form[MdlGradedEventAMSLinks]): Html = 
	  views.html.viewforms.formGradedEventAMSLinks(mdlGradedEventAMSLinksForm, 1)
	  
	def crud = slick.AppDB.dal.GradedEventAMSLinks

  def uploadGradedEventFile(gradedEventAMSId: Long) = compositeAction(Faculty) { user =>
    implicit template => implicit request =>
      val vGradedEventLinks = new MdlGradedEventAMSLinks(Option(0), "http:/", "", true, gradedEventAMSId)
      Ok(views.html.viewforms.formGradedEventAMSFiles(form.fill(vGradedEventLinks), 1))
  }
  
    def importLinksFromOnlineGradedEvents = compositeAction(Faculty){ implicit user => implicit template => implicit request =>
      // pull all KSA mappings from sub-events
      val gradedEventMappings = AppDB.dal.MappingGradedEvent.all
      val gradedRequiementLinks = AppDB.dal.GradedRequirementLinks.all
      val gradedEventAMSLinks = AppDB.dal.GradedEventAMSLinks.all
      
      gradedRequiementLinks.foreach { gradedRequiementLink =>
        // see if there is a mapping for this graded event in the mappings file
        gradedEventMappings.find {mapping => mapping.vGradedEvent == gradedRequiementLink.vGradedRequirement} match {
          case Some(mapping) => {
            // Make sure we don't insert a duplicate record
            if (gradedEventAMSLinks.find{g => g.vvGradedEventAMS == mapping.vGradedEventAMS && g.vLink == gradedRequiementLink.vLink}.isEmpty) {
              AppDB.dal.GradedEventAMSLinks.insert(MdlGradedEventAMSLinks(Some(0), gradedRequiementLink.vLink, gradedRequiementLink.vDescription, gradedRequiementLink.vIsFileLink, mapping.vGradedEventAMS))       
            }
          }
          case None =>
        }
      }
      Redirect(routes.FacultyController.listFaculty)
    }
  

  def postFile(newEntry: Int): Action[MultipartFormData[TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    form.bindFromRequest().fold(
      errFrm => {
        val fErrorMessage = formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vGradedEventAMSLinks => {
        request.body.file("gradedEventAMSFile").map { gradedEventAMSFile =>
          if (vGradedEventAMSLinks.validate) {
            val vGradedEventAMS = AppDB.dal.GradedEventAMS.select(vGradedEventAMSLinks.vvGradedEventAMS).get
            val course = slick.AppDB.dal.Courses.select(vGradedEventAMS.vCourse).get
            val courseIdNumber = course.vCourseIDNumber
            val term = "AT" + (course.vAcademicYear - 2000) + "-" + course.vAcademicTerm
            val directorypath = "Courses/" + term + "/" + courseIdNumber + "/GradedRequirementFiles/"
            //val directorypath = directory.replaceAll(" ", "%20")
            val filepath = directorypath + gradedEventAMSFile.filename
            //val filepath = filename.replaceAll(" ", "%20")
            Logger.debug(filepath)
            val contentType = gradedEventAMSFile.contentType.getOrElse(Globals.defaultContentType)
            /*
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              if (!sardine.exists(directorypath)) sardine.createDirectory(directorypath)
              val inputStream = new FileInputStream(gradedEventAMSFile.ref.file)
              contentType match {
                case Some(cType) => sardine.put(filepath, inputStream, cType)
                case None => sardine.put(filepath, inputStream)
              }
              None
            } catch {
              case e: Exception => Some(BadRequest(viewforms.html.formError(e.getMessage, request.headers("REFERER"))))
            }*/
            val upload: Try[CompleteMultipartUploadResult] = AmazonS3Controller.uploadS3FileFuture(filepath, gradedEventAMSFile.ref.file, contentType)
            upload match { // Only update the database if there is no file error
              case Success(m) =>
                val v2GradedEventAMSLinks = new MdlGradedEventAMSLinks(
                  vGradedEventAMSLinks.vidGradedEventAMSLinks,
                  filepath,
                  vGradedEventAMSLinks.vDescription,
                  vGradedEventAMSLinks.vIsFileLink,
                  vGradedEventAMSLinks.vvGradedEventAMS)
                newEntry match {
                  case 0 => AppDB.dal.GradedEventAMSLinks.update(v2GradedEventAMSLinks)
                  case _ => AppDB.dal.GradedEventAMSLinks.insert(v2GradedEventAMSLinks)
                }
                Redirect(routes.GradedEventAMSLinksController.list(v2GradedEventAMSLinks.vvGradedEventAMS))
              case Failure(ex) =>
                Logger.debug(ex.getMessage)
                Results.NotImplemented(ex.getMessage)
            }
          } else {
            val validationErrors = vGradedEventAMSLinks.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        }.getOrElse {
          BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
        }
      })
  }

    def newItem(fkId: Long): MdlGradedEventAMSLinks = new MdlGradedEventAMSLinks(Option(0), "", "", false, fkId)
    
    override def getAll(fkId: Long): List[MdlGradedEventAMSLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.GradedEventAMSLinks.allQuery.filter(v1GradedEventAMSLinks => v1GradedEventAMSLinks.vvGradedEventAMS === fkId).elements.toList
    }
    
    override def getAll(vGradedEventAMSLinks: MdlGradedEventAMSLinks): List[MdlGradedEventAMSLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.GradedEventAMSLinks.allQuery.filter(v1GradedEventAMSLinks => v1GradedEventAMSLinks.vvGradedEventAMS === vGradedEventAMSLinks.vvGradedEventAMS).elements.toList
    }
  }
