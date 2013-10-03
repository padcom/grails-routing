grails.project.work.dir = 'target'

def camelVersion = '2.12.1'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
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

		test("org.apache.camel:camel-test:${camelVersion}") { excludes "junit" }
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
