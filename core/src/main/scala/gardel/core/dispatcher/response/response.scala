package gardel.core.dispatcher.response

import gardel.core.dispatcher.common._
import gardel.core.dispatcher.request._
import status._

import scala.collection.immutable._

case class Response( 
  val request: Request,
  val status:  HttpStatus,
  val headers: Set[Header],
  val cookies: Set[Cookie]
) {

  def +[H<:Header]( h:H )  = {
    Response(
      this.request,
      this.status,
      this.headers+h,
      this.cookies
    )
  }
  def -( h:Header ) = {
    Response(
      this.request,
      this.status,
      this.headers-h,
      this.cookies
    )
  }
  
  def +( c:Cookie )  = {
    Response(
      this.request,
      this.status,
      this.headers,
      this.cookies+c
    )
  }
  def -( c:Cookie ) = {
    Response(
      this.request,
      this.status,
      this.headers,
      this.cookies-c
    )
  }


}

case class EmptyResponse(r:Request) 
  extends Response(r, status.NoContent, Set(), Set())

case class RedirectResponse(
  r:    Request,
  url:  String
) extends Response(r, MovedTemporarily, Set(), Set())

case class ErrorResponse(
  r:  Request,
  s:  HttpErrorStatus,
  message:  String
) extends Response(r,s,Set(),Set())

case class PayloadResponse[P](
  private val r:  Request,
  private val s:  HttpStatus,
  val payload:    P
) extends Response(r,s,Set(),Set()) 

case class ExplicitPayloadResponse[P,T](
  private val r:  Request,
  private val s:  HttpStatus,
  private val p:  P,
  val template:   T
) extends PayloadResponse[P](r,s,p)

case class OkResponse(
  private val r: Request
) extends Response(r,status.OK,Set(),Set())

case class StringResponse(
  private val r: Request,
  val string:String
) extends OkResponse(r)

package status {

  case class HttpStatus( val code:Int ) 
  abstract case class HttpErrorStatus( c:Int ) extends HttpStatus(c)

  case object OK                extends HttpStatus(200)
  case object NoContent         extends HttpStatus(204)
  case object MovedTemporarily  extends HttpStatus(302)
  case object NotModified       extends HttpStatus(304)

  case object NotFound                extends HttpErrorStatus(404)
  case object NotAcceptable           extends HttpErrorStatus(406)

  case object InternalServerError     extends HttpErrorStatus(500)

}

