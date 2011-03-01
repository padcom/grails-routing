package org.grails.plugins.routing

import org.apache.camel.builder.RouteBuilder

public class GrailsRouteBuilder extends RouteBuilder {

    def configuration

    public GrailsRouteBuilder(Closure configuration) {
        this.configuration = configuration
    }

    public void configure() {
        configuration.delegate = this
        configuration()
    }
}
