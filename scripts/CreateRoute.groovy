includeTargets << grailsScript("_GrailsInit")

target(createRoute: "Creates a new Camel Route.") {
    typeName = "Route"
    artifactName = "Route"
    artifactPath = "grails-app/routes"

    createArtifact()
}

setDefaultTarget(createRoute)
