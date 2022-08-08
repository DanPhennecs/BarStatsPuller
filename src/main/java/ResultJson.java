import com.fasterxml.jackson.annotation.*;
import org.json.simple.*;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"BarVersion","Region","Customer","orgId","accessToken","policyUrl","StatsUrl","OrgDomain","ExecutionIds","no_Backup_Executions","state","started","completed","duration",
        "totalAffectedRecords","totalRecordsFailed","percentageCompleted","NOT_VISIBLE","VARCHAR","TRINO_CRASHED","PARTITION_COUNT",
        "NUMBER_FORMAT_EXCEPTION", "PROBLEM_ADDING_ROW","MISMATCHED_FIELD_TYPE","ERROR_EXEC_QUERY","NOT_100_PERCENT","REPLACE_PARTITIONS",
        "SPLIT_BUFFERING","ERROR_HIVE_SPLIT", "ERROR_WRITE_HIVE","ERROR_PROCESS_PARTITION","ERROR_FETCHING_RESULTS",
        "UNCOMMON_ERRORS"})
class ResultJson {
    @JsonProperty("BarVersion")
    private String barVersion;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("accessToken")
    private String accessToken;
    @JsonProperty("Customer")
    private String customer;
    @JsonProperty("orgId")
    private String orgId;
    @JsonProperty("badToken")
    private JSONObject badToken;
    @JsonProperty("policyUrl")
    private String policyUrl;
    @JsonProperty("StatsUrl")
    private String statsUrl;
    @JsonProperty("OrgDomain")
    private String orgDomain;
    @JsonProperty("ExecuctionIds")
    private String execuctionIds;
    @JsonProperty("no_Backup_Executions")
    private String no_Backup_Executions;
    @JsonProperty("state")
    private String state;
    @JsonProperty("started")
    private String started;
    @JsonProperty("completed")
    private String completed;
    @JsonProperty("duration")
    private String duration;
    @JsonProperty("totalAffectedRecords")
    private String totalAffectedRecords;
    @JsonProperty("totalRecordsFailed")
    private String totalRecordsFailed;
    @JsonProperty("percentageCompleted")
    private Double percentageCompleted;
    @JsonProperty("UNCOMMON_ERRORS")
    private HashMap uncommon_Errors;
    @JsonProperty("NOT_VISIBLE")
    private String not_Visible;
    @JsonProperty("VARCHAR")
    private String varchar;
    @JsonProperty("TRINO_CRASHED")
    private String trino_Crashed;
    @JsonProperty("REPLACE_PARTITIONS")
    private String allOpsOther;
    @JsonProperty("PARTITION_COUNT")
    private String partition;
    @JsonProperty("NUMBER_FORMAT_EXCEPTION")
    private String numberFormatException;
    @JsonProperty("PROBLEM_ADDING_ROW")
    private String problem_adding_row;
    @JsonProperty("MISMATCHED_FIELD_TYPE")
    private String mismatched_field_types;
    @JsonProperty("ERROR_EXEC_QUERY")
    private String errorExecQuery;
    @JsonProperty("NOT_100_PERCENT")
    private String notCompleted;
    @JsonProperty("SPLIT_BUFFERING")
    private String splitBuffering;
    @JsonProperty("ERROR_HIVE_SPLIT")
    private String errorHiveSplit;
    @JsonProperty("ERROR_WRITE_HIVE")
    private String errorWriteHive;
    @JsonProperty("ERROR_PROCESS_PARTITION")
    private String errorProcessPartition;
    @JsonProperty("ERROR_FETCHING_RESULTS")
    private String errorFetchingResults;

    public ResultJson(String region, String accessToken, String policyUrl, String no_Backup_Executions, String state,
                      String started, String completed, String duration, String totalAffectedRecords, String totalRecordsFailed,
                      Double percentageCompleted, HashMap uncommon_Errors, String not_Visible, String varchar,
                      String trino_Crashed, String partition, String numberFormatException, String problem_adding_row) {
        this.region = region;
        this.accessToken = accessToken;
        this.policyUrl = policyUrl;
        this.no_Backup_Executions = no_Backup_Executions;
        this.state = state;
        this.started = started;
        this.completed = completed;
        this.duration = duration;
        this.totalAffectedRecords = totalAffectedRecords;
        this.totalRecordsFailed = totalRecordsFailed;
        this.percentageCompleted = percentageCompleted;
        this.uncommon_Errors = uncommon_Errors;
        this.not_Visible = not_Visible;
        this.varchar = varchar;
        this.trino_Crashed = trino_Crashed;
        this.partition = partition;
        this.numberFormatException = numberFormatException;
        this.problem_adding_row = problem_adding_row;
    }

