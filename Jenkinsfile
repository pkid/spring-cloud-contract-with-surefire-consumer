#!groovy
//---------------------------------------------------------------------------
import groovy.transform.Field
//import static java.util.Arrays.asList
//import hudson.plugins.performance.JMeterParser
//---------------------------------------------------------------------------


//nexus
NEXUS_URL = 'http://nexus.wdf.sap.corp:8081/nexus/content/repositories/'
NEXUS_SNAPSHOTS_REPOSITORY = 'deploy.snapshots/'

// docker artifactory
DOCKER_ARTIFACTORY_URL = 'docker.wdf.sap.corp:51032'
DOCKER_ARTIFACTORY_USER = 'ASA1_NEXTGENPAYROLL'
DOCKER_ARTIFACTORY_PASSWORD = 'uyN}77vY}A39KUm5lEgS'
DOCKER_ARTIFACTORY_REPO_NAME = '/prototype/test/public-sample-repo'

echo "Pass 1"
def helperScriptUrl = 'https://github.wdf.sap.corp/raw/nextgenpayroll-zugspitze-infrastructure/internal-jenkins-pipeline-parent/master/custom_helper.groovy'

def helper = null

node{
    deleteDir()
    if(!fileExists('.pipeline')) sh 'mkdir .pipeline'
    sh "curl --insecure ${helperScriptUrl} -o .pipeline/custom_helper.groovy"
    helper = load '.pipeline/custom_helper.groovy'
    echo helper.doStuff()
}
echo 'Pass 2'
//
//// global variables
//def githubInfo = helper.getGithubInfo()
//def githubOrg = githubInfo['org']
//def githubRepo = githubInfo['repo']
//def githubBranch = githubInfo['branch']
//echo githubBranch
//def gitUrl = 'git@github.wdf.sap.corp:' + ${githubOrg} + '/' + ${githubRepo} + '.git'
//echo "${gitUrl}"
// global variables

// global variables

def newDockerImage

stage('Commit') {
    node {
		println "Stage 1"
		echo helper.doStuff()
		
        deleteDir()
		println "Stage 2"
        git url: "git@github.wdf.sap.corp:nextgenpayroll-infrastructure/public-sample-repo.git"
		println "Stage 34"
//		def actualPOMVersion = executeShell 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec'
//        println actualPOMVersion
		def newPOMVersion = helper.adjustPOMVersion()
		println "Stage 4"
        helper.tagChangesToGit(newPOMVersion)
		println "Stage 5"
        helper.uploadArtifactsToNexus(NEXUS_URL, NEXUS_SNAPSHOTS_REPOSITORY)
		println "Stage 6"
        newDockerImage = helper.buildDockerImageAndPushToArtifactory(DOCKER_ARTIFACTORY_URL, DOCKER_ARTIFACTORY_REPO_NAME, DOCKER_ARTIFACTORY_USER, DOCKER_ARTIFACTORY_PASSWORD)
		println "Stage 7"
    }
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

def executeShell(command) {
  def result = sh returnStdout: true, script: command
  return result.trim()
}

/*
 * This function assumes a version M.m.i-SNAPSHOT in the pom.xml and creates a new version M.m.i-SHA-SNAPSHOT
 * SHA is the github commit id
 */
//def adjustPOMVersion() {
//	// get the version, e.g., M.m.i-SNAPSHOT
//	def baseVersion = executeShell 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec'
//
//	// create the new version, e.g., M.m.i-SHA-SNAPSHOT
//	def currentCommitSHA = getCurrentCommitSHA()
//	def parts = baseVersion.split('-')
//	def newVersion = parts[0] + '-' + currentCommitSHA + '-' + parts[1]
//	
//	// change the version in POM
//	sh "echo POM new version: $newVersion"
//	sh "mvn -B versions:set -DnewVersion=${newVersion}"
//	return newVersion
//}

def tagChangesToGit(version) {
	sh """
     git add pom.xml
     git commit -m "update pom version BUT ONLY TO A GIT TAG"
     git tag "BUILD_${version}" -f
     
     whoami
     pwd
     
     git push origin "BUILD_${version}" -f
    """
}

def uploadArtifactsToNexus(nexus_url, nexus_repo){
	sh "mvn -DperformRelease=true deploy -DaltDeploymentRepository=$nexus_repo::default::$nexus_url$nexus_repo"
}

def getCurrentCommitSHA() {
	return executeShell('git rev-parse HEAD')
}

def mavenBuild(String goal) {
	//def mvnHome = tool 'M3'
	//sh """
    //  ${mvnHome}/bin/mvn ${goal}
    //"""
	sh "mvn ${goal}"
}
