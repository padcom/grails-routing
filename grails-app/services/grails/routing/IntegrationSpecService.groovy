package grails.routing

import org.grails.plugins.routing.CamelRouteAware

//implements CamelRouteAware because trait injector is not executed in plugin integration test
class IntegrationSpecService implements CamelRouteAware {

	static int _messageCount = 0
	static String _lastMessage

	void sendCamelMessage(String message){
		sendMessage(IntegrationSpecRoute.INTEGRATION_SPEC_ROUTE_CHANNEL, message)
	}

	void handleMessage(payload){
		log.debug "Handling integration spec message $payload"
		_messageCount++
		_lastMessage = payload
	}
}