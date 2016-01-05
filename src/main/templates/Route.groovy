package ${packageName}

import org.apache.camel.builder.RouteBuilder

class ${className}Route extends RouteBuilder {
    void configure() {
        // example:
        // from('seda:input').to('stream:out')
    }
}
