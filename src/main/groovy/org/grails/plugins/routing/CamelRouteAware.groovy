package org.grails.plugins.routing

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate

import javax.activation.DataHandler
import javax.security.auth.Subject

/**
 * Trait which delivers camel functionality to 
 * controllers, services, jobs and potentially other artefacts.
 *
 */
@CompileStatic
trait CamelRouteAware {

	/**
	 * Camel producer used to send messages
	 */
	ProducerTemplate producerTemplate

	void sendMessage(String endpoint, Object message) {
		producerTemplate.sendBody(endpoint, message)
	}

	void sendMessageAndHeaders(String endpoint, message, Map<String, Object> headers) {
		producerTemplate.sendBodyAndHeaders(endpoint, message, headers)
	}

	void sendMessageAndHeadersAndAttachments(String endpoint, message, Map<String, Object> headers, Map<String, DataHandler> attachments){
		producerTemplate.send( endpoint, { Exchange exchange ->
			Message msg = exchange.in
			msg.setBody(message)
			msg.setHeaders(headers)
			attachments.each {
				msg.addAttachment(it.key, it.value)
			}
		} as Processor)
	}

	void requestMessage(String endpoint, message){
		producerTemplate.requestBody(endpoint,message)
	}

	void requestMessageAndHeaders(String endpoint, message, Map<String, Object> headers){
		producerTemplate.requestBodyAndHeaders(endpoint, message, headers)
	}

	void sendMessageWithAuth(String endpoint, message, auth){
		Map<String, Object> headers = [:]
        headers.put(Exchange.AUTHENTICATION, new Subject(true, [auth] as Set, [] as Set, [] as Set))
        producerTemplate.sendBodyAndHeaders(endpoint,message, headers)
    }

    void requestMessageWithAuth(String endpoint, message, auth) {
		Map<String, Object> headers = [:]
		headers.put(Exchange.AUTHENTICATION, new Subject(true, [auth] as Set, [] as Set, [] as Set))
		producerTemplate.requestBodyAndHeaders(endpoint, message, headers)
	}
}