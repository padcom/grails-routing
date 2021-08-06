package org.grails.plugins.routing.processor;

import groovy.lang.Closure;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ClosureProcessor implements Processor {
    private final Closure target;

    public ClosureProcessor(final Closure target) {
        this.target = target;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        this.target.call(exchange);
    }
}
