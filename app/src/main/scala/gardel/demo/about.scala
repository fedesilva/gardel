package gardel.demo

import gardel.core.application._
import gardel.core.dispatcher._
import gardel.core.dispatcher.common._
import gardel.core.dispatcher.rules._
import gardel.core.dispatcher.response._
import gardel.core.dispatcher.request._
import gardel.core.adapter.servlet._
import gardel.core.view._

import scala.collection.immutable._
import scala.xml._

trait AboutModule {

  this: BaseApplication =>

  /** TODO Abstract this kind of construct */
  private def matcher(r:AnyRef) = {
    val str = "/about"
    r match {
      case r:Request  => r.path.uri.startsWith(str)
      case r:Response => r.request.path.uri.startsWith(str)
    }
  }

  dispatch(
    RQRule(matcher _,handler _)
  )
  render( 
    RenderRule( matcher _, () => AboutView() )
  )

  def handler(rq:Request) = {
    for( p <- rq.parameters.toList.reverse ) {
      println("Parameter %s => %s".format(p.key,p.value))
    }
    DemoApp.CacheableResponse(rq)
  }
    
  case class AboutView() extends DemoLayout {
    def content(r:Response) = {
      <div>
          <h2>
            About Gardel
          </h2>
          {renderMenu("about",r)}
          <p>Everyday sings better.</p>
      </div>
    } 
  }

}
