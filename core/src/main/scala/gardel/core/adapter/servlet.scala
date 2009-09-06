package gardel.core.adapter.servlet

import gardel.core.application._
import gardel.core.dispatcher._
import gardel.core.dispatcher.common._
import gardel.core.dispatcher.request._
import gardel.core.dispatcher.response._
import gardel.core.view._

import gardel.core.util.EnumerationWrapper._

import javax.servlet.{ 
  Filter, 
  ServletRequest,
  ServletResponse,
  FilterChain,
  FilterConfig,
  ServletContextListener,
  ServletContext,
  ServletContextEvent,
  ServletOutputStream
}

import javax.servlet.http.{ 
  HttpServletRequest,
  HttpSession,
  HttpServletResponse,
  HttpServletResponseWrapper,
  Cookie => JCookie
}

import java.io._
import java.util.{ Locale, Date }

import scala.collection._


object ServletBootstrap {
  private var _app:Application = _
  private[servlet]def set(a:Application) = _app = a
  def app:Application = _app
}

trait ServletBootstrap extends ServletContextListener  {
  this: Application =>

  private var sc:ServletContext = _

  def context = new Context {
    override def set(a:(String,AnyRef)):Unit = {
      sc.setAttribute( a._1,a._2 ) 
    }
    override def get(k:String):Option[AnyRef] = {
      sc.getAttribute(k) match {
        case null => None
        case o    => Some(o)
      }
    }
  }

  private val ctx = new mutable.HashMap[String,String]

  def parameters:immutable.Map[String,String] = {
    immutable.Map[String,String]() ++ ctx.elements
  }

  /**
   * Here be dragons. 
   */
  override def contextInitialized(sce: ServletContextEvent):Unit = {
    sc = sce.getServletContext
    val names = sc.getInitParameterNames
    while(names.hasMoreElements) {
      val name = names.nextElement.asInstanceOf[String]
      ctx + ( name -> sc.getInitParameter(name) )
      ServletBootstrap.set(this)
    }
  }
  override def contextDestroyed(sce: ServletContextEvent):Unit = {}

}

object ServletFilter {
  val SessionKey = "gardel.core.session.KEY"
  val RequestKey = "gardel.core.request.KEY"
}

class ServletFilter extends Filter {

  var app:Application = _  

  def doFilter( request:  ServletRequest,
                response: ServletResponse,
                chain:    FilterChain ) {

    val startTime = System.nanoTime()

    val rq = request.asInstanceOf[HttpServletRequest]
    val rp = response.asInstanceOf[HttpServletResponse]

    // Create request and call dispatch
    app.dispatcher.service(RequestBuilder(app,rq)) match {
      case Some(resp) => 
        resp match {
          case stringResponse:StringResponse =>
            val out = rp.getOutputStream 
            out.write( stringResponse.string.getBytes )
            out.flush // flush
            out.close // and close the output stream

          case redir:RedirectResponse =>
            if(app.env != Production) println( "Redirection " + redir )
            rp.sendRedirect( rp.encodeRedirectURL(redir.url) )
          case error:ErrorResponse    => 
            if(app.env != Production) println( "Error " + error )
            rp.sendError( error.status.code, error.message )

          case _ =>
        }
        writeToResponse(rq,rp,app.renderer.process(resp))

      case None => 
        if( app.env != Production ) {
          println( "Not a Gardel Resource (%s). Chaining.".format(rq.getRequestURI) )
        }
        chain.doFilter(request,response); 

    }

    val endTime = (BigDecimal(System.nanoTime()) - BigDecimal(startTime)) / BigDecimal(1000000000)
    if( app.env != Production )println( "After Filter ... %s s ".format( endTime.toString ) );

  }

  def destroy() = {}
  
  def init(config:FilterConfig) = {
    //FIXME probably there's a better way to do this
    app = ServletBootstrap.app 
  }

  /** Write to the output stream and put headers and cookies. */
  private def writeToResponse(  servletRequest:HttpServletRequest,
                                servletResponse:HttpServletResponse,
                                response:Response ):Unit = {

    if(app.env != Production) println( response )

    servletResponse.setStatus( response.status.code )    

    writeHeaders(servletResponse,response)
    writeCookies(servletResponse,response)
    writeSession(servletRequest,response)
    
    response match { 
      case renderedResponse:RenderedResponse =>
        val out = servletResponse.getOutputStream 
        renderedResponse.data.bytes match {
            case Some(bytes) =>
              out.write(bytes)
            case None => () 
        }
        out.flush // flush
        out.close // and close the output stream
    }

  }

  private def writeSession( servletRequest:HttpServletRequest,
                            response:Response) = {
    println("Saving Session " + response.request.session) 
    servletRequest.getSession.setAttribute(
      ServletFilter.SessionKey, 
      response.request.session 
    )

  }

