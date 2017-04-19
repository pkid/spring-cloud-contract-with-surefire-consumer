#!/usr/bin/env groovy
//---------------------------------------------------------------------------
import groovy.transform.*
//---------------------------------------------------------------------------

// load pipeline functions
@Library('ngplibrary')
def gitPipeline = new io.ngp.GitPipeline()
def commitPipeline = new io.ngp.CommitPipeline()

//variables
def newDockerImage
def githubRepo
def gitUrl
//---------------------------------------------------------------------------

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
		echo "Jenkinsfile: $githubRepo"
	    	commitPipeline.commit()
    }
}
//---------------------------------------------------------------------------
