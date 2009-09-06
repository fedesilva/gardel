package gardel.core.dispatcher.test;

import gardel.core.application._
import gardel.core.dispatcher._
import gardel.core.dispatcher.rules._

import org.scalatest._
import org.scalatest.matchers._

class ControllerSpecs extends Spec with ShouldMatchers with PrivateMethodTester {

  describe( "An Application" ) {

    it("should be able to add StrFilters to the chain" ) {
      val app = new BaseApplication with NoopBootstrap {
        filter(  StrFilter( 
          _ == "/about.html",
          (rq) =>  rq,
          (rp) =>  rp
        ))
      } 
      assert( app.filterRules.size == 1  )
    }
    
    it("should be able to add RQFilters to the chain" ) {
      val app = new BaseApplication with NoopBootstrap {
        filter(  RQFilter( 
          (rq) => true,
          (rq) =>  rq,
          (rp) =>  rp
        ) )
      } 
      assert( app.filterRules.size == 1  )
    }

  
    it("should be able to be extended by mixing in traits") {
      trait AddRulesAfter {
        this: DispatchRuleChain =>
        filter(  StrFilter( 
          (rq)    =>  true, 
          (rq)    =>  rq,
          (rp)    =>  rp
        ))
      }
      class ApplicationTest extends BaseApplication with NoopBootstrap {
        filter(  RQFilter( 
          (rq)    =>  true, 
          (rq)    =>  rq,
          (rp)    =>  rp
        ))
      } 
  
      val app = new ApplicationTest with AddRulesAfter
      assert( app.filterRules.size == 2 )
      
      val f1 = app.filterRules(0)
      val f2 = app.filterRules(1)
  
      assert( f1.isInstanceOf[RQFilter]  )
      assert( f2.isInstanceOf[StrFilter] )
  
    }

    it( "should be able to create a dispatcher" ){
      val app = new BaseApplication with NoopBootstrap 
      val dispatcher = app.dispatcher
      assert(dispatcher != null)
      assert(dispatcher.isInstanceOf[Dispatcher])
    }
  
    it("should be able to create renderers") {}

  }

}


class ControllerSuite extends FunSuite with PrivateMethodTester {

   test("Rules added by a trait are added after the rules added by an app") {

    trait AddRulesBefore {
      this: DispatchRuleChain =>
      filter(  StrFilter( 
        (rq)    =>  true, 
        (rq)    =>  rq,
        (rp)    =>  rp
      ))
    }

    abstract class ApplicationTest extends BaseApplication{
      filter(  RQFilter( 
        (rq)    =>  true, 
        (rq)    =>  rq,
        (rp)    =>  rp
      ))
    } 

    val app = new ApplicationTest with AddRulesBefore with NoopBootstrap
    assert( app.filterRules.size == 2 )
    
    val f1 = app.filterRules(0)
    val f2 = app.filterRules(1)

    assert( !f1.isInstanceOf[StrFilter] )
    assert( !f2.isInstanceOf[RQFilter]  )

  }


}


