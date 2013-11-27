package controllers

import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import jp.t2v.lab.play2.auth._
import slick.AppDB

//import play.api.mvc._

object ProgramsController extends ControllerTrait[Long, MdlPrograms, Long] with Base with OptionalAuthElement {
  
  
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
  
	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPrograms(getAll(ffk))
 
	override def listFunction(item: MdlPrograms)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPrograms(getAll(item))
 
	override def showFunction(vMdlPrograms: MdlPrograms)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showPrograms(vMdlPrograms)
	
	override def editFunction(mdlProgramsForm: Form[MdlPrograms]): Html = 
	  views.html.viewforms.formPrograms(mdlProgramsForm, 0)
	
	override def createFunction(mdlProgramsForm: Form[MdlPrograms]): Html = 
	  views.html.viewforms.formPrograms(mdlProgramsForm, 1)
	  
	def crud = slick.AppDB.dal.Programs
	def newItem(fkId: Long) = new MdlPrograms
	
	def se = AppDB.dal.Programs.all.find(p => p.vProgram == "SE").get
  	def em = AppDB.dal.Programs.all.find(p => p.vProgram == "EM").get
  	
  	def seId = se.vidPrograms.get
  	def emId = em.vidPrograms.get



}