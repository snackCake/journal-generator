name := "journal-generator"

version := "1.0"

scalaVersion := "2.11.5"

lazy val root =
  project
    .in( file(".") )
    .aggregate(title_core, title_analysis_service, title_generator_web)

lazy val title_core = project

lazy val title_analysis_service = project.dependsOn(title_core)

lazy val title_generator_web = project
