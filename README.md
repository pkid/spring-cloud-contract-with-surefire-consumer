# public-sample-repo
## Jenkinsfile
Just copy the Jenkinsfile into you project root folder. 

This Jenkinsfile creates a job for service in Jenkins.  
With the Job the commit gets tagged and pushed to nexus. Also dockerimage gets build (related to Dockerfile inside the project) and pushed to Artifactory.

## Dockerfile
Just copy the Dockerfile into you project root folder. 

Difference between "normal" project and multi module project:  
You have to change one line inside the Dockerfile
* "Normal" project use: 
```Dockerfile
        ADD /target/$JARNAME $PROJECTNAME
```
* Multi module project use: 
```Dockerfile
        ADD /service/target/$JARNAME $PROJECTNAME
```

## POM.XML
In the POM file you have to specify the build plugins for you JAR. To have the possibility to trace back from the jar to the commit we decided to write the GIT-SHA into the MANIFEST.MF inside the JAR.  

What you have to add into you Main-POM (`root/pom.xml`) to reach this?  

1. A Git Connection:
```xml
<!-- GIT connection to project to get GIT SHA-->
<scm>
	<connection>scm:git:git://github.wdf.sap.corp:nextgenpayroll-zugspitze-infrastructure/public-sample-repo.git</connection>
</scm>
```

2. Two Plugins:
```xml
<!-- Get GIT SHA -->
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>buildnumber-maven-plugin</artifactId>
	<version>1.1</version>
	<configuration>
		<shortRevisionLength>7</shortRevisionLength>
	</configuration>
	<executions>
		<execution>
			<phase>validate</phase>
			<goals>
				<goal>create</goal>
			</goals>
		</execution>
	</executions>
</plugin>
<!-- Store GIT SHA in MANIFEST.MF-->
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-jar-plugin</artifactId>
	<version>2.1</version>
	<configuration>
		<archive>
			<manifest>
				<addDefaultImplementationEntries>true</addDefaultImplementationEntries>
			</manifest>
			<manifestEntries>
				<GIT-SHA>${buildNumber}</GIT-SHA>
			</manifestEntries>
		</archive>
	</configuration>
</plugin>
```
Attention with the artifacId's of you different POM-Files:
* root/pom.xml: 
```xml

        <artifactId>sample-repo</artifactId>
 ```

* root/api/pom.xml:  

```xml
        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-api</artifactId>
```
* root/service/pom.xml:  

```xml
        <parent>
        ...
            <artifactId>sample-repo</artifactId>
        ...
        </parent>
        ...
        <artifactId>sample-repo-service</artifactId>
```
