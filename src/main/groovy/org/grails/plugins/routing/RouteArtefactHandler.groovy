package org.grails.plugins.routing

import org.apache.camel.builder.RouteBuilder
import grails.core.ArtefactHandlerAdapter

@SuppressWarnings("rawtypes")
public class RouteArtefactHandler extends ArtefactHandlerAdapter implements GrailsRouteClassProperty {
    public RouteArtefactHandler() {
        super(RouteArtefactHandler.ROUTE, GrailsRouteClass.class, DefaultGrailsRouteClass.class, RouteArtefactHandler.ROUTE);
    }

    public boolean isArtefactClass(Class clazz) {
        return clazz != null && RouteBuilder.class.isAssignableFrom(clazz);
    }
}
