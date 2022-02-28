scalaVersion := "2.13.8"

name         := "todo-api"
organization := "ru.afanasev.todo"
version      := "0.0.1"

val http4sVersion        = "0.22.11"
val tofuVversion         = "0.10.7"
val jwrVersion           = "9.0.4"
val loggingVersion       = "3.9.4"
val logbackVersion       = "1.2.10"
val doobieVersion        = "0.13.4"
val newTypeVersion       = "0.4.4"
val circeVersion         = "0.14.1"
val kindProjectorVersion = "0.13.2"

libraryDependencies += "tf.tofu"                    %% "tofu-config"         % tofuVversion
libraryDependencies += "ch.qos.logback"             % "logback-classic"      % logbackVersion
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging"       % loggingVersion
libraryDependencies += "com.github.jwt-scala"       %% "jwt-core"            % jwrVersion
libraryDependencies += "com.github.jwt-scala"       %% "jwt-circe"           % jwrVersion
libraryDependencies += "org.http4s"                 %% "http4s-dsl"          % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-blaze-client" % http4sVersion
libraryDependencies += "org.http4s"                 %% "http4s-circe"        % http4sVersion 
libraryDependencies += "org.tpolecat"               %% "doobie-core"         % doobieVersion
libraryDependencies += "org.tpolecat"               %% "doobie-hikari"       % doobieVersion
libraryDependencies += "org.tpolecat"               %% "doobie-postgres"     % doobieVersion
libraryDependencies += "io.estatico"                %% "newtype"             % newTypeVersion
libraryDependencies += "io.circe"                   %% "circe-generic"       % circeVersion
libraryDependencies += "io.circe"                   %% "circe-literal"       % circeVersion

addCompilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)

javaOptions += "-DLogback.configurationFile=logback.groovy"

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-encoding", "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

