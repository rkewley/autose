
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

object DefinitionsController extends ControllerTrait[Long, MdlDefinitions, Long] with Base {

  val form = Form[MdlDefinitions](
    mapping (
	"fidDefinitions" -> optional(of[Long]),
	"fWord" -> text,
	"fDefinition" -> text
    )(MdlDefinitions.apply)(MdlDefinitions.unapply)
  )
      

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listDefinitions(getAll(ffk))
 
	override def listFunction(item: MdlDefinitions): Html = 
	  views.html.viewlist.listDefinitions(getAll(item))
 
	override def showFunction(vDefinitions: MdlDefinitions): Html = 
	  views.html.viewshow.showDefinitions(vDefinitions)
	
	override def editFunction(mdlDefinitionsForm: Form[MdlDefinitions]): Html = 
	  views.html.viewforms.formDefinitions(mdlDefinitionsForm, 0)
	
	override def createFunction(mdlDefinitionsForm: Form[MdlDefinitions]): Html = 
	  views.html.viewforms.formDefinitions(mdlDefinitionsForm, 1)
	  
	def crud = slick.AppDB.dal.Definitions


	def newItem(fkId: Long) = new MdlDefinitions
  }