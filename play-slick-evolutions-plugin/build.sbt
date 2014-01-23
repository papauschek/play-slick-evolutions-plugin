sbtPlugin := true

name := "play-slick-evolutions-plugin"

organization := "com.papauschek"

libraryDependencies ++= Seq(
  jdbc,
  cache, // play cache external module
  "com.typesafe.slick" %% "slick" % "2.0.0-RC1"
)

play.Project.playScalaSettings
