import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "autose"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    	"mysql" % "mysql-connector-java" % "5.1.22",
        "org.mindrot" % "jbcrypt" % "0.3m",
        "jp.t2v" %% "play20.auth" % "0.5"
     )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
