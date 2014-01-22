
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

object KSASubEventAMSController extends ControllerTrait[Long, MdlKSASubEventAMS, Long] with Base with OptionalAuthElement {

  val form = Form[MdlKSASubEventAMS](
    mapping (
	"fidKSASubEventAMS" -> optional(of[Long]),
	"fKSA" -> of[Long],
	"fSubEventAMS" -> of[Long]
    )(MdlKSASubEventAMS.apply)(MdlKSASubEventAMS.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listKSASubEventAMS(getAll(ffk), ffk)
 
	override def listFunction(item: MdlKSASubEventAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listKSASubEventAMS(getAll(item), item.vSubEventAMS)
 
	override def showFunction(vKSASubEventAMS: MdlKSASubEventAMS)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showKSASubEventAMS(vKSASubEventAMS)
	
	override def editFunction(mdlKSASubEventAMSForm: Form[MdlKSASubEventAMS]): Html = 
	  views.html.viewforms.formKSASubEventAMS(mdlKSASubEventAMSForm, 0)
	
	override def createFunction(mdlKSASubEventAMSForm: Form[MdlKSASubEventAMS]): Html = 
	  views.html.viewforms.formKSASubEventAMS(mdlKSASubEventAMSForm, 1)
	  
	def crud = slick.AppDB.dal.KSASubEventAMS


    def newItem(fkId: Long): MdlKSASubEventAMS = new MdlKSASubEventAMS(Option(0), 0, fkId)
    
    override def getAll(fkId: Long): List[MdlKSASubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.KSASubEventAMS.allQuery.filter(v1KSASubEventAMS => v1KSASubEventAMS.vSubEventAMS === fkId).elements.toList
    }
    
    override def getAll(vKSASubEventAMS: MdlKSASubEventAMS): List[MdlKSASubEventAMS] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.KSASubEventAMS.allQuery.filter(v1KSASubEventAMS => v1KSASubEventAMS.vSubEventAMS === vKSASubEventAMS.vSubEventAMS).elements.toList
    }
  }
