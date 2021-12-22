// Makes our code tidy
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.5")

// Revolver allows us to use re-start and work a lot faster!
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Native Packager allows us to create standalone jar
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.7")

// Easily manage scalac settings across scala versions with this:
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")
