name := "play-slick-test"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0"
)

play.Project.playScalaSettings
