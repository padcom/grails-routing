package grails.routing

import spock.lang.Specification
import grails.test.mixin.integration.Integration

@Integration
class TestRouteIntegrationSpec extends Specification {

	def integrationSpecService

	void 'ensure message may be sent and is received'(){
		when:
		integrationSpecService.sendCamelMessage('Hello World')

		then:
		integrationSpecService._messageCount == 1
		integrationSpecService._lastMessage == 'Hello World'
	}
}