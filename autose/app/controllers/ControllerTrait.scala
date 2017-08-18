package controllers

import play.api._
import play.api.templates._
import play.api.data._
import models._
import play.api.mvc._
import jp.t2v.lab.play2.auth._
import jp.t2v.lab.play2.stackc._


trait ControllerTrait[K, D <: Mdl[K], FFK]  {
	this: Base with OptionalAuthElement =>
	
	def form: Form[D]
	def listFunction(ffk: FFK)(implicit user: MdlUser): Html
	def listFunction(item: D)(implicit user: MdlUser): Html
	def showFunction(item: D): Html
	def editFunction(aForm: Form[D]): Html
	def createFunction(aForm: Form[D]): Html
	def crud: slick.Crud[D, K]
	def newItem(fkId: FFK): D
	def getAll(item: D):List[D] = crud.all
	def getAll(ffk: FFK):List[D] = crud.all
  def redirect(item: D): Call = routes.CoursesController.listCourses()

	def list(id: FFK) = compositeAction(NormalUser) {implicit user => implicit template => implicit request =>
	  Ok(listFunction(id))
	}
	
    def show(id: K) = compositeAction(NormalUser) {implicit user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[D] =>
          Ok(showFunction(item.get))
        case None =>
          badRequest("Item with key " + id + " not found in database", request)
      }
	}
	
	def edit(id: K) = compositeAction(Faculty) {implicit user => implicit template => implicit request =>
      crud.select(id) match {
        case item: Some[D] =>
          Ok(editFunction(form.fill(item.get)))
        case None =>
          badRequest("Item with key " + id + " not found in database. No delete performed.", request)
      }    
    }
	
   def delete(id: K) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
	  crud.select(id) match {
        case item: Some[D] =>
          crud.delete(id)
          implicit val userOption = Some(user)
          Ok(listFunction(item.get))
        case None =>
          badRequest("Programs with key " + id + " not found in database", request)
      }
  }

  def create(id: FFK) = compositeAction(Faculty) { implicit user => implicit template => implicit request =>
    Ok(createFunction(form.fill(newItem(id))))
  }

  def save(newEntry: Int) = StackAction { implicit request =>
  	form.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug("Form Error " + errorMessage)
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
          Redirect(redirect(item))
        } else {
          val validationErrors = item.validationErrors
          Logger.debug("Validation error " + validationErrors)
          BadRequest(viewforms.html.formError(validationErrors, request.headers("REFERER")))
        }
      })
  }
    
  def formErrorMessage(errors: Seq[FormError]) = {
    def errMess(message: String, errorList: List[FormError]): String = {
      if (errorList.isEmpty) message else {
        errMess(message + errorList.head.message + "\n", errorList.tail)
      }
    }
    errMess("Error Messages:\n", form.errors.toList)
  }
	def badRequest(errMessage: String, request: RequestHeader) = {
	  BadRequest(viewforms.html.formError(errMessage, request.headers("REFERER")))
	}
	 
}