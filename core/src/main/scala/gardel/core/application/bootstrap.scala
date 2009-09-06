package gardel.core.application

import gardel.core.dispatcher._

import scala.collection.immutable._
import scala.collection.mutable.{ HashMap, SynchronizedMap }



trait NoopBootstrap  {
  this: Application =>

  def parameters:Map[String,String] = Map()
  
  def context = new Context {
    private val map = new HashMap[String,AnyRef] with SynchronizedMap[String,AnyRef]
    def set(a:(String,AnyRef)):Unit   = map += a
    def get(k:String):Option[AnyRef]  = map(k) match {
      case null => None
      case v    => Some(v)
    } 
  }
}

