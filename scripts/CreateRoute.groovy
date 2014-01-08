includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(createRoute: "Creates a new Camel Route.") {
    name = argsMap.params ? argsMap.params[0] : 'Example'
    typeName = "Route"
    artifactName = "Route"
    artifactPath = "grails-app/routes"

    createArtifact(type: typeName, path: artifactPath, name: name, suffix: 'Route')
}

setDefaultTarget(createRoute)
