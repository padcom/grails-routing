package grails.routing

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.builder.AdviceWithRouteBuilder;

class RoutesTests extends CamelTestSupport {
  //def producerTemplate

  @Before
  void setUp() {
    super.setUp()

    context.addRoutes(
      new RouteBuilder(){
        @Override
        void configure(){
          from('direct:foo').to('mock:bar')
        }                             
      })
  }

  @Test
  void testSimpleRoute() {    
    getMockEndpoint('mock:bar').expectedMessageCount(1)

    template.sendBody('direct:foo', 'Hello World')
    
    assertMockEndpointsSatisfied()
  }
}
