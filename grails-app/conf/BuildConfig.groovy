grails.project.work.dir = "target"

grails.project.dependency.resolution = {
    inherits("global") {
        excludes 'tomcat'
        excludes 'hibernate'
    }

    log "warn"

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile('org.apache.camel:camel-core:2.9.0')
        compile('org.apache.camel:camel-spring:2.9.0') {
            excludes 'spring-aop', 'spring-beans', 'spring-core', 'spring-expression', 'spring-asm', 'spring-tx', 'spring-context'
        }
        compile('org.apache.camel:camel-groovy:2.9.0') {
            excludes 'spring-context', 'spring-aop', 'spring-tx', 'groovy-all'
        }
        compile('org.apache.camel:camel-stream:2.9.0')
    }

    plugins {
        build(":release:2.0.0", ':rest-client-builder:1.0.2') { export = false }
    }
}
