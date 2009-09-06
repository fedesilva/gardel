package gardel.core.dispatcher.rules

import gardel.core.application._
import gardel.core.dispatcher._
import gardel.core.dispatcher.request._
import gardel.core.dispatcher.response._

import scala.collection._

/**
 * Implements chain building functionality.
 * FIXME why queues? 
 */
trait DispatchRuleChain {

  this: Application =>

  def filterRules = _filters.clone.toList
  private val _filters = new mutable.SynchronizedQueue[FilterRule[_]]  

  def dispatcherRules = _dispatchers.clone.toList
  private val _dispatchers = new mutable.SynchronizedQueue[DispatchRule[_]]  

  def filter( rule:FilterRule[_] ) =  _filters += rule
  def dispatch( rule: DispatchRule[_] ) = _dispatchers += rule
  
}

sealed abstract case class FilterRule[T<:AnyRef]( 
  matches: (T)        => Boolean,
  before:  (Request)  => Request,
  after:   (Response) => Response
)

case class RQFilter( 
  p: (Request)  => Boolean,
  b: (Request)  => Request,
  a: (Response) => Response
) extends FilterRule(p,b,a)

case class StrFilter( 
  p: (String)     => Boolean,
  b: (Request)    => Request,
  a: (Response)   => Response
) extends FilterRule(p,b,a)


sealed abstract case class DispatchRule[T<:AnyRef]( 
  matches:  (T)       => Boolean,
  dispatch: (Request) => Response
)

case class RQRule( 
  p: (Request)  => Boolean,
  c: (Request)  => Response
) extends DispatchRule(p,c)

case class StrRule( 
  p: (String)   => Boolean,
  c: (Request)  => Response
) extends DispatchRule(p,c)


