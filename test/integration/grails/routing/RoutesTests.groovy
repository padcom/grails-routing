package grails.routing

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.builder.RouteBuilder

class RoutesTests extends CamelTestSupport {

  def camelContext
  def producerTemplate

  @Override
  protected CamelContext createCamelContext() throws Exception {
    return camelContext
  }

  @Before
  void setUp() {
    super.setUp()

    camelContext.addRoutes(
      new RouteBuilder(){
        @Override
        void configure(){
          from('direct:foo').to('mock:bar')
        }                             
      })
  }

  @After
  void tearDown() {
    camelContext.stop()
  }

  @Test
  void testSimpleRoute() {
    def mockEndpoint
    mockEndpoint = getMockEndpoint('mock:bar')
    
    mockEndpoint.expectedMessageCount(1)

    producerTemplate.sendBody('direct:foo', 'Hello World')
    
    assertMockEndpointsSatisfied()
  }
}
