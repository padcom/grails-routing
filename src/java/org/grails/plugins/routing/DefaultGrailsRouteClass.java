package org.grails.plugins.routing;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import groovy.lang.Closure;

@SuppressWarnings("rawtypes")
public class DefaultGrailsRouteClass extends AbstractInjectableGrailsClass implements GrailsRouteClass, GrailsRouteClassProperty {
    public DefaultGrailsRouteClass(Class clazz) {
        super(clazz, ROUTE);
    }

    public DefaultGrailsRouteClass(Class clazz, String trailingName) {
        super(clazz, trailingName);
    }

    @Override
    public Closure getConfiguration() {
        return getPropertyValue(CONFIGURE, Closure.class);
    }
}
