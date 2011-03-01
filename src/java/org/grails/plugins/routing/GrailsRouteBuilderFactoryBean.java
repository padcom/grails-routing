package org.grails.plugins.routing;

import org.springframework.beans.factory.FactoryBean;
import org.apache.log4j.Logger;

@SuppressWarnings("rawtypes")
public class GrailsRouteBuilderFactoryBean implements FactoryBean {

    private static final Logger log = Logger.getLogger(GrailsRouteBuilderFactoryBean.class);

    private GrailsRouteClass routeClass;

    @Override
    public Object getObject() throws Exception {
        log.debug("RouteClass: " + routeClass);
        return new GrailsRouteBuilder(routeClass.getConfiguration());
    }

    @Override
    public Class getObjectType() {
        return GrailsRouteBuilder.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setRouteClass(GrailsRouteClass routeClass) {
        log.debug("Hit GrailsRouteBuilderFactoryBean.setRouteClass");
        this.routeClass = routeClass;
    }
}
