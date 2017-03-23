# ngp-sample-repo

## Jenkinsfile
Just copy the Jenkinsfile into you project root folder. 

This Jenkinsfile creates a job for service in Jenkins.  
With the Job the commit gets tagged and pushed to nexus. Also dockerimage gets build (related to Dockerfile inside the project) and pushed to Artifactory.

## Dockerfile
Just copy the Dockerfile into you project root folder. 

Change the jar-name to you projectsname:  
`ENV PROJECTNAME = sample.jar` --> `ENV PROJECTNAME = projectname.jar`
