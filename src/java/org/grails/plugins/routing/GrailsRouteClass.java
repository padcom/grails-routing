package org.grails.plugins.routing;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;
import groovy.lang.Closure;

public interface GrailsRouteClass extends InjectableGrailsClass {
    Closure getConfiguration();
}
