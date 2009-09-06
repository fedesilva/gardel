package gardel.core.view

import gardel.core.dispatcher.common._
import gardel.core.dispatcher.response._
import gardel.core.application._

import scala.collection._
import scala.xml._

import java.io.OutputStream


class Renderer(val views: List[RenderRule]) {
  

  /**
   *   Finds the first view that can render this response
   *   and invokes its render method. 
   *   <br/>
   *   
   *   @param r the response to be rendered.
   *   @return the rendered response.
   *   
   *   FIXME Check for exceptions 
   */ 
  def process( r:Response ):RenderedResponse  = {
    views.find(_.matches(r)) match {
      case Some(v) => v.template().render(r)
      case None    => RenderedResponse(r,EmptyRenderedData)
    } 
  }

}

/** 
 * Provides implementations for view related methods in the Application 
 */
trait RenderRuleChain {
  this: Application =>

  /** Get all views in the chain */
  def renderRules: List[RenderRule] = _views.clone.toList
  private val _views = new mutable.ListBuffer[RenderRule]

  /** Store a view */
  def render(v:RenderRule) = _views + v

}

case class RenderRule(
  matches:  (Response) => Boolean,
  template: ()          => Template
)

trait Template {  
  def render(r:Response):RenderedResponse
}

/** 
 * Basic HtmlView. Adds proper mimetype header.
 * <br/>
 * You need to supply an implementation for the html method.
 */
abstract case class HtmlTemplate() extends Template {
  override def render(r:Response):RenderedResponse = {
    RenderedResponse( 
      r+TextHtml,
      new RenderedData {
        override def bytes = { Some(html(r).toString.getBytes) }
      }
    )
  }

  def html(r:Response):Elem

}

case class RenderedResponse(
  val original: Response,
  val data:     RenderedData
) extends Response(
  original.request,
  original.status,
  original.headers,
  original.cookies
)

final case class EmptyRenderedResponse(
  private val r:Response
) extends RenderedResponse(r, EmptyRenderedData )

trait RenderedData {
  /** You ask for bytes until you get None */
  def bytes:Option[Array[Byte]]
}

case object EmptyRenderedData extends RenderedData {
  override def bytes = None
}


