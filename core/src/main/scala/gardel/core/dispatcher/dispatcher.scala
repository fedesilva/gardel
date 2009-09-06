package gardel.core.dispatcher

import gardel.core.application.Production
import gardel.core.dispatcher.request._
import gardel.core.dispatcher.response._
import gardel.core.dispatcher.rules._

import javax.servlet._
import javax.servlet.http._
import scala.collection._

class Dispatcher(
  val filters:      List[FilterRule[_]],
  val dispatchers:  List[DispatchRule[_]]
) {

  /**
   * Search for a dispatcher for the request and if it exists apply filters, 
   *   dispatch and apply post-filters.
   *
   * @param  request the request to process
   * @return Some response or None.
   * 
   * FIXME Check and chain the response
   * FIXME We need to ensure an exception stops processing but all filters must be undone. 
   *       This means we need to unfold the filters already processed.
   */
  def service(request:Request):Option[Response] = {
    if(request.application.env != Production) println(request)
    findDispatcher(request) match {
      case Some(dispatcher) => 
        val filtered      = beforeFilter(request) // FIXME Check for Exceptions
        val response      = dispatcher.dispatch(filtered)
        val finalResponse = afterFilter(response) // FIXME Check for Exceptions 
        Some(finalResponse)
      case x => None
    }
  }

  /** 
   * FIXME Check raised exceptions, wrap in special EX containing rule that failed.
   *       This is done so we can unroll the filters using this to partition the coll. 
   */
  private def beforeFilter(request:Request): (Request) = { 
    filters.foldLeft(request){ (rq,r) => r.before(rq) }
  }

  private def findDispatcher(request:Request) = {
    // The first to match gets to run
    dispatchers.find { (rule) => 
      rule match {
        case r:StrRule => 
          r matches request.path.uri
        case r:RQRule  => 
          r matches request
        case _ => 
          false
      }
    }
  }

  private def afterFilter(response:Response) = {
    filters.foldRight(response) { (r,rp) => r.after(rp) }
  }

}

