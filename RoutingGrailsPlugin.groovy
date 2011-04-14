import grails.util.*

import org.apache.log4j.Logger

import org.apache.camel.spring.*
import org.apache.camel.model.*
import org.apache.camel.language.groovy.CamelGroovyMethods
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.beans.factory.config.MethodInvokingFactoryBean

import org.grails.plugins.routing.*

class RoutingGrailsPlugin {

    static final log = Logger.getLogger('org.grails.plugins.routing.RoutingGrailsPlugin')
    
    def version = "1.1.0"
    def grailsVersion = "1.3.6 > *"
    def dependsOn = [:]
    def pluginExcludes = []
    def loadAfter = [ 'controllers', 'services' ]
    def watchedResources = [
        "file:./grails-app/routes/**/*Route.groovy",
        "file:./plugins/*/grails-app/routes/**/*Route.groovy",
        "file:./grails-app/controllers/**/*Controller.groovy",
        "file:./grails-app/services/**/*Service.groovy"
    ]
    def artefacts = [ new RouteArtefactHandler() ]

    def author = "Maciej Hryniszak, Chris Navta"
    def authorEmail = "padcom@gmail.com, chris@ix-n.com"
    def documentation = "http://grails.org/plugin/routing"
    def title = "Routing capabilities using Apache Camel"
    def description = '''\\
This plugin provides routing capabilities using Apache Camel (http://camel.apache.org). It gives controllers and services a 'sendMessage' method that will send
a message to a given endpoint.

It also adds a 'Route' artifact that allows configuration of Camel routing using the Java DSL. New Routes can be
added with the 'grails create-route route-name' command.
'''

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    	init()

        def routeClasses = application.routeClasses
        def config = ConfigurationHolder.config.grails.camel
        def camelContextId = config?.camelContextId ?: 'camelContext'

        println "DEBUG: camelContextId = ${camelContextId}"

        log.debug "Configuring Routes"
        routeClasses.each { routeClass ->
            configureRouteBeans.delegate = delegate
            configureRouteBeans(routeClass)
        }

        xmlns camel:'http://camel.apache.org/schema/spring'

        camel.camelContext(id: camelContextId) {
            routeClasses.each { routeClass ->
                camel.routeBuilder(ref: "${routeClass.fullName}")
            }
            camel.template(id: 'producerTemplate')
        }
    }

    def doWithDynamicMethods = { ctx ->
    	this.addMethods(application.controllerClasses,ctx);
    	this.addMethods(application.serviceClasses,ctx);
    	// if the "grails-quartz" plugin is installed this will add 
    	// dynamic methods to all jobs (or 'tasks' in the Quartz terminology)
    	if (isQuartzPluginInstalled(application))
            this.addMethods(application.taskClasses,ctx);
    }

    def doWithApplicationContext = { applicationContext -> 
    }

    def onChange = { event ->
        def artifactName = "${event.source.name}"
        log.debug "Detected a change in ${artifactName}"
        if (artifactName.endsWith('Controller') || artifactName.endsWith('Service')) {
            def artifactType = (artifactName.endsWith('Controller')) ? 'controller' : 'service'
            log.debug "It's a ${artifactType}"
            def grailsClass = application."${artifactType}Classes".find { it.fullName == artifactName }
            this.addMethods([grailsClass],event.ctx)
        }
    }

    def onConfigChange = { event ->
    }

    private init() {
        log.debug "Adding dynamic methods to RouteBuilder helpers"
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

    private configureRouteBeans = { routeClazz ->
        def fullName = routeClazz.fullName

        "${fullName}RouteClass"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication",true)
            targetMethod = "getArtefact"
            arguments = [RouteArtefactHandler.TYPE, fullName]
        }

        "${fullName}"(GrailsRouteBuilderFactoryBean) {
            routeClass = ref("${fullName}RouteClass")
        }
    }

    private addMethods(artifacts,ctx) {
        log.debug "Adding dynamic methods to ${artifacts}"
        def template = ctx.getBean('producerTemplate')

        artifacts?.each { artifact ->
            artifact.metaClass.sendMessage = { endpoint,message ->
                template.sendBody(endpoint,message)
            }
            artifact.metaClass.requestMessage = { endpoint,message ->
                template.requestBody(endpoint,message)
            }
        }
    }

    private isQuartzPluginInstalled(application) {
        try {
          def tasks = application.taskClasses
          return true
        } catch (e) {
          return false
        }
    }
}
