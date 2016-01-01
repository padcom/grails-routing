package grails.routing

import org.apache.camel.builder.RouteBuilder

/**
 * Route used by integration spec to verify functionality.
 * Needs to be excluded from plugin
 */
class IntegrationSpecRoute extends RouteBuilder {

    static final String INTEGRATION_SPEC_ROUTE_CHANNEL = 'direct:integrationSpec'

    void configure() {
        from(INTEGRATION_SPEC_ROUTE_CHANNEL)
	    	.beanRef('integrationSpecService', 'handleMessage')
    }
}