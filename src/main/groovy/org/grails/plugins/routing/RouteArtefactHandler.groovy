package org.grails.plugins.routing

import grails.core.ArtefactHandlerAdapter
import org.apache.camel.builder.RouteBuilder

@SuppressWarnings("rawtypes")
class RouteArtefactHandler extends ArtefactHandlerAdapter implements GrailsRouteClassProperty {
    RouteArtefactHandler() {
        super(ROUTE, GrailsRouteClass.class, DefaultGrailsRouteClass.class, ROUTE)
    }

    boolean isArtefactClass(Class clazz) {
        return clazz != null && RouteBuilder.class.isAssignableFrom(clazz)
    }
}
