grails.project.work.dir = "target"

def camelVersion = '2.9.4'

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
		compile("org.apache.camel:camel-core:${camelVersion}")
		compile("org.apache.camel:camel-spring:${camelVersion}") {
			excludes 'spring-aop', 'spring-beans', 'spring-core', 'spring-expression', 'spring-asm', 'spring-tx', 'spring-context'
		}
		compile("org.apache.camel:camel-groovy:${camelVersion}") {
			excludes 'spring-context', 'spring-aop', 'spring-tx', 'groovy-all'
		}
		compile("org.apache.camel:camel-stream:${camelVersion}")
	}

	plugins {
		build(":release:2.2.0") { export = false }
	}
}
