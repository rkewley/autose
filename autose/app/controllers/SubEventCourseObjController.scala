
package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB

import scala.slick.driver.MySQLDriver.simple._

object SubEventCourseObjController extends ControllerTrait[Long, MdlSubEventCourseObj, Long] with Base with OptionalAuthElement {

  val form = Form[MdlSubEventCourseObj](
    mapping (
	"fidSubEventCourseObj" -> optional(of[Long]),
	"fSubEventAMS" -> of[Long],
	"fCourseObj" -> of[Long]
    )(MdlSubEventCourseObj.apply)(MdlSubEventCourseObj.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit user: MdlUser): Html =
	  views.html.viewlist.listSubEventCourseObj(getAll(ffk), ffk)
 
	override def listFunction(item: MdlSubEventCourseObj)(implicit user: MdlUser): Html =
	  views.html.viewlist.listSubEventCourseObj(getAll(item), item.vSubEventAMS)
 
	override def showFunction(vSubEventCourseObj: MdlSubEventCourseObj): Html =
	  views.html.viewshow.showSubEventCourseObj(vSubEventCourseObj)
	
	override def editFunction(mdlSubEventCourseObjForm: Form[MdlSubEventCourseObj]): Html = 
	  views.html.viewforms.formSubEventCourseObj(mdlSubEventCourseObjForm, 0)
	
	override def createFunction(mdlSubEventCourseObjForm: Form[MdlSubEventCourseObj]): Html = 
	  views.html.viewforms.formSubEventCourseObj(mdlSubEventCourseObjForm, 1)
	  
	def crud = slick.AppDB.dal.SubEventCourseObj


    def newItem(fkId: Long): MdlSubEventCourseObj = new MdlSubEventCourseObj(Option(0), fkId, 0)
    
    override def getAll(fkId: Long): List[MdlSubEventCourseObj] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubEventCourseObj.allQuery.filter(v1SubEventCourseObj => v1SubEventCourseObj.vSubEventAMS === fkId).elements.toList
    }
    
    override def getAll(vSubEventCourseObj: MdlSubEventCourseObj): List[MdlSubEventCourseObj] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.SubEventCourseObj.allQuery.filter(v1SubEventCourseObj => v1SubEventCourseObj.vSubEventAMS === vSubEventCourseObj.vSubEventAMS).elements.toList
    }
  }
