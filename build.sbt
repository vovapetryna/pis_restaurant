import sbt.Keys.scalacOptions

val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.13.5",
  scalacOptions += "-language:postfixOps"
)

val akkaVersion     = "2.6.13"
val akkaHttpVersion = "10.2.4"

lazy val core = Project(id = "core", base = file("modules/core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++=
      Seq(
        "com.lihaoyi"                %% "upickle"        % "1.4.0",
        "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2",
        "ch.qos.logback"             % "logback-classic" % "1.2.3",
        "com.aventrix.jnanoid"       % "jnanoid"         % "2.0.0",
        "de.mkammerer"               % "argon2-jvm"      % "2.7"
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
        "com.typesafe.akka"    %% "akka-actor-typed" % akkaVersion,
        "com.typesafe.akka"    %% "akka-http"        % akkaHttpVersion,
        "com.typesafe.akka"    %% "akka-stream"      % akkaVersion,
        "com.typesafe.akka"    %% "akka-http-xml"    % akkaHttpVersion,
        "com.github.jwt-scala" %% "jwt-core"         % "9.0.2",
        "com.github.jwt-scala" %% "jwt-upickle"      % "9.0.2"
      )
  )

lazy val root = project
  .in(file("."))
  .settings(commonSettings: _*)
  .aggregate(api)
