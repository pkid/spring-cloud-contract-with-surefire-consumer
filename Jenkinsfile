#!/usr/bin/env groovy
//---------------------------------------------------------------------------
import groovy.transform.*
import hudson.model.*
//import static java.util.Arrays.asList
//import hudson.plugins.performance.JMeterParser
//---------------------------------------------------------------------------

//nexus
NEXUS_URL = 'http://nexus.wdf.sap.corp:8081/nexus/content/repositories/'
NEXUS_SNAPSHOTS_REPOSITORY = 'deploy.snapshots/'
//---------------------------------------------------------------------------

// docker artifactory
DOCKER_ARTIFACTORY_URL = 'docker.wdf.sap.corp:51032'
DOCKER_ARTIFACTORY_USER = 'ASA1_NEXTGENPAYROLL'
DOCKER_ARTIFACTORY_PASSWORD = 'uyN}77vY}A39KUm5lEgS'
DOCKER_ARTIFACTORY_REPO_NAME = '/prototype/test/public-sample-repo'
//---------------------------------------------------------------------------

//variables
def newDockerImage
//---------------------------------------------------------------------------

//stages
stage('Commit') {
    node {
		deleteDir()
		println "set git url"
		git url: "git@github.wdf.sap.corp:nextgenpayroll-infrastructure/public-sample-repo.git"
		println "create new POM version"
		def newPOMVersion = adjustPOMVersion()
		println "tag new POM version to GIT"
		tagChangesToGit(newPOMVersion)
		println "upload artifacts to nexus"
		uploadArtifactsToNexus(NEXUS_URL, NEXUS_SNAPSHOTS_REPOSITORY)
		println "build new docker image and push to artifactory"
		newDockerImage = buildDockerImageAndPushToArtifactory(DOCKER_ARTIFACTORY_URL, DOCKER_ARTIFACTORY_REPO_NAME, DOCKER_ARTIFACTORY_USER, DOCKER_ARTIFACTORY_PASSWORD)
		println "finish stage"
    }
}
//---------------------------------------------------------------------------

//HELPER-METHODES

/*
 * This function executes sh statements and returns the output
 */
def executeShell(command) {
	def result = sh returnStdout: true, script: command
	return result.trim()
}

/*
 * This function assumes a version M.m.i-SNAPSHOT in the pom.xml and creates a new version M.m.i-SHA-SNAPSHOT
 * SHA is the github commit id
 */
def adjustPOMVersion() {
	// get the version, e.g., M.m.i-SNAPSHOT
	def baseVersion = executeShell 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec'
			
	// create the new version, e.g., M.m.i-SHA-SNAPSHOT
	def currentCommitSHA = getCurrentCommitSHA()
	def parts = baseVersion.split('-')
	def newVersion = parts[0] + '-' + currentCommitSHA + '-' + parts[1]
					
	// change the version in POM
	sh "echo POM new version: $newVersion"
	sh "mvn -B versions:set -DnewVersion=${newVersion}"
	return newVersion
}

def getCurrentCommitSHA() {
	return executeShell('git rev-parse HEAD')
}

def tagChangesToGit(version) {
	sh """
		git add pom.xml
		git commit -m "update pom version BUT ONLY TO A GIT TAG"
		git tag "BUILD_${version}" -f
		git push origin "BUILD_${version}" -f
	"""
}

def uploadArtifactsToNexus(nexus_url, nexus_repo){
	sh "mvn -DperformRelease=true deploy -DaltDeploymentRepository=$nexus_repo::default::$nexus_url$nexus_repo"
}

/*
 * This function builds a docker image using the Dockerfile and push the image to Artifactory
 */
def buildDockerImageAndPushToArtifactory(url, repo, user, password){
	if(! fileExists ('Dockerfile')){
		echo 'Dockerfile does not exist - skip building docker images'
		return
	}
	
	def pomVersion = executeShell 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec'
	def artifactId = executeShell 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.artifactId}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec'
	
	def imageName = url + repo + ":" + pomVersion
	def jarName = artifactId + "-" + pomVersion + ".jar"

	sh "docker login -u $user -p $password $url"
	sh "docker build --build-arg JARNAME=$jarName -t  $imageName ."
	sh "docker push $imageName"
	sh "docker rmi $imageName"
	
	return imageName
}

def mavenBuild(String goal) {
	sh "mvn ${goal}"
}
