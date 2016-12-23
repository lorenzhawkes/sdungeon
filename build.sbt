name := """sdungeon"""

version := "1.0-SNAPSHOT"

lazy val scalaV = "2.11.8"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline ,
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    "org.webjars" %% "webjars-play" % "2.5.0",
    "org.webjars" % "bootstrap" % "3.3.7-1",
    "org.webjars" % "jquery" % "3.1.1-1",

    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
  )
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.4",
    "com.github.fomkin" %%% "pushka-json" % "0.8.0"
  ),
  skip in packageJSDependencies := false,
  jsDependencies ++= Seq("org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js", RuntimeDOM)
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.11",
      "com.github.fomkin" %% "pushka-json" % "0.8.0"
    ),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  ).
  jsSettings(
    libraryDependencies ++= Seq(
      "org.akka-js" %%% "akkajsactor" % "0.2.4.16",
      "com.github.fomkin" %%% "pushka-json" % "0.8.0"
    )
  ).
  jsConfigure(
     _ enablePlugins ScalaJSWeb

  )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
