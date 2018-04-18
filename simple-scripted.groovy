stage('Say Hello') { echo 'Hello' }

parallel firstBranch: {
    stage('Say World') { 
        echo 'World' 
    }
    stage('Arbitrary Complication') {
        echo 'I am a complication.'   
    }
}, secondBranch: {
    stage('Fortune Cookie') { 
        echo "${params.TEXTINPUT}"
    }
}
if (params.INPUT) {
    stage('True') { 
        echo 'I am True expression.' 
    }
} 
else {
    stage('False') { 
        echo 'I am a False expression.' 
    }
}

//stage('Fail') { sh 'cp bob bob' }

stage('Get confirmation') {
    milestone()
    def FEEDBACK = input (submitterParameter: 'SUBMITTER', submitter: 'roscoejp', message: "${params.PROMPT}", ok: 'Bob')
    echo "Bob'd by: ${FEEDBACK.SUBMITTER}"
    milestone()
}

stage('Finish') {
    echo "Done"
}
