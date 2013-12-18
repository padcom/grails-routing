package org.grails.plugins.routing

import javax.activation.DataHandler
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor

class CamelMessageService {
    def producerTemplate

    def sendMessage(endpoint,message) {
        producerTemplate.sendBody(endpoint,message)
    }

    def sendMessageAndHeaders(endpoint, message, headers) {
        producerTemplate.sendBodyAndHeaders(endpoint,message,headers)
    }

    def sendMessageAndHeadersAndAttachments(endpoint, message, headers, Map<String, DataHandler> attachments) {
        producerTemplate.send( endpoint, { Exchange exchange ->
            Message msg = exchange.in;
            msg.setBody(message)
            msg.setHeaders(headers)
            attachments.each {
                msg.addAttachment(it.key, it.value)
            }
        } as Processor)
    }

    def requestMessage(endpoint,message) {
        producerTemplate.requestBody(endpoint,message)
    }

    def requestMessageAndHeaders(endpoint, message, headers) {
        producerTemplate.requestBodyAndHeaders(endpoint, message, headers)
    }
}
