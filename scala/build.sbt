name := "ScalaSimpleSample"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"
libraryDependencies += "io.netty" % "netty-all" % "4.1.6.Final"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"
