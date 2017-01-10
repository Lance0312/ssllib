name := "SslLib"
version := "0.1.0"
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.10.6", "2.11.8")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.0"

libraryDependencies += "co.verdigris.security.specs" %% "spec-pkcs1" % "0.1.0"
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"
libraryDependencies += scalatest % Test

resolvers += "Verdigris Scala JARs" at "https://s3.amazonaws.com/scala-jars"