  /** Write the headers to the response. */
  private def writeHeaders(
    servletResponse:HttpServletResponse, response:Response) = {
    response.headers.foreach( (h) => 
        h.value match {
          case d:Date   =>
            servletResponse.setDateHeader( h.name, d.getTime )
          case i:Int    =>
            servletResponse.setIntHeader( h.name, i )
          case s:String =>
            servletResponse.setHeader( h.name, s )
          case _        =>
            servletResponse.setHeader( h.name, h.value.toString )
        }
    )
  }

  /** Write the cookies to the response */
  private def writeCookies(
    servletResponse:HttpServletResponse,
    response:Response) = {
    
    response.cookies.foreach { 
      (c) => val jc = new JCookie(c.name,c.value)
      jc.setPath(c.path)
      jc.setSecure(c.secure)
      jc.setVersion(c.version)
      jc.setMaxAge(c.maxAge)
      jc.setDomain(c.domain)
      jc.setComment(c.comment)
      servletResponse.addCookie(jc)
    }

  }


  object RequestBuilder {
    def apply(app:Application, rq:HttpServletRequest):Request = {
      Request(
        app,
        session(rq),
        method(rq),
        clientData(rq),
        serverData(rq),
        pathData(rq),
        contentData(rq),
        attributes(rq),
        headers(rq),
        parameters(rq),
        cookies(rq)
      )
    }
  
    /** Retrieves or creates the session data from the container. */
    private def session(rq:HttpServletRequest) = {
      rq.getSession.getAttribute( ServletFilter.SessionKey ) match {
        case s:Session  => s
        case _          => Session(immutable.Map[String,SessionAttribute]())
      }
    }
  
    private def method(rq:HttpServletRequest):RequestMethod = {
      rq.getMethod match {
        case "GET"    => Get
        case "POST"   => Post
        case "DELETE" => Delete
        case "PUT"    => Put
        case "HEAD"   => Head
        case _        => Get
      }
    }
  
    private def serverData(rq:HttpServletRequest) = {
      ServerData(
        rq.getServerName,
        rq.getScheme match {
          case "http"   => Http
          case "https"  => Https
        },
        rq.getServerPort,
        rq.getLocalAddr
      )
    }
  
    private def clientData(rq:HttpServletRequest) = {
      ClientData(
        rq.getRemoteAddr,
        rq.getRemoteHost,
        rq.getRemotePort,
        locales(rq)          
      ) 
    }

    private def locales(rq:HttpServletRequest) = {
      immutable.Set[Locale]() ++ 
      ( for( locale <- List.fromIterator(rq.getLocales)) yield {
        locale.asInstanceOf[Locale]
      })
    }
  
    private def pathData(rq:HttpServletRequest) = {
      val context = rq.getContextPath match { case null => "" case s:String => s }
      val uri     = rq.getRequestURI.drop(context.length)
      val query   = rq.getQueryString match { case null => "" case s:String => s }
      PathData( context, uri, query )
    }

    private def contentData(rq:HttpServletRequest) = {
      ContentData(
        rq.getContentType match { case s:String => s case null => "" },
        rq.getCharacterEncoding match { case s:String => s case null => "" },
        rq.getContentLength
      ) 
    }
  
    private def attributes(rq:HttpServletRequest) = {
      rq.getAttribute( ServletFilter.RequestKey ) match {
        case r:immutable.Set[Attribute] => r
        case _ => immutable.Set[Attribute]()
      }
    }
    
    /**
     * Build the headers list. 
     * <br/>
     * If there is more than one value for a header we 
     */
    private def headers(rq:HttpServletRequest) = {
      immutable.Set[Header]() ++ 
      ( for( name <- List.fromIterator(rq.getHeaderNames)) yield {
           val strName = name.asInstanceOf[String]
           val values  = List.fromIterator( rq.getHeaders(strName)).asInstanceOf[List[String]]
           Header( strName, values.last )
      })
    }

  
    private def parameters(rq:HttpServletRequest) = {
      immutable.Set[Parameter]() ++
      ( for( name <- List.fromIterator(rq.getParameterNames)) yield {
          val k = name match { case s:String => s case _ => "" } 
          val v = rq.getParameter(k) 
          Parameter( k,v )
        })
    }
  
    private def cookies(rq:HttpServletRequest) = {
      rq.getCookies match {
        case null => immutable.Set[Cookie]()
        case _    =>
          immutable.Set[Cookie]() ++
          rq.getCookies.map { c =>
            val ck = c.asInstanceOf[JCookie]
            Cookie( 
              ck.getName,
              ck.getValue,
              ck.getDomain,
              ck.getPath,
              ck.getMaxAge,
              ck.getSecure,
              ck.getVersion,
              ck.getComment )
        }.toList
      }
    }
  
  }
  
}


