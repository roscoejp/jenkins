// Run this from the script console in Jenkins. Replace MY_JENKINS_CREDENTIAL_ID with your credential ID
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.*

  
def cred_id = "MY_JENKINS_CREDENTIAL_ID"

def creds = CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class,
        Jenkins.instance, // replace with item to get folder or item scoped credentials 
        null,
        null
);

for (credential in creds) { 
    if(credential.id == cred_id){
        if (credential instanceof UsernamePasswordCredentialsImpl) {
            println credential.getId() + " " + credential.getUsername() + " " + credential.getPassword().getPlainText()
        } else if (credential instanceof org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl) {
            println credential.getId() + " " + credential.fileName + " " + credential.content.getText("UTF-8") 
        } else if (credential instanceof StringCredentialsImpl) {
            println credential.getId() + " " + credential.getSecret().getPlainText() 
        } else if(credential instanceof BasicSSHUserPrivateKey) {
            println credential.getId() + " " + credential.getUsername() + "\n" + credential.getPrivateKey()
        } else if (credential.getClass().toString() == "class com.microsoft.azure.util.AzureCredentials") {
            println "AzureCred:" + credential.getSubscriptionId() + " " + credential.getClientId() + " " + credential.getPlainClientSecret() + " " + credential.getTenant()
        } else if (credential.getClass().toString() == "class org.jenkinsci.plugins.github_branch_source.GitHubAppCredentials") {
            println credential.getId() + " " + credential.getUsername() + "\n" + credential.getPrivateKey().getPlainText()
        } else if (credential.getClass().toString() == "class com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl") {
            println credential.getId() + " " + credential.getAccessKey() + " " + credential.getSecretKey()
        } else {
            println credential.getClass()
        } 
    }
}
