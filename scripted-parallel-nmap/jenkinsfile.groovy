#!/usr/bin/env groovy
// Run this outside the sandbox so you can get the node count

int executorCount = 0
def subnetList = []
def stepsForParallel = [:]

// Can't use these inline or else Jenkins will try to actually run the stages when they're getting created
def generateStage(job) {
    return {
        stage("${job}") { 
            echo "This is a ${job}."
        }
    }
}

// Might be a way to get this that doesn't break the sandbox
stage("Get Worker Count") {
    final jenkins = Jenkins.instance

    for (def computer in jenkins.computers) {
        executorCount += computer.numExecutors
    }
    print "Found ${executorCount} nodes"
}

// Need a node to run our subnet-split script, outputs a list of subnets based on workers available
node {
    stage("Split Subnets") {
        output = sh (
            script: "python ~/subnet-split.py --nodes ${executorCount} --cidr ${params.SUBNET}",
            returnStdout: true
        )
        subnetList = output.split(',')
        print "${subnetList}"
    }
}

// actually runs the parallel steps
stage("Parallel steps") {

    stepsForParallel = subnetList.collectEntries {
        ["${it}" : generateStage(it)]
    }

    parallel stepsForParallel
}
