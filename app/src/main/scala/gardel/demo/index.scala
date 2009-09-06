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

trait IndexModule {
  
  this: BaseApplication =>

  private def matcher(r:AnyRef) = {
    val str = "/"
    r match {
      case r:Request  => r.path.uri         == str
      case r:Response => r.request.path.uri == str
      case _ => false
    }
  }

  dispatch(RQRule(matcher _   , handler _) )
  render(RenderRule(matcher _ , () => IndexView() ))

  private def handler(rq:Request) ={
    for( p <- rq.parameters.toList.reverse ) {
      println("Parameter %s => %s ".format(p.key,p.value))
    }
    OkResponse(rq)  
  }

  private case class IndexView() extends DemoLayout {
    override def content(r:Response) = {
      <div>
          <h2>
            Welcome 
          </h2>
          {renderMenu("index",r)}
          <p>Por una cabeza</p>
      </div>
    }
  }
}