    public ResultJson () {}

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPolicyUrl() {
        return policyUrl;
    }

    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    public String getStatsUrl() {
        return statsUrl;
    }

    public void setStatsUrl(String statsUrl) {
        this.statsUrl = statsUrl;
    }

    public String getNo_Backup_Executions() {
        return no_Backup_Executions;
    }

    public void setNo_Backup_Executions(String no_Backup_Executions) {
        this.no_Backup_Executions = no_Backup_Executions;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotalAffectedRecords() {
        return totalAffectedRecords;
    }

    public void setTotalAffectedRecords(String totalAffectedRecords) {
        this.totalAffectedRecords = totalAffectedRecords;
    }

    public String getTotalRecordsFailed() {
        return totalRecordsFailed;
    }

    public void setTotalRecordsFailed(String totalRecordsFailed) {
        this.totalRecordsFailed = totalRecordsFailed;
    }

    public Double getPercentageCompleted() {
        return percentageCompleted;
    }

    public void setPercentageCompleted(Double percentageCompleted) {
        this.percentageCompleted = percentageCompleted;
    }

    public HashMap getUncommon_Errors() {
        return uncommon_Errors;
    }

    public void setUncommon_Errors(HashMap uncommon_Errors) {
        this.uncommon_Errors = uncommon_Errors;
    }

    public String getNot_Visible() {
        return not_Visible;
    }

    public void setNot_Visible(String not_Visible) {
        this.not_Visible = not_Visible;
    }

    public String getVarchar() {
        return varchar;
    }

    public void setVarchar(String varchar) {
        this.varchar = varchar;
    }

    public String getTrino_Crashed() {
        return trino_Crashed;
    }

    public void setTrino_Crashed(String trino_Crashed) {
        this.trino_Crashed = trino_Crashed;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getNumberFormatException() {
        return numberFormatException;
    }

    public void setNumberFormatException(String numberFormatException) {
        this.numberFormatException = numberFormatException;
    }

    public String getMismatched_field_types() {
        return mismatched_field_types;
    }

    public void setMismatched_field_types(String mismatched_field_types) {
        this.mismatched_field_types = mismatched_field_types;
    }

    public String getErrorExecQuery() {
        return errorExecQuery;
    }

    public void setErrorExecQuery(String errorExecQuery) {
        this.errorExecQuery = errorExecQuery;
    }

    public String getProblem_adding_row() {
        return problem_adding_row;
    }

    public void setProblem_adding_row(String problem_adding_row) {
        this.problem_adding_row = problem_adding_row;
    }


    public String getExecuctionIds() {
        return execuctionIds;
    }

    public void setExecuctionIds(String execuctionIds) {
        this.execuctionIds = execuctionIds;
    }

    public String getNotCompleted() {
        return notCompleted;
    }

    public void setNotCompleted(String notCompleted) {
        this.notCompleted = notCompleted;
    }

    public JSONObject getBadToken() {
        return badToken;
    }

    public void setBadToken(JSONObject badToken) {
        this.badToken = badToken;
    }

    public String getAllOpsOther() {
        return allOpsOther;
    }

    public void setAllOpsOther(String allOpsOther) {
        this.allOpsOther = allOpsOther;
    }

    public String getSplitBuffering() {
        return splitBuffering;
    }

    public void setSplitBuffering(String splitBuffering) {
        this.splitBuffering = splitBuffering;
    }

    public String getErrorHiveSplit() {
        return errorHiveSplit;
    }

    public void setErrorHiveSplit(String errorHiveSplit) {
        this.errorHiveSplit = errorHiveSplit;
    }

    public String getErrorWriteHive() {
        return errorWriteHive;
    }

    public void setErrorWriteHive(String errorWriteHive) {
        this.errorWriteHive = errorWriteHive;
    }

    public String getErrorProcessPartition() {
        return errorProcessPartition;
    }

    public void setErrorProcessPartition(String errorProcessPartition) {
        this.errorProcessPartition = errorProcessPartition;
    }

    public String getErrorFetchingResults() {
        return errorFetchingResults;
    }

    public void setErrorFetchingResults(String errorFetchingResults) {
        this.errorFetchingResults = errorFetchingResults;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgDomain() {
        return orgDomain;
    }

    public void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    public String getBarVersion() {
        return barVersion;
    }

    public void setBarVersion(String barVersion) {
        this.barVersion = barVersion;
    }
}

