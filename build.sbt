name := """sellem-mongodb"""

version := "1.0-SNAPSHOT"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(jdbc, anorm, cache, ws)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
   "org.scalatest" %% "scalatest" % "2.2.0",
   "org.pegdown" % "pegdown" % "1.4.2",
   "org.jsoup" % "jsoup" % "1.7.2",
    "org.mockito" % "mockito-all" % "1.9.5"
  )

