//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

def routeDir = "${basedir}/grails-app/routes"

if(!(routeDir as File).exists()) {
    Ant.mkdir(dir:routeDir)
	
    event("CreatedFile", [routeDir])
    event("StatusFinal", ["Camel Route directory was created."])
}