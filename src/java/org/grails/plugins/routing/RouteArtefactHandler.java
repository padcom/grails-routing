package org.grails.plugins.routing;

import java.lang.reflect.Field;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.util.ReflectionUtils;
import org.apache.camel.builder.RouteBuilder;

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
