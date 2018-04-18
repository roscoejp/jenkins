stage('Say Hello') { echo 'Hello' }

parallel firstBranch: {
    stage('Say World') { 
        echo 'World' 
    }
}, secondBranch: {
    stage('Fortune Cookie') { 
        echo "${params.TEXTINPUT}"
        parallel firstBranch: {
            stage('Say Nested Word') { 
                echo 'Nested World' 
            }
        }, secondBranch: {
            stage('Nested Fortune Cookie') { 
                echo "Nested ${params.TEXTINPUT}" 
            }
        }
    }
}
if (params.INPUT) {
    stage('True') { 
        echo 'I am True expression.' 
    }
    stage('Arbitrary Complication') {
        echo 'I am a complication.'   
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
    input "${params.PROMPT}"
    milestone()
}

stage('Finish') {
    echo "Done"
}
