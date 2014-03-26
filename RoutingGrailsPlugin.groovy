import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.groovy.extend.CamelGroovyMethods
import org.apache.camel.model.ChoiceDefinition
import org.apache.camel.model.ProcessorDefinition
import org.grails.plugins.routing.RouteArtefactHandler
import org.springframework.beans.factory.config.MethodInvokingFactoryBean

import javax.activation.DataHandler

class RoutingGrailsPlugin {
	def version          = '1.3.2'
	def grailsVersion    = '2.0.0 > *'
	def loadAfter        = ['controllers', 'services']
	def artefacts        = [new RouteArtefactHandler()]
	def documentation    = 'http://grails.org/plugin/routing'
	def title            = 'Apache Camel Plugin'
	def description      = 'Provides message routing capabilities using Apache Camel'

	def license = "APACHE"
	def developers = [
		[name: "Matthias Hryniszak", email: "padcom@gmail.com"],
		[name: "Chris Navta", email: "chris@ix-n.com"],
		[name: "Arsen A. Gutsal", email: "gutsal.arsen@gmail.com"]
	]
	def issueManagement = [system: "GITHUB", url: "https://github.com/padcom/grails-routing/issues"]
	def scm = [url: "https://github.com/padcom/grails-routing"]

	def doWithSpring = {
		def config = application.config.grails.routing
		def camelContextId = config?.camelContextId ?: 'camelContext'
		def useMDCLogging = config?.useMDCLogging ?: false
		def streamCache = config?.streamCache ?: false
		def trace = config?.trace ?: false
		def routeClasses = application.routeClasses

		initializeRouteBuilderHelpers()

		routeClasses.each { routeClass ->
			def fullName = routeClass.fullName

			"${fullName}Class"(MethodInvokingFactoryBean) {
				targetObject = ref("grailsApplication", true)
				targetMethod = "getArtefact"
				arguments = [RouteArtefactHandler.ROUTE, fullName]
			}

			"${fullName}"(ref("${fullName}Class")) { bean ->
				bean.factoryMethod = "newInstance"
				bean.autowire = "byName"
			}
		}

		xmlns camel:'http://camel.apache.org/schema/spring'

		// we don't allow camel autostart regardless to autoStartup value
		// this may cause problems if autostarted camel start invoking routes which calls service/controller
		// methods, which use dynamically injected methods
		// because doWithDynamicMethods is called after doWithSpring
		camel.camelContext(id: camelContextId, 
                                   useMDCLogging: useMDCLogging, 
                                   autoStartup: false, 
                                   streamCache: streamCache,
                                   trace: trace) {
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

		addDynamicMethods(application.controllerClasses, template)
		addDynamicMethods(application.serviceClasses, template)

		if (isQuartzPluginInstalled(application)) {
			addDynamicMethods(application.taskClasses, template)
		}

		// otherwise we autostart camelContext here
		def config = application.config.grails.routing
		
		if (config.grails.routing.autoStartup ?: true) {
                        def camelContextId = config.camelContextId ?: 'camelContext'
			application.mainContext.getBean(camelContextId).start()
		}
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
			addDynamicMethods([grailsClass], event.ctx.getBean('producerTemplate'))
		}
	}

	// ------------------------------------------------------------------------
	// Private methods
	// ------------------------------------------------------------------------

	private initializeRouteBuilderHelpers() {
		ProcessorDefinition.metaClass.filter = { filter ->
			if (filter instanceof Closure) {
				CamelGroovyMethods.filter(delegate, filter)
			} else {
				delegate.filter(filter)
			}
		}

		ChoiceDefinition.metaClass.when = { filter ->
			if (filter instanceof Closure) {
				CamelGroovyMethods.when(delegate, filter)
			} else {
				delegate.when(filter)
			}
		}

		ProcessorDefinition.metaClass.process = { filter ->
			if (filter instanceof Closure) {
				CamelGroovyMethods.process(delegate, filter)
			} else {
				delegate.process(filter)
			}
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
			artifact.metaClass.sendMessageAndHeadersAndAttachments = { endpoint, message, headers, Map<String, DataHandler> attachments ->
				template.send( endpoint, { Exchange exchange ->
					Message msg = exchange.in;
					msg.setBody(message)
					msg.setHeaders(headers)
					attachments.each {
						msg.addAttachment(it.key, it.value)
					}
				} as Processor)
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
