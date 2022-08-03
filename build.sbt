/*
 * Copyright 2015 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.typesafe.sbt.osgi.OsgiKeys._
import sbtrelease.ReleaseStateTransformations._

lazy val root = (project in file("."))
  .enablePlugins(SbtOsgi)
  .settings(
    organization := "com.github.mkroli.lpm",
    name := "lpm",
    scalaVersion := "3.1.2",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.16", "2.13.8", "3.1.2"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.12" % Test,
      "org.scalatest" %% "scalatest-funspec" % "3.2.12" % Test,
      "junit" % "junit" % "4.13.2" % Test,
      "com.github.sbt" % "junit-interface" % "0.13.2" % Test
    ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),
    exportPackage += "com.github.mkroli.lpm",
    privatePackage := Nil,
    publishMavenStyle := true,
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage := Some(url("https://github.com/mkroli/lpm")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/mkroli/lpm"),
        "scm:git:https://github.com/mkroli/lpm.git"
      )
    ),
    developers := List(
      Developer(
        id = "mkroli",
        name = "Michael Krolikowski",
        email = "mkroli@yahoo.de",
        url = url("https://github.com/mkroli")
      )
    ),
    releaseCrossBuild := true,
    releaseVersionBump := sbtrelease.Version.Bump.Minor,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+ publishSigned"),
      releaseStepCommand("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
