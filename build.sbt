ThisBuild / scalaVersion := "2.13.1"

val root = (project in file("."))
    .settings(
        name := "FP Effect Handling"
    )

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.1.1"
)