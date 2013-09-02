
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

object PerformanceIndicatorController extends ControllerTrait[Long, MdlPerformanceIndicator, Long] with Base with OptionalAuthElement {

  val form = Form[MdlPerformanceIndicator](
    mapping (
	"fidPerformanceIndicator" -> optional(of[Long]),
	"fPerformanceIndicator" -> text,
	"fProgramOutcome" -> of[Long]
    )(MdlPerformanceIndicator.apply)(MdlPerformanceIndicator.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPerformanceIndicator(getAll(ffk), ffk)
 
	override def listFunction(item: MdlPerformanceIndicator)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listPerformanceIndicator(getAll(item), item.vProgramOutcome)
 
	override def showFunction(vPerformanceIndicator: MdlPerformanceIndicator)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showPerformanceIndicator(vPerformanceIndicator)
	
	override def editFunction(mdlPerformanceIndicatorForm: Form[MdlPerformanceIndicator]): Html = 
	  views.html.viewforms.formPerformanceIndicator(mdlPerformanceIndicatorForm, 0)
	
	override def createFunction(mdlPerformanceIndicatorForm: Form[MdlPerformanceIndicator]): Html = 
	  views.html.viewforms.formPerformanceIndicator(mdlPerformanceIndicatorForm, 1)
	  
	def crud = slick.AppDB.dal.PerformanceIndicator


    def newItem(fkId: Long): MdlPerformanceIndicator = new MdlPerformanceIndicator(Option(0), "", fkId)
    
    override def getAll(fkId: Long): List[MdlPerformanceIndicator] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.PerformanceIndicator.allQuery.filter(v1PerformanceIndicator => v1PerformanceIndicator.vProgramOutcome === fkId).elements.toList
    }
    
    override def getAll(vPerformanceIndicator: MdlPerformanceIndicator): List[MdlPerformanceIndicator] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.PerformanceIndicator.allQuery.filter(v1PerformanceIndicator => v1PerformanceIndicator.vProgramOutcome === vPerformanceIndicator.vProgramOutcome).elements.toList
    }
  }
