import sbt.Keys.scalacOptions

val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.13.5",
  scalacOptions += "-language:postfixOps"
)

lazy val core = Project(id = "core", base = file("modules/core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++=
      Seq(
        "com.lihaoyi"          %% "upickle"        % "1.4.0",
        "ch.qos.logback"       % "logback-classic" % "1.2.3",
        "com.aventrix.jnanoid" % "jnanoid"         % "2.0.0",
        "de.mkammerer"         % "argon2-jvm"      % "2.7"
      )
  )
  .settings(scalacOptions ++= Seq("-language:implicitConversions"))

lazy val db = Project(id = "db", base = file("modules/db"))
  .settings(commonSettings: _*)
  .dependsOn(core)
  .settings(
    libraryDependencies ++=
      Seq(
        "com.typesafe.slick"  %% "slick"          % "3.3.2",
        "com.typesafe.slick"  %% "slick-hikaricp" % "3.3.2",
        "com.github.tminglei" %% "slick-pg"       % "0.19.3"
      )
  )

lazy val api = Project(id = "api", base = file("api"))
  .settings(commonSettings: _*)
  .dependsOn(db)
  .settings(
    libraryDependencies ++=
      Seq(
        "org.eclipse.jetty"      % "jetty-server"  % "11.0.6",
        "org.eclipse.jetty"      % "jetty-servlet" % "11.0.6",
        "org.scala-lang.modules" %% "scala-xml"    % "2.0.1"
      )
  )

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .aggregate(api)
