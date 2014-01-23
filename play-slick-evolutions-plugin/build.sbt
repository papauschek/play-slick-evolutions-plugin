sbtPlugin := true

name := "play-slick-evolutions-plugin"

organization := "com.papauschek"

libraryDependencies ++= Seq(
  jdbc,
  cache, // play cache external module
  "com.typesafe.slick" %% "slick" % "2.0.0"
)

play.Project.playScalaSettings
