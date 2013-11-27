
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

object ElectiveGroupController extends ControllerTrait[Long, MdlElectiveGroup, Long] with Base with OptionalAuthElement {

  val form = Form[MdlElectiveGroup](
    mapping (
	"fidElectiveGroup" -> optional(of[Long]),
	"fElectiveGroupName" -> text,
	"fNumberToChoose" -> of[Long],
	"fProgram" -> of[Long],
	"fSubDiscipline" -> of[Boolean]
    )(MdlElectiveGroup.apply)(MdlElectiveGroup.unapply)
  )
      

	override def listFunction(ffk: Long)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listElectiveGroup(getAll(ffk), ffk)
 
	override def listFunction(item: MdlElectiveGroup)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewlist.listElectiveGroup(getAll(item), item.vProgram)
 
	override def showFunction(vElectiveGroup: MdlElectiveGroup)(implicit maybeUser: Option[MdlUser]): Html = 
	  views.html.viewshow.showElectiveGroup(vElectiveGroup)
	
	override def editFunction(mdlElectiveGroupForm: Form[MdlElectiveGroup]): Html = 
	  views.html.viewforms.formElectiveGroup(mdlElectiveGroupForm, 0)
	
	override def createFunction(mdlElectiveGroupForm: Form[MdlElectiveGroup]): Html = 
	  views.html.viewforms.formElectiveGroup(mdlElectiveGroupForm, 1)
	  
	def crud = slick.AppDB.dal.ElectiveGroup


    def newItem(fkId: Long): MdlElectiveGroup = new MdlElectiveGroup(Option(0), "", 0, fkId, false)
    
    override def getAll(fkId: Long): List[MdlElectiveGroup] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ElectiveGroup.allQuery.filter(v1ElectiveGroup => v1ElectiveGroup.vProgram === fkId).elements.toList
    }
    
    override def getAll(vElectiveGroup: MdlElectiveGroup): List[MdlElectiveGroup] = AppDB.database.withSession { implicit session: Session =>
      AppDB.dal.ElectiveGroup.allQuery.filter(v1ElectiveGroup => v1ElectiveGroup.vProgram === vElectiveGroup.vProgram).elements.toList
    }
  
    
  override def save(newEntry: Int) = StackAction { implicit request =>
  	form.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      formItem => {
        val item = formItem
        if (item.validate) {
          println("Inserting or updating item")
          newEntry match {
            case 0 => 
              println("updating item")
              crud.update(item)
            case _ => 
              println("inserting item")
              crud.insert(item)
          }
          Redirect(routes.RequiredCoursesController.list(item.vProgram))
        } else {
          val validationErrors = item.validationErrors
          Logger.debug(validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
  
   override def delete(id: Long) = compositeAction(NormalUser) { user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[MdlElectiveGroup] =>
          crud.delete(id)
          implicit val userOption = Some(user)
          Redirect(routes.RequiredCoursesController.list(item.get.vProgram))
        case None =>
          badRequest("Programs with key " + id + " not found in database", request)
      }
  }

  
    

  }
