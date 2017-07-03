import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "autose"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm, javaJdbc, "mysql" % "mysql-connector-java" % "5.1.22",
        "org.mindrot" % "jbcrypt" % "0.3m",
        "jp.t2v" %% "play2.auth"      % "0.10.1",
        "jp.t2v" %% "play2.auth.test" % "0.10.1" % "test",
        "com.typesafe.slick" %% "slick" % "1.0.0",
        "com.typesafe.play" %% "play-slick" % "0.3.2",
        "org.apache.camel" % "maven-html-to-pdf" % "2.10.1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here  
		  resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)      
  )

}
