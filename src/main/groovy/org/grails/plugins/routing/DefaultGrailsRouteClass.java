package org.grails.plugins.routing;

import org.grails.core.AbstractInjectableGrailsClass;

@SuppressWarnings("rawtypes")
public class DefaultGrailsRouteClass extends AbstractInjectableGrailsClass implements GrailsRouteClass, GrailsRouteClassProperty {
    public DefaultGrailsRouteClass(Class clazz) {
        super(clazz, ROUTE);
    }

    public DefaultGrailsRouteClass(Class clazz, String trailingName) {
        super(clazz, trailingName);
    }
}
