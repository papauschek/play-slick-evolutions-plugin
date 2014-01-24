// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin in order to import the Play library
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")
