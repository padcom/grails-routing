import grails.util.*

import org.apache.camel.spring.*
import org.apache.camel.model.*
import org.apache.camel.language.groovy.CamelGroovyMethods
import org.springframework.beans.factory.config.MethodInvokingFactoryBean

import org.grails.plugins.routing.RouteArtefactHandler
import org.grails.plugins.routing.processor.ClosureProcessor

class RoutingGrailsPlugin {
	def version          = '1.2.3'
	def grailsVersion    = '2.0.0 > *'
	def dependsOn        = [:]
	def loadAfter        = [ 'controllers', 'services' ]
	def artefacts        = [ new RouteArtefactHandler() ]
	def author           = 'Matthias Hryniszak, Chris Navta'
	def authorEmail      = 'padcom@gmail.com, chris@ix-n.com'
	def documentation    = 'http://grails.org/plugin/routing'
	def title            = 'Routing capabilities using Apache Camel'
	def description      = 'Provides message routing capabilities using Apache Camel'

	def doWithSpring = {
		def config = application.config.grails.routing
		def camelContextId = config?.camelContextId ?: 'camelContext'
        def useMDCLogging = config?.useMDCLogging ?: false
		def routeClasses = application.routeClasses

		initializeRouteBuilderHelpers()

		routeClasses.each { routeClass ->
			def fullName = routeClass.fullName

			"${fullName}Class"(MethodInvokingFactoryBean) {
				targetObject = ref("grailsApplication", true)
				targetMethod = "getArtefact"
				arguments = [ RouteArtefactHandler.ROUTE, fullName ]
			}

			"${fullName}"(ref("${fullName}Class")) { bean ->
				bean.factoryMethod = "newInstance"
				bean.autowire = "byName"
			}
		}

		xmlns camel:'http://camel.apache.org/schema/spring'

		camel.camelContext(id: camelContextId, useMDCLogging: useMDCLogging) {
			def threadPoolProfileConfig = config?.defaultThreadPoolProfile

			camel.threadPoolProfile(
				id: "defaultThreadPoolProfile",
				defaultProfile: "true",
				poolSize: threadPoolProfileConfig?.poolSize ?: "10",
				maxPoolSize: threadPoolProfileConfig?.maxPoolSize ?: "20",
				maxQueueSize: threadPoolProfileConfig?.maxQueueSize ?: "1000",
				rejectedPolicy: threadPoolProfileConfig?.rejectedPolicy ?: "CallerRuns")

			routeClasses.each { routeClass ->
				camel.routeBuilder(ref: "${routeClass.fullName}")
			}
			camel.template(id: 'producerTemplate')
		}
	}

	def doWithDynamicMethods = { ctx ->
		def template = ctx.getBean('producerTemplate')

		addDynamicMethods(application.controllerClasses, template);
		addDynamicMethods(application.serviceClasses, template);

		if (isQuartzPluginInstalled(application))
			addDynamicMethods(application.taskClasses, template);
	}

	def watchedResources = [
		"file:./grails-app/controllers/**/*Controller.groovy",
		"file:./grails-app/services/**/*Service.groovy"
	]

	def onChange = { event ->
		def artifactName = "${event.source.name}"

		if (artifactName.endsWith('Controller') || artifactName.endsWith('Service')) {
			def artifactType = (artifactName.endsWith('Controller')) ? 'controller' : 'service'
			def grailsClass = application."${artifactType}Classes".find { it.fullName == artifactName }
			addDynamicMethods([ grailsClass ], event.ctx.getBean('producerTemplate'))
		}
	}

	// ------------------------------------------------------------------------
	// Private methods
	// ------------------------------------------------------------------------

	private initializeRouteBuilderHelpers() {
		ProcessorDefinition.metaClass.filter = { filter ->
			if (filter instanceof Closure) {
				filter = CamelGroovyMethods.toExpression(filter)
			}
			delegate.filter(filter);
		}

		ChoiceDefinition.metaClass.when = { filter ->
			if (filter instanceof Closure) {
				filter = CamelGroovyMethods.toExpression(filter)
			}
			delegate.when(filter);
		}

		ProcessorDefinition.metaClass.process = { filter ->
			if (filter instanceof Closure) {
				filter = new ClosureProcessor(filter)
			}
			delegate.process(filter);
		}
	}

	private addDynamicMethods(artifacts, template) {
		artifacts?.each { artifact ->
			artifact.metaClass.sendMessage = { endpoint,message ->
				template.sendBody(endpoint,message)
			}
			artifact.metaClass.sendMessageAndHeaders = { endpoint, message, headers ->
				template.sendBodyAndHeaders(endpoint,message,headers)
			}
			artifact.metaClass.requestMessage = { endpoint,message ->
				template.requestBody(endpoint,message)
			}
			artifact.metaClass.requestMessageAndHeaders = { endpoint, message, headers ->
				template.requestBodyAndHeaders(endpoint, message, headers)
			}
		}
	}

	private isQuartzPluginInstalled(application) {
		// this is a nasty implementation... maybe there's something better?
		try {
			def tasks = application.taskClasses
			return true
		} catch (e) {
			return false
		}
	}
}
