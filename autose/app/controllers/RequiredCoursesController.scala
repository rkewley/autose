
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

object RequiredCoursesController extends ControllerTrait[Long, MdlRequiredCourses, Long] with Base with OptionalAuthElement {

  val form = Form[MdlRequiredCourses](
    mapping (
	"fidRequiredCourses" -> optional(of[Long]),
	"fProgram" -> of[Long],
	"fCourse" -> text
    )(MdlRequiredCourses.apply)(MdlRequiredCourses.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listRequiredCourses(getAll(ffk), ffk, form.fill(newItem(ffk)), generateElectiveGroupCourseForms(ffk))
 
	override def listFunction(item: MdlRequiredCourses)(implicit user: MdlUser): Html =
	  views.html.viewlist.listRequiredCourses(getAll(item), item.vProgram, form.fill(newItem(item.vProgram)), generateElectiveGroupCourseForms(item.vProgram))
 
	override def showFunction(vRequiredCourses: MdlRequiredCourses): Html =
	  views.html.viewshow.showRequiredCourses(vRequiredCourses)
	
	override def editFunction(mdlRequiredCoursesForm: Form[MdlRequiredCourses]): Html = 
	  views.html.viewforms.formRequiredCourses(mdlRequiredCoursesForm, 0)
	
	override def createFunction(mdlRequiredCoursesForm: Form[MdlRequiredCourses]): Html = 
	  views.html.viewforms.formRequiredCourses(mdlRequiredCoursesForm, 1)
	  
	def crud = slick.AppDB.dal.RequiredCourses
	
	//  This method generates one blank ElectiveGroupCourse.form for each elective group in the program
	def generateElectiveGroupCourseForms(programId: Long): List[Form[MdlElectiveGroupCourse]] = {
	  AppDB.database.withSession { implicit session: Session => 
	  val forms = AppDB.dal.ElectiveGroup.allQuery.filter(eg => eg.vProgram === programId).elements.toList.map {  eg =>
	    ElectiveGroupCourseController.form.fill(ElectiveGroupCourseController.newItem(eg.vidElectiveGroup.get))
	  }
	  forms
	  }	  
  	}

    def newItem(fkId: Long): MdlRequiredCourses = new MdlRequiredCourses(Option(0), fkId, "")
    
    override def getAll(fkId: Long): List[MdlRequiredCourses] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.RequiredCourses.allQuery.filter(v1RequiredCourses => v1RequiredCourses.vProgram === fkId).elements.toList
    }
    
    override def getAll(vRequiredCourses: MdlRequiredCourses): List[MdlRequiredCourses] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.RequiredCourses.allQuery.filter(v1RequiredCourses => v1RequiredCourses.vProgram === vRequiredCourses.vProgram).elements.toList
    }
    
    def requiredForProgram(courseName: String, programId: Long): Boolean = {
      // Query the required courses for Program == programId and see there exists a course where c.vCourse == courseName
      AppDB.dal.RequiredCourses.all.filter(c => c.vProgram == programId).exists(c => c.vCourse == courseName)
    }
  }
