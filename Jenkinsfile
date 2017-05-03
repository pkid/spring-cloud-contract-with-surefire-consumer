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
		notifyBuild('STARTED')

//stages
stage('Get Git Info') {
    node {
	    	notifyPipeline.notifyBuild('STARTED')
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
