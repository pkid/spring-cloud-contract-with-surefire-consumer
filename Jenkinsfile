#!groovy
//---------------------------------------------------------------------------
import groovy.transform.Field
import static java.util.Arrays.asList
//import hudson.plugins.performance.JMeterParser
//---------------------------------------------------------------------------

// nexus
NEXUS_URL = 'http://nexus.wdf.sap.corp:8081/nexus/content/repositories/'
NEXUS_SNAPSHOTS_REPOSITORY = 'deploy.snapshots/'

// docker artifactory
DOCKER_ARTIFACTORY_URL = 'docker.wdf.sap.corp:51032'
DOCKER_ARTIFACTORY_USER = 'ASA1_NEXTGENPAYROLL'
DOCKER_ARTIFACTORY_PASSWORD = 'uyN}77vY}A39KUm5lEgS'
DOCKER_ARTIFACTORY_REPO_NAME = '/prototype/testcommon'

echo 'Pass 1'
def helperScriptUrl = 'https://github.wdf.sap.corp/raw/nextgenpayroll-infrastructure/public-sample-repo/master/help_new.groovy'
def fortifyScriptUrl = 'https://github.wdf.sap.corp/raw/ContinuousDelivery/jenkins-pipelines/master/scripts/fortifychecks.py'

@Field def helper
node{
    deleteDir()
    if(!fileExists('.pipeline')) sh 'mkdir .pipeline'
    sh "curl --insecure ${helperScriptUrl} -o .pipeline/helper.groovy"
    helper = load '.pipeline/helper.groovy'
}
echo 'Pass 2'

// global variables
def githubInfo = helper.getGithubInfo()
def githubOrg = githubInfo['org']
def githubRepo = githubInfo['repo']
def githubBranch = githubInfo['branch']
echo githubBranch

def gitUrl = 'git@github.wdf.sap.corp:' + githubOrg + '/' + githubRepo + '.git'
echo gitUrl

def newDockerImage

stage('Commit') {
    node {
        deleteDir()
        git url: 'git@github.wdf.sap.corp:nextgenpayroll/testcommon.git'
        def newPOMVersion = adjustPOMVersion()
        tagChangesToGit(newPOMVersion)
        uploadArtifactsToNexus(NEXUS_URL, NEXUS_SNAPSHOTS_REPOSITORY)
        newDockerImage = buildDockerImageAndPushToArtifactory(DOCKER_ARTIFACTORY_URL, DOCKER_ARTIFACTORY_REPO_NAME, DOCKER_ARTIFACTORY_USER, DOCKER_ARTIFACTORY_PASSWORD)
    }
}
