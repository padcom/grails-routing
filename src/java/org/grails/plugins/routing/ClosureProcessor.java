package org.grails.plugins.routing;

import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import groovy.lang.Closure;

public class ClosureProcessor implements Processor {

    private Closure target;

    public ClosureProcessor(Closure target) {
        this.target = target;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        this.target.call(exchange);
    }
}
