stage('Say Hello') { echo 'Hello' }

parallel firstBranch: {
    stage('Say World') { echo 'World' }
}, secondBranch: {
    stage('Fortune Cookie') { echo "${params.INPUT}" }
}
if (params.INPUT == 'default') {
    stage('True') { echo 'I am default expression.' }
} 
else {
    stage('False') { echo 'I am not a default expression.' }
}

//stage('Fail') { sh 'cp bob bob' }

stage('Get confirmation') {
    milestone()
    input "Looks good?"
    milestone()
}

stage('Finish') {
    echo "Done"
}
