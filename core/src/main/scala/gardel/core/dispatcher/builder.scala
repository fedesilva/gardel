package gardel.core.application

import gardel.core.application._
import gardel.core.dispatcher.rules._
import gardel.core.dispatcher.request._
import gardel.core.dispatcher.response._
import gardel.core.view._

/**
 *  Convenience Methods to define rules a'la Sinatra. 
 */
trait ChainBuilder {
  this: BaseApplication with DispatchRuleChain with RenderRuleChain =>

  def get(path:String, dispatcher:(Request) => Response) = {
    dispatch( RQRule(equalRqMatcher(path,Get)(_), dispatcher) )
  }

  def post(path:String, dispatcher:(Request) => Response) = {
    dispatch( RQRule(equalRqMatcher(path,Post)(_), dispatcher) )
  }

  def delete(path:String, dispatcher:(Request) => Response) = {
    dispatch( RQRule(equalRqMatcher(path,Delete)(_), dispatcher) )
  }

  def put(path:String, dispatcher:(Request) => Response) = {
    dispatch( RQRule(equalRqMatcher(path,Put)(_), dispatcher) )
  }

  def get(path:String, dispatcher:(Request) => Response, renderer: () => Template) = {
    dispatch( RQRule(equalRqMatcher(path,Get)(_), dispatcher) )
    render( RenderRule(equalRpMatcher(path,Get)(_), renderer) )
  }
  
  def post(path:String, dispatcher:(Request) => Response, renderer: () => Template) = {
    dispatch( RQRule(equalRqMatcher(path,Post)(_), dispatcher) )
    render( RenderRule(equalRpMatcher(path,Post)(_), renderer) )
  }

  def delete(path:String, dispatcher:(Request) => Response, renderer: () => Template) = {
    dispatch( RQRule(equalRqMatcher(path,Delete)(_), dispatcher) )
    render( RenderRule(equalRpMatcher(path,Delete)(_), renderer) )
  }

  def put(path:String, dispatcher:(Request) => Response, renderer: () => Template) = {
    dispatch( RQRule(equalRqMatcher(path,Put)(_), dispatcher) )
    render( RenderRule(equalRpMatcher(path,Put)(_), renderer) )
  }

  private def equalRqMatcher(path:String,method:RequestMethod)(r:Request) = {
    ( r.method == method && r.path.uri == path )
  }
  private def equalRpMatcher(path:String,method:RequestMethod)(r:Response) = {
    ( r.request.method == method && r.request.path.uri == path )
  }

}


