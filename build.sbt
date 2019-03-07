val packageName = "test-client"

val apacheHttpComponents = "4.4.1"

lazy val `lagom-test-api` = (project in file("lagom-test-api"))
  .settings(
    version := "1.0.8",

    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslClient % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
    )
  )

lazy val `lagom-test-client-impl` = (project in file("lagom-test-client-impl"))
  .enablePlugins(LagomScala)
  .settings(
    version := "1.0.9",

    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslClient,
      lagomScaladslClient % Test,
      lagomScaladslTestKit,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.18" % Test,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
      "org.apache.httpcomponents" % "httpclient" % apacheHttpComponents,
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
    )


  )
  .dependsOn(`lagom-test-api`)

ThisBuild / lagomCassandraEnabled := false
ThisBuild / lagomKafkaEnabled := false
