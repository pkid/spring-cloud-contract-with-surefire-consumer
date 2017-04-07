# public-sample-repo

## Jenkinsfile
Just copy the Jenkinsfile into you project root folder. 

This Jenkinsfile creates a job for service in Jenkins.  
With the Job the commit gets tagged and pushed to nexus. Also dockerimage gets build (related to Dockerfile inside the project) and pushed to Artifactory.

## Dockerfile
Just copy the Dockerfile into you project root folder. 

Change the jar-name to you projectsname:  
```Dockerfile
        ENV PROJECTNAME = sample.jar` --> `ENV PROJECTNAME = projectname.jar
```
Difference between "normal" project and multi module project:  
You have to change one line inside the Dockerfile
* "Normal" project use: 
```Dockerfile
        ADD /target/$JARNAME $PROJECTNAME
```
* Multi module project use: 
```Dockerfile
        ADD /service/target/$JARNAME $PROJECTNAME
```

## POM.XML
Attention with the artifacId's of you different POM-Files:
* root/pom.xml: 
```xml

        <artifactId>sample-repo</artifactId>
 ```

* root/api/pom.xml:  

```xml
        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-api</artifactId>
```
* root/service/pom.xml:  

```xml
        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-service</artifactId>
```
