package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import slick._
//import play.api.mvc._

object ProgramsController extends ControllerTrait[Long, MdlPrograms] with Base {
  
  
  def form = Form[MdlPrograms](
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
  
	def listFunction = viewlist.html.listPrograms.f
	def showFunction: displayType
	def editFunction: formType
	def createFunction: displayType
	def crud = AppDB.dal.Programs
	def newItem: Mdl[K]
  

}