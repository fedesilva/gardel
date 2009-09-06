package gardel.core.dispatcher.request

import scala.collection._

import gardel.core.dispatcher.common._
import gardel.core.application.Application

import java.util.Locale

//FIXME make the empty constructors go away.
//FIXME create the copy operators

sealed case class ServerData(
  val serverName:     String,
  val scheme:         Scheme,
  val port:           Int,
  val serverAddress:  String
) 

abstract sealed case class Scheme()
case object Http   extends Scheme
case object Https  extends Scheme

sealed case class ClientData(
  val remoteAddress:  String,
  val remoteHost:     String,
  val remotePort:     Int,
  val locales:        Set[Locale]
)

sealed case class PathData(
  context:  String,
  uri:      String,
  query:    String
)

sealed case class ContentData(
  val mimeType:   String,
  val encoding:   String,
  val length:     Int
)

sealed case class Cookie( 
  val name:     String,
  val value:    String,
  val domain:   String,
  val path:     String,
  val maxAge:   Int,
  val secure:   Boolean,
  val version:  Int,
  val comment:  String
) {

  def this( name:String,value:String,domain:String ) = {
    this( name, value, domain, "/", -1, false, 0, "" )
  }

}


sealed abstract case class RequestMethod()
case object Get    extends RequestMethod
case object Put    extends RequestMethod
case object Post   extends RequestMethod
case object Delete extends RequestMethod
case object Head   extends RequestMethod

sealed case class Request(
  val application:  Application,
  val session:      Session,
  val method:       RequestMethod,
  val client:       ClientData,
  val server:       ServerData,
  val path:         PathData,
  val contentData:  ContentData,
  val attributes:   immutable.Set[Attribute],
  val headers:      immutable.Set[Header],
  val parameters:   immutable.Set[Parameter],
  val cookies:      immutable.Set[Cookie]
) {

  /** Add a header */
  def +(a:Attribute) = new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes+a,
    this.headers,
    this.parameters,
    this.cookies
  )
  /** Remove a header */
  def -(a:Attribute) = new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes-a,
    this.headers,
    this.parameters,
    this.cookies
  )


  /** Add a header */
  def +(h:Header) = new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers+h,
    this.parameters,
    this.cookies
  )
  /** Remove a header */
  def -(h:Header) = new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers - h,
    this.parameters,
    this.cookies
  )


  /** Add a parameter */
  def +(p:Parameter) =  new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters+p ,
    this.cookies
  )
  /** Remove a parameter */
  def -(p:Parameter) =  new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters-p,
    this.cookies
  )

  /** Add a cookie */
  def +(c:Cookie) =  new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters,
    this.cookies+c
  )

  /** Remove a cookie */
  def -(c:Cookie) =  new Request(
    this.application,
    this.session,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters,
    this.cookies-c
  )

  
  /** Add a session attribute */
  def +(s:SessionAttribute) =  {
    println( "Session Attribute" + s )
    new Request(
    this.application,
    this.session+s,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters,
    this.cookies
  )}

  /** Remove a session attribute */
  def -(s:SessionAttribute) =  new Request(
    this.application,
    this.session-s,
    this.method,
    this.client,
    this.server,
    this.path,
    this.contentData,
    this.attributes,
    this.headers,
    this.parameters,
    this.cookies
  )

}

