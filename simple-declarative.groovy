#!/usr/bin/env groovy
// Declarative Pipeline
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
        
        if (params.INPUT) {
            stage('Flat True') {
                steps {
                    echo "True"
                }
            }
        }
        else {
            stage('Flat False') {
                steps {
                    echo "False"
                }
            }
        } 
    }
}
