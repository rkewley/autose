
package controllers

import play.api._
import play.api.templates._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import persistence._
import scala.slick.driver.MySQLDriver.simple._
import com.googlecode.sardine._
import play.api.libs.Files._
import java.io._
import jp.t2v.lab.play2.auth._

object GradedRequirementLinksController extends ControllerTrait[Long, MdlGradedRequirementLinks, Long] with Base with OptionalAuthElement {

  val form = Form[MdlGradedRequirementLinks](
    mapping(
      "fidGradedRequirementLinks" -> optional(of[Long]),
      "fLink" -> text,
      "fDescription" -> text,
      "fIsFileLink" -> of[Boolean],
      "fGradedRequirement" -> of[Long])(MdlGradedRequirementLinks.apply)(MdlGradedRequirementLinks.unapply))

  override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html =
    views.html.viewlist.listGradedRequirementLinks(getAll(ffk), ffk)

  override def listFunction(item: MdlGradedRequirementLinks)(implicit maybeUser: Option[MdlUser]): Html =
    views.html.viewlist.listGradedRequirementLinks(getAll(item), item.vGradedRequirement)

  override def showFunction(vGradedRequirementLinks: MdlGradedRequirementLinks)(implicit maybeUser: Option[MdlUser]): Html =
    views.html.viewshow.showGradedRequirementLinks(vGradedRequirementLinks)

  override def editFunction(mdlGradedRequirementLinksForm: Form[MdlGradedRequirementLinks]): Html =
    views.html.viewforms.formGradedRequirementLinks(mdlGradedRequirementLinksForm, 0)

  override def createFunction(mdlGradedRequirementLinksForm: Form[MdlGradedRequirementLinks]): Html =
    views.html.viewforms.formGradedRequirementLinks(mdlGradedRequirementLinksForm, 1)

  override def delete(id: Long) = {
    val gradedRequirementLink = crud.select(id).get
    if (gradedRequirementLink.vIsFileLink) {
      val sardine = SardineFactory.begin("seweb", "G0Systems!")
      try {
        if (sardine.exists(gradedRequirementLink.vLink)) {
          sardine.delete(gradedRequirementLink.vLink)
        }
      } catch {
        case e: Exception => {
          println("Could not delete file " + gradedRequirementLink.vLink)
        }
      }
    }
    super.delete(id)
  }

  def uploadGradedEventFile(gradedEventAMSId: Long) = compositeAction(NormalUser) { user =>
    implicit template => implicit request =>
      val vGradedEventLinks = new MdlGradedRequirementLinks(Option(0), "http:/", "", true, 0)
      Ok(views.html.viewforms.formGradedRequirementFiles(form.fill(vGradedEventLinks), 1))
  }

  def postFile(newEntry: Int): Action[MultipartFormData[TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    form.bindFromRequest().fold(
      errFrm => {
        val fErrorMessage = formErrorMessage(errFrm.errors)
        Logger.debug(fErrorMessage)
        BadRequest(viewforms.html.formError(fErrorMessage, request.headers("REFERER")))
      },
      vGradedRequirementLinks => {
        request.body.file("gradedRequirementFile").map { gradedRequirementFile =>
          if (vGradedRequirementLinks.validate) {
            val vGradedRequirements = AppDB.dal.GradedRequirements.select(vGradedRequirementLinks.vGradedRequirement).get
            val course = slick.AppDB.dal.Courses.select(vGradedRequirements.vCourse).get
            val courseIdNumber = course.vCourseIDNumber
            val term = "AT" + (course.vAcademicYear - 2000) + "-" + course.vAcademicTerm
            val directory = Globals.webDavServer + "Courses/" + term + "/" + courseIdNumber + "/GradedRequirementFiles/"
            val directorypath = directory.replaceAll(" ", "%20")
            val filename = directory + gradedRequirementFile.filename
            val filepath = filename.replaceAll(" ", "%20")
            Logger.debug(filepath)
            val contentType = gradedRequirementFile.contentType
            val sardine = SardineFactory.begin("seweb", "G0Systems!")
            val simpleResult = try {
              if (!sardine.exists(directorypath)) sardine.createDirectory(directorypath)
              val inputStream = new FileInputStream(gradedRequirementFile.ref.file)
              contentType match {
                case Some(cType) => sardine.put(filepath, inputStream, cType)
                case None => sardine.put(filepath, inputStream)
              }
              None
            } catch {
              case e: Exception => Some(BadRequest(viewforms.html.formError(e.getMessage, request.headers("REFERER"))))
            }
            simpleResult match { // Only update the database if there is no file error
              case None =>
                val v2GradedReqiurementLinks = new MdlGradedRequirementLinks(
                  vGradedRequirementLinks.vidGradedRequirementLinks,
                  filepath,
                  vGradedRequirementLinks.vDescription,
                  vGradedRequirementLinks.vIsFileLink,
                  vGradedRequirementLinks.vGradedRequirement)
                newEntry match {
                  case 0 => AppDB.dal.GradedRequirementLinks.update(v2GradedReqiurementLinks)
                  case _ => AppDB.dal.GradedRequirementLinks.insert(v2GradedReqiurementLinks)
                }
                Redirect(routes.GradedRequirementLinksController.list(vGradedRequirementLinks.vGradedRequirement))
              case Some(badResult) =>
                badResult
            }
          } else {
            val validationErrors = vGradedRequirementLinks.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        }.getOrElse {
          BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
        }
      })
  }

  def crud = slick.AppDB.dal.GradedRequirementLinks

  def newItem(fkId: Long): MdlGradedRequirementLinks = new MdlGradedRequirementLinks(Option(0), "", "", false, fkId)

  override def getAll(fkId: Long): List[MdlGradedRequirementLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
    AppDB.dal.GradedRequirementLinks.allQuery.filter(v1GradedRequirementLinks => v1GradedRequirementLinks.vGradedRequirement === fkId).elements.toList
  }

  override def getAll(vGradedRequirementLinks: MdlGradedRequirementLinks): List[MdlGradedRequirementLinks] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
    AppDB.dal.GradedRequirementLinks.allQuery.filter(v1GradedRequirementLinks => v1GradedRequirementLinks.vGradedRequirement === vGradedRequirementLinks.vGradedRequirement).elements.toList
  }
}
