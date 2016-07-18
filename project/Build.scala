import play.sbt.Play.autoImport._
import play.sbt.{PlayJava, PlayScala}
import sbt.Keys._
import sbt._

object Build extends sbt.Build {
  val appName = "health-keeper"
  val appVersion = "1.0"

  val appScalaVersion = "2.11.7"

  // disable doc generation on dist
  sources in(Compile, doc) := Seq.empty
  publishArtifact in(Compile, packageDoc) := false

  lazy val rootSettings: Seq[Setting[_]] = Seq(
    scalaVersion := appScalaVersion,
    version := appVersion,
    libraryDependencies ++= databaseDependencies ++ commonDependencies ++ scalaz,
    slickTask <<= slickCodeGenTask
  )

  val scalaz = Seq("core", "effect")
    .map(v => "org.scalaz" %% s"scalaz-$v" % "7.2.4")

  val databaseDependencies = Seq(
    "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
    "com.typesafe.play" %% "play-slick" % "1.1.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
    "com.typesafe.slick" %% "slick-codegen" % "3.1.0",
    "com.github.tminglei" %% "slick-pg" % "0.14.0",
    "com.github.tminglei" %% "slick-pg_date2" % "0.14.0",
    "com.github.tminglei" %% "slick-pg_play-json" % "0.14.0"
  )

  val commonDependencies = Seq(
    cache,
    ws,
    "org.mindrot" % "jbcrypt" % "0.3m",
    "com.github.etaty" %% "rediscala" % "1.6.0",
    "org.apache.commons" % "commons-math3" % "3.6.1"
  )

  val root = Project(appName, file(".")).enablePlugins(PlayScala, PlayJava).settings(
    rootSettings
  )

  lazy val slickTask = TaskKey[Unit]("gen-tables")
  lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
    val configPath = root.base.getAbsoluteFile / "conf" / "application.conf"
    val outputDir = root.base.getAbsoluteFile / "app"
    toError(r.run("db.slick.codegen.SeparatedTablesCodeGenerator", cp.files, Array(configPath.getPath, outputDir.getPath), s.log))
  }
}
