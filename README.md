### After fork:

* Update Camel from 2.9.2 to 2.11.0
* Support Spring Security

### Configuration for Spring Security:

* You've Spring Security plugin installed.
* Config.groovy. e.g.

<code>
grails.routing {
      	useSpringSecurity= true
      	authorizationPolicies = [
      		[id : 'camelUser', access : 'ROLE_USER'],
      		[id : 'camelAdmin', access : 'ROLE_ADMIN']
      	]
      }
</code>

Note that if I use id with name 'user', there's problem with my Grails app. So, I recommend to use unique id, perhaps adding some prefix.
Access is role name. if multiple roles, separate it by comma. I haven't tried it though.

* create-route route.name.here
* Add route configuration. The following is example, where it calls services.
<code>
        from('seda:admin').policy('camelAdmin').to('bean:loggingService?method=logUser')
        from('seda:user').policy('camelUser').to('bean:loggingService?method=logUser')
        from('seda:anyuser').to('bean:loggingService?method=logAnonym')
</code>
* To send message you can do something like
<code>
sendMessageWithAuth("seda:${springSecurityService.authentication.name}", msg, SecurityContextHolder.context.authentication )
</code>

### Reading list

* https://camel.apache.org/groovy-dsl.html
* http://camel.apache.org/spring-security.html
* http://camel.apache.org/spring-security-example.html

