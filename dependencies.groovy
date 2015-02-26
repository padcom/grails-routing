grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

	plugins {
//        compile ":spring-security-core:1.2.7.3"

		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
