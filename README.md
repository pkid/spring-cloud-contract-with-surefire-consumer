# ngp-sample-repo

## Jenkinsfile
Just copy the Jenkinsfile into you project root folder. 

This Jenkinsfile creates a job for service in Jenkins.  
With the Job the commit gets tagged and pushed to nexus. Also dockerimage gets build (related to Dockerfile inside the project) and pushed to Artifactory.

Difference between "normal" project and multi module project:  
You have to change one line in the `buildDockerImageAndPushToArtifactory`- Methode
* "Normal" project use: `def jarName = artifactId + "-" + pomVersion + ".jar"`
* Multi module project use: `def jarName = artifactId + "-service-" + pomVersion + ".jar"`

## Dockerfile
Just copy the Dockerfile into you project root folder. 

Change the jar-name to you projectsname:  
`ENV PROJECTNAME = sample.jar` --> `ENV PROJECTNAME = projectname.jar`

Difference between "normal" project and multi module project:  
You have to change one line inside the Dockerfile
* "Normal" project use: `ADD /target/$JARNAME $PROJECTNAME`
* Multi module project use: `ADD /service/target/$JARNAME $PROJECTNAME`

## POM.XML
Attention with the artifacId's of you different POM-Files:
* root/pom.xml: 

        <artifactId>sample-repo</artifactId>
* root/api/pom.xml:  

        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-api</artifactId>
* root/service/pom.xml:  

        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-service</artifactId>
