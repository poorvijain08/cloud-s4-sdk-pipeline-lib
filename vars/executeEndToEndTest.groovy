import com.sap.icd.jenkins.E2ETestCommandHelper
import com.sap.icd.jenkins.EndToEndTestType

def call(Map parameters = [:]) {
    handleStepErrors(stepName: 'executeEndToEndTest',stepParameters: parameters) {
        final script = parameters.script

        def appUrls = parameters.get('appUrls')
        EndToEndTestType type = parameters.get('endToEndTestType')

        if(appUrls) {
            for(def appUrl : appUrls) {

                String shScript
                List credentials = []

                if(appUrl instanceof String){
                    shScript = E2ETestCommandHelper.generate(type, appUrl)
                }
                else if(appUrl instanceof Map && appUrl.url && appUrl.credentialId) {
                    String url = appUrl.url
                    shScript = E2ETestCommandHelper.generate(type, url)

                    String credentialId = appUrl.credentialId
                    credentials.add([$class: 'UsernamePasswordMultiBinding', credentialsId: credentialId, passwordVariable: 'e2e_password', usernameVariable: 'e2e_username'])
                }
                else {
                    error("Each appUrl in the configuration must be either a String or a Map containing a property url and a property credentialId.")
                }

                withCredentials(credentials) {
                    executeNpm(script: script) {
                        sh shScript
                    }
                }
            }
        } else {
            echo "End to end test skipped because no appUrls defined!"
        }
    }
}