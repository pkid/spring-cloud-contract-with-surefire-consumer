
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

