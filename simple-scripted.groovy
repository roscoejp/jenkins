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
        parallel NestedFirstBranch: {
            stage('Say Nested Word') { 
                echo 'Nested World' 
            }
        }, NestedSecondBranch: {
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
