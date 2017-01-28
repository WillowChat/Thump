#!/usr/bin/env groovy

pipeline {
    agent any

    post {
        success {
            ircSendSuccess()
        }

        failure {
            ircSendFailure()
        }
    }

    stages {
        stage('Checkout') {
            steps {
                ircSendStarted()

                checkout scm
                sh "rm -Rv build || true"
            }
        }

        stage('Setup') {
            steps {
                sh "./gradlew clean setupCIWorkspace --no-daemon -Dorg.gradle.jvmargs=-Xmx1024m"
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew build -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon -Dorg.gradle.jvmargs=-Xmx1024m"
            }
        }

        stage('Archive') {
            steps {
                parallel(
                    archive: { archive includes: 'build/libs/*.jar' },
                    junit: { junit 'build/test-results/**/*.xml' },
                    maven: {
                        sh "./gradlew generatePomFileForMavenJavaPublication -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"

                        stash includes: 'build/publications/mavenJava/pom-default.xml,build/libs/*.jar', name: 'maven_artifacts', useDefaultExcludes: false
                    }
                )
            }
        }

        stage('Deploy') {
            agent {
                label 'maven_repo'
            }

            steps {
                sh "rm -Rv build || true"

                unstash 'maven_artifacts'

                sh "ls -lR build"

                sh "find build/libs -name Thump\\*${env.BUILD_NUMBER}.jar | head -n 1 | xargs -I '{}' mvn install:install-file -Dfile={} -DpomFile=build/publications/mavenJava/pom-default.xml -DlocalRepositoryPath=/var/www/maven.hopper.bunnies.io"
                sh "find build/libs -name Thump\\*sources.jar | head -n 1 | xargs -I '{}' mvn install:install-file -Dfile={} -Dclassifier=sources -DpomFile=build/publications/mavenJava/pom-default.xml -DlocalRepositoryPath=/var/www/maven.hopper.bunnies.io"
                sh "find build/libs -name Thump\\*deobf.jar | head -n 1 | xargs -I '{}' mvn install:install-file -Dfile={} -Dclassifier=deobf -DpomFile=build/publications/mavenJava/pom-default.xml -DlocalRepositoryPath=/var/www/maven.hopper.bunnies.io"
            }
        }
    }
}
