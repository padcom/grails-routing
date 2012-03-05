@artifact.package@

import org.apache.camel.builder.RouteBuilder

class @artifact.name@ extends RouteBuilder {
	def grailsApplication

    @Override
    void configure() {
		def config = grailsApplication?.config

        // example:
        // from('seda:input').to('stream:out')
    }
}
