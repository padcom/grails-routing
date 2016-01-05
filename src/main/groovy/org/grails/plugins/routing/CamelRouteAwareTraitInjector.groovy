package org.grails.plugins.routing

import grails.compiler.traits.TraitInjector
import groovy.transform.CompileStatic

/**
 * Trait injector which adds CamelRouteAware trait
 * to artefacts which support interactions with Camel routes
 */
@CompileStatic
class CamelRouteAwareTraitInjector implements TraitInjector {

    @Override
    Class getTrait() {
        CamelRouteAware
    }

    @Override
    String[] getArtefactTypes() {
        ['Service', 'Controller', 'Job'] as String[]
    }
}
