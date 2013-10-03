grails-routing
==============


Summary
==============
Provides message routing capabilities using Apache Camel
Installation
grails install-plugin routing
Description
 Please note that all development (including the examples) have recently been migrated to GitHub. Please update your branches!
 Grails-Routing 1.2.3 is the current stable version.
See GitHub for known issues.
Sources: https://github.com/padcom/grails-routing
Continuous integration: http://dev.aplaline.com/hudson/job/grails-routing/
 1.2.0 breaking changes
Route builders (so called Route classes) are now standard Camel RouteBuilder descendants not plain classes with configure closure
Route reloading has been removed. Apache Camel does not support route reloading and the existing hack didn't work properly in the latest version of that library.
 Since version 1.2.0 this plugin does not use ConfigurationHolder or any other static holders. Therefore it is safe to use it in an environment where on one Tomcat there is more than Grails application running

Overview
==============
The grails routing plug-in allows you to send and route messages to a wide variety of destination endpoints directly from your Controllers and Services. It also provides a new Grails artifact, Routes, to configure your routes using known Enterprise Integration Patterns via the Apache Camel Java DSL.

This plugin is a new and updated version of the grails-camel plugin with virtually the same capabilities but it targets Apache Camel 2.9.0 instead.


Creating Routes
==============
To create a new route, use the grails create-route command:

grails create-route MyMessage
This will create a route in your grails-app/routes directory:

import org.apache.camel.builder.RouteBuilder
class MyMessageRoute extends RouteBuilder {
	def grailsApplication

	@Override
	void configure() {
		def config = grailsApplication?.config

		// example:
		// from('seda:input').to('stream:out')
	}
}

In the configure closure you have full access to the Camel Java DSL to configure your message routes.


Route Configuration
==============
Simple Example
==============
To create a route from an in-memory queue called "input.queue" to stdout, use:

from("seda:input.queue").to("stream:out")
This would print out any Object sent to "seda:input.queue" to the console.


Slightly More Complex Example
==============
Suppose you wanted to send messages asynchronously to the following Grails Service:

class MyService {
    def myMethod(fooBarText) {
        log.info "Got text: ${ fooBarText }"
    }
}
Using Camel's bean integration, we can deliver messages directly to any Grails Service:

from("seda:input.queue").filter {
    it.in.body.contains("FooBar")
}.to("bean:myService?method=myMethod")
This would deliver any message with the text "FooBar" in the body to the myMethod method of the myService service.

This example also illustrates one of the routing enhancements the plug-in offers. You can pass a Closure to the "filter", "when" and "process" DSL methods.


Receiving emails from GMail
===========================
To receive emails from a GMail account is quite simple with Apache Camel and its camel-mail component. Here's what you need to do:

Enable IMap access to the GMail account you want to check (How to is here)

Add the necessary runtime dependency to Camel Mail component in BuildConfig.groovy

runtime 'org.apache.camel:camel-mail:2.9.0'
Follow the instructions on Apache Camel Mail component documentation page to setup the route

Hot reloading
If you want your service classes or beans to be hot reloadable DO NOT use

static transactional=true
in your service class. This is a known limitation as of now and will not work.


Sending Messages
================
The plug-in provides a new method, "sendMessage", to all Controllers and Services for sending messages to endpoints. It accepts a String endpoint and an Object message:

def myMessage = [name:"foo",data:"bar"]
sendMessage("seda:input.queue", myMessage)
This would send the Map "myMessage" to an in-memory queue called "input.queue".

If you need to send a message with headers you can use the following construct:

def myMessage = "The content of my message"
sendMessageAndHeaders("seda:input.queue", myMessage, [ header1: "value", header2: 2 ])

Configuration
==============
In order to be able to run multiple applications utilizing this plugin in one JVM you're going to need to change the ID of the camel context bean. You can do so in your Config.groovy like this:

grails.camel.camelContextId = 'hello'

Configuring thread pool (available as of 1.2.2)

Up until now only the default configuration was available (as per official docs):

<threadPoolProfile id="defaultThreadPoolProfile" defaultProfile="true"
                       poolSize="10" maxPoolSize="20" maxQueueSize="1000"
                       rejectedPolicy="CallerRuns"/>
Currently the default options are maintained as per that definition but the following options can be specified in Config.groovy to override the defaults:

grails.routing.threadPoolProfileConfig.poolSize
grails.routing.threadPoolProfileConfig.maxPoolSize
grails.routing.threadPoolProfileConfig.maxQueueSize
grails.routing.threadPoolProfileConfig.rejectedPolicy

Using Camel and ActiveMQ for JMS Messaging
To use JMS messaging use the routing-jms plugin. It provides all the required artifacts right out of the box and makes integrating JMS messaging a breeze.


Camel Components
================
Apache Camel has a wide variety of built-in Components for message delivery, such as JMS, SMTP, Web Services and Jabber. Take a look at the Apache Camel documentation for a comprehensive list. By default the following components are included:

camel-groovy
camel-stream
Including further components is as easy as adding a reference to the BuildConfig.groovy file as follows:
runtime("org.apache.camel:camel-ftp:2.9.4")
This will include full support for ftp:// endpoints in your application's routing facilities. For a complete list of available components see the Apache Camel documentation


Integration with Quartz plugin
===============================
Services and controllers are not the only artifacts capable of sending messages to through the Camel router. Once you install the grails-quartz plugin your jobs will be capable of sending messages just like your services and controllers do. This facility is unique to this plugin and is as of this moment not available in the original grails-camel plugin.


Ready to use examples
======================
Here you'll find a simplistic example with just one controller (HomeController) and just one action (the index action) that when invoked will send a message to an internal queue (seda:input) which will then be routed to standard output (stream:out)

Here is another simplistic example to demonstrate the integration with Quartz plugin. The ExampleJob is executed every 5 seconds and sends a message to the internal queue (seda:input) which will then be routed to standard output (stream:out)


Credits
=======
Chris Navta - The original grails-camel plugin as well as the original version of this documentation
Matthias Hryniszak - Quartz integration and adoption to Camel 2.9.0 and examples
Arsen A. Gutsal - Latest upgrade to Camel 2.9.4
