package gardel.core.application

import gardel.core.dispatcher._
import gardel.core.dispatcher.rules._
import gardel.core.view._

import scala.collection.immutable._

/** At the very least start with this class */
abstract class BaseApplication extends Object
  with Application 
  with DispatchRuleChain
  with RenderRuleChain
  with ChainBuilder


sealed abstract case class Environment()
case object Development extends Environment
case object Testing     extends Environment
case object Staging     extends Environment
case object Production  extends Environment

/** Interface into the application */
trait Application {

  /** TODO this sould be externalazed? */
  val env:Environment = Development

  /** Factory for dispatchers.
   *  This will be called to serve requests.
   */
  def dispatcher = new Dispatcher(
    filterRules,
    dispatcherRules
  )

  /** The filters list */
  def filterRules:List[FilterRule[_]]
  /** The dispatchers list */
  def dispatcherRules:List[DispatchRule[_]]

  /** Holds data at application scope. */
  def context:Context

  /** Holds initial parameters passed to the app. */
  def parameters:Map[String,String]


  /** Factory for Renderers.
   *  This will be called to render responses
   */ 
  def renderer = new Renderer(renderRules)

  /** The registered views list */
  def renderRules: List[RenderRule]

  
}
