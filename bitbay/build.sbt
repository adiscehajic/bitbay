name := """bitbay"""

version := "1.0-SNAPSHOT"

//lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.cloudinary" % "cloudinary" % "1.0.14",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "com.paypal.sdk" % "rest-api-sdk" % "1.2.0",
  "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5",
  "com.squareup.okhttp" % "okhttp" % "2.5.0",
  filters
)

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
