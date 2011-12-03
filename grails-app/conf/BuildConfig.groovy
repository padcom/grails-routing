grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
        excludes 'tomcat'
        excludes 'hibernate'
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        build('org.apache.camel:camel-core:2.9.0-RC1')
        build('org.apache.camel:camel-spring:2.9.0-RC1') {
            excludes 'spring-aop', 'spring-beans', 'spring-core', 'spring-expression', 'spring-asm', 'spring-tx', 'spring-context'
        }
        runtime('org.apache.camel:camel-groovy:2.9.0-RC1') {
            excludes 'spring-context', 'spring-aop', 'spring-tx', 'groovy-all'
        }
        runtime('org.apache.camel:camel-stream:2.9.0-RC1')
    }
}
