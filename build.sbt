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

import sbtrelease._
import ReleaseKeys._
import ReleaseStateTransformations._

organization := "com.github.mkroli.lpm"

name := "lpm"

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.5")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.0" % Test,
  "junit" % "junit" % "4.11" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

osgiSettings

OsgiKeys.exportPackage += "com.github.mkroli.lpm"

OsgiKeys.privatePackage := Nil

releaseSettings

releaseProcess <<= releaseProcess { releaseProcess =>
  releaseProcess filterNot Set(publishArtifacts, pushChanges)
}
