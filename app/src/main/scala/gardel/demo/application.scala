package gardel.demo

import gardel.core.application._
import gardel.core.dispatcher.common._
import gardel.core.dispatcher.rules._
import gardel.core.dispatcher.response._
import gardel.core.dispatcher.request._
import gardel.core.adapter.servlet._
import gardel.core.view._

import scala.collection.immutable._
import scala.xml._

class DemoApp extends BaseApplication
  with ServletBootstrap
  with IndexModule
  with AboutModule
  with RedirModule 
  with ErrorModule 
  with SessionModule 
  with BuilderModule 

object DemoApp {

  val DemoSessionKey = "gardel.demo.session.KEY"

  case class CacheableResponse(
    private val rq:Request
  ) extends OkResponse(rq) {
    
    override val headers = Set(
        Header("Cache-Control", "cache"),
        Header("Expires", "Tue, 01 Sep 2010 00:07:10 GMT" )
      )
  }

}



abstract case class DemoLayout() extends HtmlTemplate {

  override def html(r:Response) = {
      <html>
        <head>
          <title>Gardel Demo</title>
        </head>
        <body>
          <h1>Gardel Demo</h1>
          {content(r)}
          <p>{ 
            r.request.session(DemoApp.DemoSessionKey).getOrElse("No Session Data") 
          }</p>
        </body>
      </html>
  }


  def content(r:Response):Elem

  /**
   * TODO Can be abstracted into a helper class.
   *      It's a list of ( "label", enabledRenderer, disableRenderer  )   
   *
   * TODO can be put into a Site class which would also register dispatchers, etc.
   *      A kind of builder which can be mixed into the application.
   *
   */
  def renderMenu(l:String, r:Response) = {
    <div id="menu" > 
      {(if(l!="index") indexMenuItem(r) else <span><b>Index</b></span> )}
      {redirMenuItem(r)}
      {errorMenuItem(r)}
      {sessionMenuItem(r)}
      {builder1MenuItem(r)}
      {(if(l!="about") aboutMenuItem(r) else <span><b>About</b></span> )}
    </div>
  }

  def indexMenuItem(r:Response) = {
    <a href={url("/?u=1",r)} >[Index]</a>
  }
  def aboutMenuItem(r:Response) = {
    <a href={url("/about?a=1&b=2&c=3",r)}>[About]</a>
  }
  def redirMenuItem(r:Response) = {
    <a href={url("/redir",r)}>[Redirection]</a>
  }

  def errorMenuItem(r:Response) = {
    <a href={url("/demoerror",r)}>[Internal Error]</a>
  }

  def sessionMenuItem(r:Response) = {
    <a href={url("/session",r)}>[Session]</a>
  }

  def builder1MenuItem(r:Response) = {
    <a href={url("/builder_get",r)}>[Builder Get]</a>
  }

  /** TODO Move to a helper class */
  def url(url:String,r:Response) = r.request.path.context + url

}


