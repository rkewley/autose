package slick

import play.api.Play.current
import scala.slick.session.Session
object AppDB extends DBeable {
 
  lazy val database = getDb
  lazy val dal = getDal
}