package org.grails.plugins.routing;

import org.apache.camel.builder.RouteBuilder;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

@SuppressWarnings("rawtypes")
public class RouteArtefactHandler extends ArtefactHandlerAdapter implements GrailsRouteClassProperty {
    public RouteArtefactHandler() {
        super(ROUTE, GrailsRouteClass.class, DefaultGrailsRouteClass.class, ROUTE);
    }

    @Override
    public boolean isArtefactClass(Class clazz) {
        return clazz != null && RouteBuilder.class.isAssignableFrom(clazz);
    }
}
