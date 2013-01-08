package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import models._
import persistence._
import views._
import play.api.mvc._
import play.api.mvc.Results._
import jp.t2v.lab.play20.auth._
import play.api.Play._
import play.api.cache.Cache
import scala.reflect.Manifest
import scala.reflect.ClassManifest$

object LoginController extends Controller with LoginLogout with AuthConfigImpl {

  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(SqlUser.authenticate)(_.map(u => (u.vemail, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def logout = Action { implicit request =>
    gotoLogoutSucceeded.flashing(
      "success" -> "You've been logged out"
    )
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => gotoLoginSucceeded(user.get.vemail)
    )
  }

}
