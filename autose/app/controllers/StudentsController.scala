
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
import jp.t2v.lab.play2.auth._

object StudentsController extends ControllerTrait[Long, MdlStudents, Long] with Base with OptionalAuthElement {

  val form = Form[MdlStudents](
    mapping (
	"fidStudents" -> optional(of[Long]),
	"fStudentId" -> text,
	"fLastname" -> text,
	"fFirstname" -> text,
	"fMajorAMS" -> text,
	"fGradYear" -> of[Long],
	"fProgram" -> of[Long]
    )(MdlStudents.apply)(MdlStudents.unapply)
  )
      
	override def list(ffk: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
	  Ok(views.html.viewlist.listStudents(getAll(ffk)))
	}
  
    override def show(id: Long) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[MdlStudents] =>
          Ok(views.html.viewshow.showStudents(item.get))
        case None =>
          badRequest("Item with key " + id + " not found in database", request)
      }
	}
	
	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listStudents(getAll(ffk))
 
	override def listFunction(item: MdlStudents)(implicit user: MdlUser): Html =
	  views.html.viewlist.listStudents(getAll(item))
 
	override def showFunction(vStudents: MdlStudents): Html =
	  views.html.viewshow.showStudents(vStudents)
	
	override def editFunction(mdlStudentsForm: Form[MdlStudents]): Html = 
	  views.html.viewforms.formStudents(mdlStudentsForm, 0)
	
	override def createFunction(mdlStudentsForm: Form[MdlStudents]): Html = 
	  views.html.viewforms.formStudents(mdlStudentsForm, 1)
	  
	def crud = slick.AppDB.dal.Students


	def newItem(fkId: Long) = new MdlStudents
	
  def selectUploadType = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewlist.listStudents(getAll(0)))
  }
	
  def uploadStudents = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(views.html.viewforms.formStudentsUpload(form.fill(newItem(0))))
  }

  def saveStudentUpload = Action(parse.multipartFormData) { implicit request =>
  	request.body.file("studentsFile").map { studentsFile =>
  	  	println("Adding students")
  	  	addStudentsFromFile(studentsFile.ref.file)
  		Redirect(routes.StudentsController.list(0))
  	}.getOrElse {
  		BadRequest(viewforms.html.formError("File upload error", request.headers("REFERRER")))
  	}
  }

  def addStudentsFromFile(studentsFile: java.io.File) { 
    val lines = scala.io.Source.fromFile(studentsFile).getLines.toList.tail
    val allStudents = AppDB.dal.Students.all.map(_.vStudentId)
    val programs = AppDB.dal.Programs.all.map(_.selectIdentifier)
    lines.foreach { line =>
      val values = line.split(",")
      if (values.length == 5) {
        val student = new MdlStudents(Option(0), values(0), values(1), values(2), values(4), values(3).toInt, 0)
        println("Student: " + student)
        val programs = AppDB.dal.Programs.all
        if (!allStudents.contains(student.vStudentId)) {
          val major: Long = student.vMajorAMS match {
            case x if x.startsWith("Engineering Management") => 2
            case x if x.startsWith("Systems Engineering") => 3
            case x if x.startsWith("Systems Management") => 5
            case x if x.startsWith("Systems Design") => 5
            case x if x.startsWith("Operations Research") => 7
            case _ => 8
          }
          val updatedStudent = new MdlStudents(Option(0), student.vStudentId, student.vLastname, student.vFirstname, student.vMajorAMS, student.vGradYear, major)
          println("Updated Student: " + updatedStudent)
          AppDB.dal.Students.insert(updatedStudent)
        }

      }
    }
  }
	
  }