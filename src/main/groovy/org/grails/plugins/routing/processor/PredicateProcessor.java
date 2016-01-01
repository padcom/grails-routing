package org.grails.plugins.routing.processor;

import groovy.lang.Closure;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

/**
 * Created with IntelliJ IDEA.
 * User: arief
 * Date: 6/12/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class PredicateProcessor implements Predicate {
    private final Closure target;

    public PredicateProcessor(Closure target) {
        this.target = target;
    }
    @Override
    public boolean matches(Exchange exchange) {
        Object obj = this.target.call(exchange);
        if(obj == null) return false;
        else if(obj instanceof Boolean) return ((Boolean) obj).booleanValue();
        return true;
    }
}
