
package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._

object ElectiveGroupCourseController extends ControllerTrait[Long, MdlElectiveGroupCourse, Long] with Base with OptionalAuthElement {

  val form = Form[MdlElectiveGroupCourse](
    mapping (
	"fidElectiveGroupCourse" -> optional(of[Long]),
	"fElectiveGroup" -> of[Long],
	"fCourse" -> text,
	"fRequired" -> of[Boolean]
    )(MdlElectiveGroupCourse.apply)(MdlElectiveGroupCourse.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listElectiveGroupCourse(getAll(ffk), ffk)
 
	override def listFunction(item: MdlElectiveGroupCourse)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listElectiveGroupCourse(getAll(item), item.vElectiveGroup)
 
	override def showFunction(vElectiveGroupCourse: MdlElectiveGroupCourse)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showElectiveGroupCourse(vElectiveGroupCourse)
	
	override def editFunction(mdlElectiveGroupCourseForm: Form[MdlElectiveGroupCourse]): Html = 
	  views.html.viewforms.formElectiveGroupCourse(mdlElectiveGroupCourseForm, 0)
	
	override def createFunction(mdlElectiveGroupCourseForm: Form[MdlElectiveGroupCourse]): Html = 
	  views.html.viewforms.formElectiveGroupCourse(mdlElectiveGroupCourseForm, 1)
	  
	def crud = slick.AppDB.dal.ElectiveGroupCourse


    def newItem(fkId: Long): MdlElectiveGroupCourse = new MdlElectiveGroupCourse(Option(0), fkId, "", false)
    
    override def getAll(fkId: Long): List[MdlElectiveGroupCourse] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ElectiveGroupCourse.allQuery.filter(v1ElectiveGroupCourse => v1ElectiveGroupCourse.vElectiveGroup === fkId).elements.toList
    }
    
    override def getAll(vElectiveGroupCourse: MdlElectiveGroupCourse): List[MdlElectiveGroupCourse] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ElectiveGroupCourse.allQuery.filter(v1ElectiveGroupCourse => v1ElectiveGroupCourse.vElectiveGroup === vElectiveGroupCourse.vElectiveGroup).elements.toList
    }
    
  override def save(newEntry: Int) = StackAction { implicit request =>
  	form.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      formItem => {
        val item = formItem
        if (item.validate) {
          println("Inserting or updating item")
          newEntry match {
            case 0 => 
              println("updating item")
              crud.update(item)
            case _ => 
              println("inserting item")
              crud.insert(item)
          }
          Redirect(routes.RequiredCoursesController.list(AppDB.dal.ElectiveGroup.select(item.vElectiveGroup).get.vProgram))
        } else {
          val validationErrors = item.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
  
   override def delete(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[MdlElectiveGroupCourse] =>
          crud.delete(id)
          implicit val userOption = Some(user)
          Redirect(routes.RequiredCoursesController.list(AppDB.dal.ElectiveGroup.select(item.get.vElectiveGroup).get.vProgram))
        case None =>
          badRequest("Programs with key " + id + " not found in database", request)
      }
  }
   
    def electiveForProgram(courseName: String, programId: Long): Boolean = {
      // Query the required courses for Program == programId and see there exists a course where c.vCourse == courseName
      AppDB.dal.ElectiveGroup.all.filter(eg => eg.vProgram == programId).flatMap { electiveGroup =>
        AppDB.dal.ElectiveGroupCourse.coursesForElectiveGroup(electiveGroup.vidElectiveGroup.get)
      }.exists(c => c.vCourse == courseName)
    }


    
  }
