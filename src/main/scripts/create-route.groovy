description("Creates a new Camel Route") {
    usage "grails create-route [ROUTE NAME]"
    argument name: 'Route Name', description: "The name of the Route"
}

model = model(args[0])
render template: "Route.groovy",
        destination: file("grails-app/routes/$model.packagePath/${model.simpleName}Route.groovy"),
        model: model