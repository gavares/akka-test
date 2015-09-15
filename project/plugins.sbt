// Comment to get more information during initialization
logLevel := Level.Info

resolvers += Classpaths.typesafeReleases

resolvers += Classpaths.typesafeSnapshots

// For local testing, uncomment this line and comment
// out the two lines below
//addSbtPlugin("com.playi" % "sbt-playi" % "1.0")
lazy val root = project.in(file("."))
lazy val sbtPlayi = uri("https://github.com/playi/sbt-playi.git") 
