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
                sh "./gradlew clean setupCIWorkspace --no-daemon"
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew build -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
            }
        }

        stage('Archive') {
            steps {
                archive includes: 'build/libs/*.jar'
                junit 'build/test-results/**/*.xml'
            }
        }

        stage('Deploy') {
            steps {
                sh "./gradlew publishMavenJavaPublicationToMavenRepository -PBUILD_NUMBER=${env.BUILD_NUMBER} -PDEPLOY_DIR=/var/www/maven.hopper.bunnies.io --no-daemon"
            }
        }
    }
}
