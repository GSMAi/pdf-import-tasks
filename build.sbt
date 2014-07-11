name := "pdf-import-tasks"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.itextpdf" % "itextpdf" % "5.4.5"
  , "org.specs2" %% "specs2" % "2.3.13" % "test"
)


