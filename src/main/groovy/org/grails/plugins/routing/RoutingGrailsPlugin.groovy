package org.grails.plugins.routing

import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.apache.camel.groovy.extend.CamelGroovyMethods
import org.apache.camel.model.ChoiceDefinition
import org.apache.camel.model.ProcessorDefinition
import org.grails.plugins.routing.processor.PredicateProcessor
import org.springframework.beans.factory.config.MethodInvokingFactoryBean

@Slf4j
class RoutingGrailsPlugin extends Plugin {
    def grailsVersion = "4.0.0 > *"
    def artefacts = [new RouteArtefactHandler()]
    def loadAfter = ['controllers', 'services']
    def author = 'Matthias Hryniszak, Chris Navta, Arief Hidaya'
    def authorEmail = 'padcom@gmail.com, chris@ix-n.com'
    def documentation = 'http://grails.org/plugin/routing'
    def title = 'Apache Camel Plugin'
    def description = 'Provides message routing capabilities using Apache Camel'
    def profiles = ['web']
    def pluginExcludes = [
            "grails-app/routes/*",
            "grails-app/services/*"
    ]
    def license = "APACHE"
    def developers = [
            [name: "Matthias Hryniszak", email: "padcom@gmail.com"],
            [name: "Chris Navta", email: "chris@ix-n.com"],
            [name: "Arsen A. Gutsal", email: "gutsal.arsen@gmail.com"]
    ]
    def issueManagement = [system: "GITHUB", url: "https://github.com/padcom/grails-routing/issues"]
    def scm = [url: "https://github.com/padcom/grails-routing"]

    Closure doWithSpring() {
        { ->
            log.info "		...  Configuring RoutingGrailsPlugin"
            def config = config.grails.routing
            def camelContextId = config?.camelContextId ?: 'camelContext'
            def useMDCLogging = config?.useMDCLogging ?: false
            def streamCache = config?.streamCache ?: false
            def trace = config?.trace ?: false
            def useSpringSecurity = config?.useSpringSecurity ?: false
            def authorizationPolicies = config?.authorizationPolicies ?: []
            def routeClasses = grailsApplication.routeClasses

            initializeRouteBuilderHelpers()

            routeClasses.each { routeClass ->
                log.trace "Configuring Route - ${routeClass}"
                def fullName = routeClass.fullName

                "${fullName}Class"(MethodInvokingFactoryBean) {
                    targetObject = ref("grailsApplication", false)
                    targetMethod = "getArtefact"
                    arguments = [RouteArtefactHandler.ROUTE, fullName]
                }

                "${fullName}"(ref("${fullName}Class")) { bean ->
                    bean.factoryMethod = "newInstance"
                    bean.autowire = "byName"
                }
            }

            if (useSpringSecurity) {
                xmlns camelSecure: 'http://camel.apache.org/schema/spring-security'
                authorizationPolicies?.each {
                    camelSecure.authorizationPolicy(id: it.id, access: it.access,
                            accessDecisionManager: it.accessDecisionManager ?: "accessDecisionManager",
                            authenticationManager: it.authenticationManager ?: "authenticationManager",
                            useThreadSecurityContext: it.useThreadSecurityContext ?: true,
                            alwaysReauthenticate: it.alwaysReauthenticate ?: false)
                }
            }

            xmlns camel: 'http://camel.apache.org/schema/spring'

            // we don't allow camel autostart regardless to autoStartup value
            // this may cause problems if autostarted camel start invoking routes which calls service/controller
            // methods, which use dynamically injected methods
            // because doWithDynamicMethods is called after doWithSpring

            //changed in grails 3 back to autostartup - doWithDynamicMethods should be
            //migrated to trait
            camel.camelContext(id: camelContextId,
                    useMDCLogging: useMDCLogging,
                    autoStartup: true,
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
            log.info "		RoutingGrailsPlugin Configured ..."
        }
    }

    private void initializeRouteBuilderHelpers() {
        // only filter predicate. but looks like it's been handled. https://camel.apache.org/groovy-dsl.html
        ProcessorDefinition.metaClass.filter = { filter ->
            if (filter instanceof Closure) {
                filter = new PredicateProcessor(filter)
            }
        }
        ChoiceDefinition.metaClass.when = { filter ->
            if (filter instanceof Closure) {
                filter = new PredicateProcessor(filter)
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
}
