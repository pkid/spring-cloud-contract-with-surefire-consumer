#!/usr/bin/env groovy
//---------------------------------------------------------------------------
import groovy.transform.*
//---------------------------------------------------------------------------

// load pipeline functions
@Library('ngplibrary')
def gitPipeline = new io.ngp.GitPipeline()
def commitPipeline = new io.ngp.CommitPipeline()
def updateK8SPipeline = new io.ngp.K8SPipeline()
def notifyPipeline = new io.ngp.NotifyPipeline()

//variables

def newDockerImage
def githubRepo
def gitUrl
//---------------------------------------------------------------------------

node {
	try {
		// Send start notification
		notifyBuild('STARTED')

//stages
stage('Get Git Info') {
    node {
	    	deleteDir()
		def githubInfo = gitPipeline.getGithubInfo()
		def githubOrg = githubInfo['org']
		githubRepo = githubInfo['repo']
		def githubBranch = githubInfo['branch']
		gitUrl = 'git@github.wdf.sap.corp:' + githubOrg + '/' + githubRepo + '.git'
    }
}

stage('Commit') {
    node {
		deleteDir()
	    	commitPipeline.setGitUrl(gitUrl)
	   	commitPipeline.setGithubRepo(githubRepo)
	    	newDockerImage = commitPipeline.commit()
    }
}

stage('Update K8S') {
    node {
	    	def gitSHA = commitPipeline.getCurrentCommitSHA()
	        updateK8SPipeline.helmUpgrade(system: "trunk", helmRelease: githubRepo, newImage: newDockerImage, gitSHA: gitSHA)
    }
}

  	} catch (e) {
    		// If there was an exception thrown, the build failed
    		currentBuild.result = "FAILED"
    		throw e
  	} finally {
    		// Success or failure, always send notifications
   		notifyBuild(currentBuild.result)
  	}
}
//---------------------------------------------------------------------------

def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus = buildStatus ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  // Send notifications
//  slackSend (color: colorCode, message: summary)

  emailext (
      subject: subject,
      body: details,
    //  recipientProviders: [[$class: 'DevelopersRecipientProvider']],
			to: "${env.BUILD_USER_EMAIL}"
    )
}
