package gardel.core.dispatcher

import scala.collection.immutable._
import gardel.core.util.KeyValuePair

case class SessionAttribute(k:String,v:AnyRef) extends KeyValuePair[String,AnyRef](k,v)

case class Session(data:Map[String,SessionAttribute])  {

  def apply(s:String):Option[AnyRef] = {
    if(data.contains(s)) Some(data(s).value)
      else None
  }

  def +(a:SessionAttribute) = {
    Session(data+(a.key->a))
  }
  def -(a:SessionAttribute) = {
    Session(data-a.key)
  }

  def +(a:(String,AnyRef)) = {
    Session(data+(a._1 -> SessionAttribute(a._1,a._2)))
  }
  def -(a:String) = {
    Session(data-a)
  }
 

}
