includeTargets << grailsScript("Init")

target(main: "Creates a new Camel Route.") {
    typeName = "Route"
    artifactName = "Route"
    artifactPath = "grails-app/routes"

    createArtifact()
}

setDefaultTarget(main)
