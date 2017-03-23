# ngp-sample-repo

## Jenkinsfile
copy the Jenkinsfile into you project root folder. You have to change servicename variable to the projectname (like it is spelled in  git url).  

    SERVICE_NAME = "YOUR-SERVICE-NAME"  

This Jenkinsfile in project folder creates a job for service. With the Job the commit gets tagged and pushed to nexus. Also dockerimage gets build (related to Dockerfile inside the project) and pushed to Artifactory.
