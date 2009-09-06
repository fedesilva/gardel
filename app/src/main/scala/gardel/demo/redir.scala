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

trait RedirModule {
  this: BaseApplication =>
  dispatch( StrRule( _.startsWith("/redir"), 
    (r) => RedirectResponse(r,r.path.context) )
  )
}

