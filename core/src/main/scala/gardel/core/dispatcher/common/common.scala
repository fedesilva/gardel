package gardel.core.dispatcher.common

import gardel.core.util._

case class Header(name:String,values:Any)    extends KeyValuePair[String,Any](name,values)
case class Parameter(k:String,v:String)         extends KeyValuePair[String,String](k,v)
case class Attribute(k:String,v:AnyRef)         extends KeyValuePair[String,AnyRef](k,v)

case class ContentType(override val name:String) extends Header(name,Headers.ContentType)
case object TextHtml extends ContentType(ContentTypes.TextHtml)

object Headers {
  val ContentType = "Content-Type"
}

object ContentTypes {
  val TextHtml = "text/html"
}
