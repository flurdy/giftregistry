import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "GiftRegistry"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.mindrot" % "jbcrypt" % "0.3m",
      "com.typesafe" %% "play-plugins-mailer" % "2.0.4",
      "com.andersen-gott" %% "scravatar" % "1.0.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
    )

}
