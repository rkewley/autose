package controllers

import play.api._
import play.api.templates._
import play.api.data._
import models._
import play.api.mvc._


trait ControllerTrait[K, D]  {
	this: Base =>

	type displayType = Mdl[K] => Html
	type formType = (Form[D], Int) => Html
	
	def form: Form[D]
	def listFunction(list: List[Mdl[K]]): Html
	def showFunction: displayType
	def editFunction: formType
	def createFunction: displayType
	def crud: Crud[Mdl[K], K]
	def newItem: Mdl[K]

	def list(dataList: List[Mdl[K]]) = {
	  Ok(listFunction(dataList))
	}
	
    def show(id: K) = Action { implicit request =>
	  crud.select(id) match {
        case item: Some[Mdl[K]] =>
          Ok(showFunction(item.get))
        case None =>
          badRequest("Programs with key " + id + " not found in database", request)
      }
	}

	
	def edit(id: K) = compositeAction(NormalUser) {user => implicit template => implicit request =>
      crud.select(id) match {
        case item: Some[Mdl[K]] =>
          Ok(editFunction(form.fill(item.get.asInstanceOf[D]), 0))
        case None =>
          badRequest("Item with key " + id + " not found in database", request)
      }    
    }
	
   def delete(id: K) = compositeAction(NormalUser) { user => implicit template => implicit request =>
    crud.delete(id)
    Ok(listFunction(crud.all))
  }

  def create = compositeAction(NormalUser) { user => implicit template => implicit request =>
    Ok(editFunction(form.fill(newItem.asInstanceOf[D]), 1))
  }

  def save(newEntry: Int) = Action { implicit request =>
  	form.bindFromRequest.fold(
  	  form => {
        val errorMessage = formErrorMessage(form.errors)
        Logger.debug(errorMessage)
        BadRequest(viewforms.html.formError(errorMessage, request.headers("REFERER")))
      },
      formItem => {
        val item = formItem.asInstanceOf[Mdl[K]]
        if (item.validate) {
          newEntry match {
            case 0 => crud.update(item)
            case _ => crud.insert(item)
          }
          Redirect(routes.ProgramsController.listPrograms)
        } else {
          val validationErrors = item.validationErrors
          Logger.debug(validationErrors)
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