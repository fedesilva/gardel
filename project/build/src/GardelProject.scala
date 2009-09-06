import sbt._

class GardelProject(info: ProjectInfo) extends ParentProject(info)
{

  override def parallelExecution = true

  lazy val core = project("core", "gardel-core", new GardelCoreProject(_) )
  lazy val app  = project("app",  "gardel-app",  new GardelAppProject(_), core  )

  val javaNetRepo         = "Java.net Repository for Maven 2"         at "http://download.java.net/maven/2/"
  val scalaToolsReleases  = "Scala-Tools Maven2 Releases Repository"  at "http://scala-tools.org/repo-releases"
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

}

protected class GardelCoreProject(info: ProjectInfo) extends DefaultProject(info)
{

  val servletApi    = "javax.servlet"             % "servlet-api"   % "2.5"   % "provided->default"

  val scalaTest     = "org.scala-tools.testing"   % "scalatest"     % "0.9.5" % "test->default"
  val scalaCheck    = "org.scala-tools.testing"   % "scalacheck"    % "1.5"   % "test->default"
  val mockito       = "org.mockito"               % "mockito-all"   % "1.8.0" % "test->default"
  val hamcrestAll   = "org.hamcrest"              % "hamcrest-all"  % "1.1"   % "test->default"

  override def managedStyle = ManagedStyle.Maven

}

protected class GardelAppProject(info: ProjectInfo) extends DefaultWebProject(info)
{

  // jetty is only needed for testing
  val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test->default"  
  
  override def jettyWebappPath  = webappPath
  override def scanDirectories  = mainCompilePath :: testCompilePath :: Nil

}
