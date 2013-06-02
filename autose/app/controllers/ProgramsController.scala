package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.MdlPrograms
import models.Mdl
import views._


//import play.api.mvc._

object ProgramsController extends ControllerTrait[Long, MdlPrograms] with Base {
  
  
  override def form = Form[MdlPrograms](
    mapping (
    "fidPrograms" -> optional(of[Long]),
	"fProgram" -> text,
	"fName" -> text,
	"fSlogan" -> text,
	"fInformation" -> text,
	"fProgramDirector" -> of[Long],
	"fDepartment" -> of[Long]
    )(MdlPrograms.apply)(MdlPrograms.unapply) 
  )
  
	override def listFunction(listMdlPrograms: List[MdlPrograms]): Html = 
	  views.html.viewlist.listPrograms(listMdlPrograms)
 
	override def showFunction(vMdlPrograms: MdlPrograms): Html = 
	  viewshow.html.showPrograms(vMdlPrograms)
	
	override def editFunction(mdlProgramsForm: Form[MdlPrograms]): Html = 
	  viewforms.html.formPrograms(mdlProgramsForm, 0)
	
	override def createFunction(mdlProgramsForm: Form[MdlPrograms]): Html = 
	  viewforms.html.formPrograms(mdlProgramsForm, 1)
	  
	def crud = slick.AppDB.dal.Programs
	def newItem = new MdlPrograms

}