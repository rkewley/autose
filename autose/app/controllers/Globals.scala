package controllers

import com.typesafe.config._
import scala.util.control.Exception._

case class CMIConfig(config: Config) {
  def configValue[T](value: String, f: String => T, default: T): T = {
    (catching(classOf[ConfigException])
      opt { f(value)}).getOrElse(default)
  }
}

object Globals {
  val configuration = ConfigFactory.load()
  val cmiConfig = CMIConfig(configuration)
  val webDavServer = cmiConfig.configValue("cmi.webDavServer", configuration.getString, "http://134.240.29.188/webdav1/")
  //val webDavServer = "http://134.240.29.188/webdav1/"
  val currentYear = cmiConfig.configValue("cmi.currentYear", configuration.getInt, 2016)
  //val currentYear = 2017
  val currentTerm = cmiConfig.configValue("cmi.currentTerm", configuration.getInt, 2)
  //val currentTerm = 2
  val gradesYear = cmiConfig.configValue("cmi.gradesYear", configuration.getInt, 2016)
  //val gradesYear = 2016
  val gradesTerm = cmiConfig.configValue("cmi.gradesTerm", configuration.getInt, 2)
  //val gradesTerm = 2
  val linkRegex = "http:.+webdav1\\/".r
  
  def updateLink(link: String): String = {
    val newLink = linkRegex.findFirstIn(link) match {
      case Some(s) => linkRegex.replaceFirstIn(link, webDavServer)
      case None => link.startsWith("http") match {
        case true => link
        case false => webDavServer + link
      }
    }
    newLink
  }
}