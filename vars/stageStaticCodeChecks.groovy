import com.sap.cloud.sdk.s4hana.pipeline.QualityCheck
import com.sap.cloud.sdk.s4hana.pipeline.ReportAggregator
import com.sap.piper.ConfigurationLoader

def call(Map parameters = [:]) {
    def stageName = 'staticCodeChecks'
    def script = parameters.script
    piperStageWrapper(stageName: stageName, script: script) {
        Map configuration = ConfigurationLoader.stageConfiguration(script, stageName)

        checkPmd script: script, excludes: configuration.pmdExcludes
        ReportAggregator.instance.reportStaticCodeExecution(QualityCheck.PmdCheck)

        checkFindbugs script: script, excludeFilterFile: configuration.findbugsExcludesFile
        ReportAggregator.instance.reportStaticCodeExecution(QualityCheck.FindbugsCheck)
    }
}
