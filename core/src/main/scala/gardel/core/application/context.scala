package gardel.core.application

trait Context {
  def set(a:(String,AnyRef)):Unit
  def get(k:String):Option[AnyRef]
}
