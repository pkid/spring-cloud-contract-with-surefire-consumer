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

def getGithubInfo() {
    def githubInfo = [:]
    def tokens = "${env.JOB_NAME}".tokenize('/')
    githubInfo['org'] = tokens[tokens.size()-3]
    echo githubInfo['org']
    githubInfo['repo'] = tokens[tokens.size()-2]
    echo githubInfo['repo']
    githubInfo['branch'] = tokens[tokens.size()-1]
    echo githubInfo['branch']
    return githubInfo
}


