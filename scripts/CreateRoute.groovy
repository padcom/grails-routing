includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(createRoute: "Creates a new Camel Route.") {
    name = argsMap.params ? argsMap.params[0] : 'Example'
    ant.echo("argsMap is $argsMap")
    typeName = "Route"
    artifactName = "Route"
    artifactPath = "grails-app/routes"
    createArtifact(type: typeName, path: artifactPath, name: name, suffix: 'Route')
}

setDefaultTarget(createRoute)
