import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "GiftRegistry"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.mindrot" % "jbcrypt" % "0.3m",
      "com.typesafe" %% "play-plugins-mailer" % "2.0.4",
      "com.andersen-gott" %% "scravatar" % "1.0.1",
      "org.mongodb" %% "casbah" % "2.4.1",
      "se.radley" %% "play-plugins-salat" % "1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      routesImport += "se.radley.plugin.salat.Binders._",
      templatesImport += "org.bson.types.ObjectId"
    )

}
