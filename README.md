### After fork:

* Update Camel from 2.9.2 to 2.11.0
* Support Spring Security

### Configuration for Spring Security:

* You've Spring Security plugin installed.
* Config.groovy. e.g.

<code>grails.routing {
      	useSpringSecurity= true
      	authorizationPolicies = [
      		[id : 'user', access : 'ROLE_USER'],
      		[id : 'admin', access : 'ROLE_ADMIN']
      	]
      	// other config. default value
      	camelContextId = "camelContext"
      	useMDCLogging = false
      	defaultThreadPoolProfile {
      		poolSize = "10"
      		maxPoolSize = "20"
      		maxQueueSize = "1000"
      		rejectedPolicy = "CallerRuns"
      	}
      }
</code>

### Reading list

* https://camel.apache.org/groovy-dsl.html
* http://camel.apache.org/spring-security.html
* http://camel.apache.org/spring-security-example.html

