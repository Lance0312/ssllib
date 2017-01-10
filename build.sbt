import com.amazonaws.services.s3.model.Region

name := "SslLib"
organization := "co.verdigris.ssl"
version := "0.1.0"
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.10.6", "2.11.8")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.0"

libraryDependencies += "co.verdigris.security.specs" %% "spec-pkcs1" % "0.1.0"
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"
libraryDependencies += scalatest % Test

s3region := Region.US_Standard
s3overwrite := true

publishTo := Some(s3resolver.value("Verdigris Scala Libs Repository", s3("scala-jars")))

resolvers += "Verdigris Scala Libs Repository" at "https://s3.amazonaws.com/scala-jars"