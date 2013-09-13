def routeDir = "${basedir}/grails-app/routes"

if(!(routeDir as File).exists()) {
    ant.mkdir(dir:routeDir)

    event("CreatedFile", [routeDir])
    event("StatusFinal", ["Camel Route directory was created."])
}
