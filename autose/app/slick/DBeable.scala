package slick

import scala.slick.session.Database
import play.api.db.DB
import play.api.Application
import scala.slick.driver.ExtendedProfile
 
trait DBeable {
 
  val SLICK_DRIVER = "slick.db.driver"
  val DEFAULT_SLICK_DRIVER = "scala.slick.driver.MySQLDriver"
 
  def getDal(implicit app : Application) : DAL = {
    val driverClass = app.configuration.getString(SLICK_DRIVER).getOrElse(DEFAULT_SLICK_DRIVER)
    val driver = singleton[ExtendedProfile](driverClass)
    new DAL(driver)
  }
 
  def getDb(implicit app : Application) = {
    Database.forDataSource(DB.getDataSource())
  }
 
  private def singleton[T](name : String)(implicit man: Manifest[T]) : T =
    Class.forName(name + "$").getField("MODULE$").get(man.runtimeClass).asInstanceOf[T]
  
}