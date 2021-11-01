package org.grails.plugins.routing

import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
class TestRouteIntegrationSpec extends Specification {

    IntegrationSpecService integrationSpecService

    void 'ensure message may be sent and is received'() {
        when:
        integrationSpecService.sendCamelMessage('Hello World')

        then:
        integrationSpecService._messageCount == 1
        integrationSpecService._lastMessage == 'Hello World'
    }
}