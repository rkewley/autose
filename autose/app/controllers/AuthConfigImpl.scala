package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.templates._
import play.api.http.Status._
import models._
import views._
import play.api.mvc._
import play.api.mvc.Results._
import jp.t2v.lab.play2.auth._
import play.api.Play._
import play.api.cache.Cache
import scala.reflect.Manifest
import scala.reflect._
import persistence.SqlUser

trait AuthConfigImpl extends AuthConfig {

  type Id = String

  type User = MdlUser

  type Authority = Permission

  val idTag: ClassTag[Id] = classTag[Id]

  val sessionTimeoutInSeconds = 3600

  def resolveUser(id: Id) = SqlUser.findByEmail(id)

//  def loginSucceeded(request: RequestHeader) = Redirect(routes.UserController.listUser)
  def loginSucceeded(request: RequestHeader): Result = {
    val uri = request.session.get("access_uri").getOrElse(routes.CoursesController.listCourses.url.toString)
    Redirect(uri).withSession(request.session - "access_uri")
  }
  
  def logoutSucceeded(request: RequestHeader) = Redirect(routes.LoginController.login)

  def authenticationFailed(request: RequestHeader) = Redirect(routes.LoginController.login)

  def authorizationFailed(request: RequestHeader) = Forbidden("no permission")

  def authorize(user: MdlUser, authority: Authority) = (user.permission, authority) match {
    case (Administrator, _) => true
    case (NormalUser, NormalUser) => true
    case _ => false
  }
}


trait Base extends Controller with Auth with Pjax with AuthConfigImpl {

  def compositeAction(permission: Permission)(f: MdlUser => Template => RequestHeader => PlainResult) =
    Action { implicit request =>
      (for {
        user     <- authorized(permission).right
        template <- pjax(user).right
      } yield f(user)(template)(request)).merge
    }
}

trait Pjax {
  self: Controller =>

  type Template = String => Html => Html
  def pjax(user: MdlUser)(implicit request: RequestHeader): Either[PlainResult, Template] = Right {
    if (request.headers.keys("X-PJAX")) html.pjaxTemplate.apply
    else html.fullTemplate.apply(user)
  }

}
