#!/usr/bin/env groovy
// Declarative Pipeline
// More at https://github.com/jenkinsci/pipeline-examples/tree/master/declarative-examples
pipeline {
    agent any
    
    stages {
        stage('Say Hello') {
            steps {
                echo 'Hello'
            }
        }
        stage('Git Info') {
            parallel {
                stage('Branch') {
                    steps {
                        echo "${params.GIT_BRANCH}"
                    }
                }
                stage('Commit') {
                    steps {
                        echo "${params.GIT_COMMIT}"
                    }
                }
            }
        }
        
        stage('True/False') {
            when {
                expression { params.INPUT ==~ /(?i)(Y|YES|T|TRUE|ON|RUN)/}
            }
            steps {
                echo "True"
            }
        }
    }
}
