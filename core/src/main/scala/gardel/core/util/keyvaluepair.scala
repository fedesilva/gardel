package gardel.core.util

/**
 *  This case class exists so that making named <code>Tuple2</code>s is a bit easier
 *  and it is clear they are intended to be used as named key/value pairs. <br/>
 *  Also methods like +.() can be overloaded by taking a correctly typed param: ie
 *  <code>Request</code> has +() methods to add <code>Attribute</code> and 
 *  <code>Parameter</code>. Which one you want is determined by type.
 */
abstract case class KeyValuePair[K,V](
  val key:  K,
  val value:V
) extends Tuple2[K,V](key,value) {
  def tuple = (this.key -> this)
}


