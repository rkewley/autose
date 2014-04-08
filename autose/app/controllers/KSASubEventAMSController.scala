
package controllers

import play.api._
import play.api.mvc._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import views._
import slick.AppDB
import scala.slick.driver.MySQLDriver.simple._
import jp.t2v.lab.play2.auth._
import models._
import persistence._
import play.api.libs.json._

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
    
    override def getAll(fkId: Long): List[MdlKSASubEventAMS] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSASubEventAMS.allQuery.filter(v1KSASubEventAMS => v1KSASubEventAMS.vSubEventAMS === fkId).elements.toList
    }
    
    override def getAll(vKSASubEventAMS: MdlKSASubEventAMS): List[MdlKSASubEventAMS] = AppDB.database.withSession { implicit session: scala.slick.driver.MySQLDriver.simple.Session =>
      AppDB.dal.KSASubEventAMS.allQuery.filter(v1KSASubEventAMS => v1KSASubEventAMS.vSubEventAMS === vKSASubEventAMS.vSubEventAMS).elements.toList
    }
    
    def importFromOnlineSubEvents = compositeAction(NormalUser){ user => implicit template => implicit request =>
      // pull all KSA mappings from sub-events
      val subEventMappings = AppDB.dal.MappingSubEvent.all
      val ksaSubEvents = AppDB.dal.KSASubGradedEvent.all
      val ksaSubEventsAMS = AppDB.dal.KSASubEventAMS.all
      
      ksaSubEvents.foreach { ksaSubEvent =>
        // see if there is a mapping for this sub event in the mappings file
        subEventMappings.find {mapping => mapping.vSubEvent == ksaSubEvent.vSubGradedEvent} match {
          case Some(mapping) => {
            // make sure we don't insert a duplicate record
            if (ksaSubEventsAMS.find{k => k.vKSA == ksaSubEvent.vKSA && k.vSubEventAMS == mapping.vSubEventAMS}.isEmpty) {
            	AppDB.dal.KSASubEventAMS.insert(MdlKSASubEventAMS(Some(0), ksaSubEvent.vKSA, mapping.vSubEventAMS))
            }
          }
          case None =>
        }
      }
      Redirect(routes.FacultyController.listFaculty)
    }
    
    def importFromOnlineGradedEvents = compositeAction(NormalUser){ user => implicit template => implicit request =>
      // pull all KSA mappings from sub-events
      val gradedEventMappings = AppDB.dal.MappingGradedEvent.all
      val ksaGradedEvents = AppDB.dal.KSAGradedEvent.all
      val ksaSubEventsAMS = AppDB.dal.KSASubEventAMS.all
      
      ksaGradedEvents.foreach { ksaGradedEvent =>
        // see if there is a mapping for this graded event in the mappings file
        gradedEventMappings.find {mapping => mapping.vGradedEvent == ksaGradedEvent.vGradedEvent} match {
          case Some(mapping) => {
            // See if this graded event has only one sub-event in AMS.  If so, we can map the KSA to it
            val subEvents = AppDB.dal.SubEventAMS.selectByGradedEventAMS(mapping.vGradedEventAMS)
            if (subEvents.length == 1) {
              // make sure we don't insert a duplicate record
              val subEvent = subEvents.head.vidSubEventAMS.get
              if (ksaSubEventsAMS.find{k => k.vKSA == ksaGradedEvent.vKSA && k.vSubEventAMS == subEvent}.isEmpty) {
                AppDB.dal.KSASubEventAMS.insert(MdlKSASubEventAMS(Some(0), ksaGradedEvent.vKSA, subEvent))
              }
            }          
          }
          case None =>
        }
      }
      Redirect(routes.FacultyController.listFaculty)
    }
    
	def getKSASubEventJsonByTopic(idSubEvent: Long, idTopic: Long) = Action {
	    val currentlyAssociated = AppDB.dal.KSASubEventAMS.selectBySubEvent(idSubEvent).map(_.vKSA)
	    println(currentlyAssociated.toString)
	    val ksa1 = SqlTopicObjectives.selectWhere("Topic = " + idTopic)
	    val ksa = ksa1.filter(ksa => !currentlyAssociated.contains(ksa.vTopicObjectiveNumber))
	    val resultJson = JsObject(ksa.map(ksa =>
	        ksa.vTopicObjectiveNumber.toString -> JsString(ksa.vObjective)))
	    Ok(resultJson)
	}

  def saveList = Action { implicit request =>
  	MdlKSASubEventAMSList.formList.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      vKSASubEventAMSList => {
        vKSASubEventAMSList.getList.foreach (vKSASubEventAMS =>
          if (vKSASubEventAMS.validate) {
            AppDB.dal.KSASubEventAMS.insert(vKSASubEventAMS)
          } else {
            val validationErrors = vKSASubEventAMS.validationErrors
            Logger.debug(validationErrors)
            BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
          }
        )
        Redirect(routes.KSASubEventAMSController.list(vKSASubEventAMSList.vSubEventAMS))
      })
  }

    
  }
