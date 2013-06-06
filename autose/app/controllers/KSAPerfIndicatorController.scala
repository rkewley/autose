
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

object KSAPerfIndicatorController extends ControllerTrait[Long, MdlKSAPerfIndicator, Long] with Base {

  val form = Form[MdlKSAPerfIndicator](
    mapping (
	"fidKSAPerfIndicator" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fPerformanceIndicator" -> of[Long]
    )(MdlKSAPerfIndicator.apply)(MdlKSAPerfIndicator.unapply)
  )
      

	override def listFunction(ffk: Long): Html = 
	  views.html.viewlist.listKSAPerfIndicator(getAll(ffk))
 
	override def listFunction(item: MdlKSAPerfIndicator): Html = 
	  views.html.viewlist.listKSAPerfIndicator(getAll(item))
 
	override def showFunction(vKSAPerfIndicator: MdlKSAPerfIndicator): Html = 
	  views.html.viewshow.showKSAPerfIndicator(vKSAPerfIndicator)
	
	override def editFunction(mdlKSAPerfIndicatorForm: Form[MdlKSAPerfIndicator]): Html = 
	  views.html.viewforms.formKSAPerfIndicator(mdlKSAPerfIndicatorForm, 0)
	
	override def createFunction(mdlKSAPerfIndicatorForm: Form[MdlKSAPerfIndicator]): Html = 
	  views.html.viewforms.formKSAPerfIndicator(mdlKSAPerfIndicatorForm, 1)
	  
	def crud = slick.AppDB.dal.KSAPerfIndicator


	def newItem(fkId: Long) = new MdlKSAPerfIndicator
  }