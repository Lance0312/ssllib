import com.amazonaws.services.s3.model.Region

name := "SslLib"
organization := "co.verdigris.ssl"
version := "1.1.0"
scalaVersion := "2.11.10"
crossScalaVersions := Seq("2.10.6", "2.11.10")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.0"

libraryDependencies += "co.verdigris.security.specs" %% "spec-pkcs1" % "0.1.0"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.11.122"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.2.0"
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"
libraryDependencies += scalatest % Test

s3region := Region.US_Standard
s3overwrite := true

publishTo := Some(s3resolver.value("Verdigris Scala Libs", s3("scala-jars")))

resolvers += "Verdigris Scala Libs Repository" at "https://s3.amazonaws.com/scala-jars"

enablePlugins(SiteScaladocPlugin)

scmInfo := Some(ScmInfo(url("https://github.com/VerdigrisTech/ssllib"), "git@github.com:VerdigrisTech/ssllib.git"))
ghpages.settings
git.remoteRepo := scmInfo.value.get.connection
