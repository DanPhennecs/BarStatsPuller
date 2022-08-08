import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.*;

import com.fasterxml.jackson.databind.*;
import com.sforce.soap.partner.*;
import com.sforce.ws.*;
import io.kubernetes.client.openapi.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;


public class Parser {


    public static PartnerConnection partnerConnection;
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String ORGID = "";
    public static String ARN = "";
    public static String DOMAIN = "";
    public static String REGION = "";
    public static Boolean IS_SANBOX = false;
    public static String partnerUrl = "";
    public static String orgDomain = "";
    public static String customerName = "";
    public static boolean includeTokenBoolean = false;
    public static ResultJson resultJson = new ResultJson();
    public static String executionId = "";
    public static String ACCESSTOKEN = "";

    public Parser() {
    }

    private static void createArn() {

        switch (REGION) {
            case "us-west-1": ARN = "arn:aws:eks:us-west-1:410275221728:cluster/phoenix-prod-us-west-1";
                DOMAIN = "usw1";
                break;
            case "us-east-1": ARN = "arn:aws:eks:us-east-1:410275221728:cluster/phoenix-prod-us-east-1";
                DOMAIN = "us-east-1";
                break;
            case "af-south-1": ARN = "arn:aws:eks:af-south-1:410275221728:cluster/phoenix-prod-af-south-1";
                DOMAIN = "afs1";
                break;
            case "ap-northeast-1": ARN = "arn:aws:eks:ap-northeast-1:410275221728:cluster/phoenix-prod-ap-northeast-1";
                DOMAIN = "apne1";
                break;
            case "ap-northeast-2": ARN = "arn:aws:eks:ap-northeast-2:410275221728:cluster/phoenix-prod-ap-northeast-2";
                DOMAIN = "apne2";
                break;
            case "ap-southeast-2": ARN = "arn:aws:eks:ap-southeast-2:410275221728:cluster/phoenix-prod-ap-southeast-2";
                DOMAIN = "apse2";
                break;
            case "ca-central-1": ARN = "arn:aws:eks:ca-central-1:410275221728:cluster/phoenix-prod-ca-central-1";
                DOMAIN = "cac1";
                break;
            case "eu-central-1": ARN = "arn:aws:eks:eu-central-1:410275221728:cluster/phoenix-prod-eu-central-1";
                DOMAIN = "euc1";
                break;
            case "eu-north-1": ARN = "arn:aws:eks:eu-north-1:410275221728:cluster/phoenix-prod-eu-north-1";
                DOMAIN = "eun1";
                break;
            case "eu-west-1": ARN = "arn:aws:eks:eu-west-1:410275221728:cluster/phoenix-prod-eu-west-1";
                DOMAIN = "euw1";
                break;
            case "eu-west-2": ARN = "arn:aws:eks:eu-west-2:410275221728:cluster/phoenix-prod-eu-west-2";
                DOMAIN = "euw2";
                break;
            case "eu-west-3": ARN = "arn:aws:eks:eu-west-3:410275221728:cluster/phoenix-prod-eu-west-3";
                DOMAIN = "euw3";
                break;
            case "me-south-1": ARN = "arn:aws:eks:me-south-1:410275221728:cluster/phoenix-me-south-1";
                DOMAIN = "mes1";
                break;
            case "sa-east-1": ARN = "arn:aws:eks:sa-east-1:410275221728:cluster/phoenix-prod-sa-east-1";
                DOMAIN = "sae1";
                break;
            case "us-west-2": ARN = "arn:aws:eks:us-west-2:410275221728:cluster/phoenix-prod-us-west-2";
                DOMAIN = "usw2";
                break;
            case "eu-south-1": ARN = "arn:aws:eks:eu-south-1:410275221728:cluster/phoenix-prod-eu-south-1";
                DOMAIN = "eus1";
                break;
            case "ap-east-1": ARN = "arn:aws:eks:ap-east-1:410275221728:cluster/phoenix-prod-ap-east-1";
                DOMAIN = "ape1";
                break;
            case "ap-south-1": ARN = "arn:aws:eks:ap-south-1:410275221728:cluster/phoenix-prod-ap-south-1";
                DOMAIN = "aps1";
                break;

        }
    }

    public static String run() throws IOException, InterruptedException, ApiException {

        JSONParser parser = new JSONParser();

        if (REGION.isBlank()) {
            pullFromProvisionDB();
        }

        createArn();
        removeAllErrors();
        if (ACCESSTOKEN.isBlank()) {
            setAccessToken();
        }

        if (resultJson.getCustomer() == null) {
            getUserinfo();
        }

        if (partnerConnection == null && !partnerUrl.isBlank()) {
            try {
                buildPartnerConnection();
                customerName = partnerConnection.getUserInfo().getOrganizationName();
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        }

        checkBarVersion();



//        try {
//            QueryResult queryResult = partnerConnection.query("Select id from Account");
//            int size = queryResult.getSize();
//        } catch (ConnectionException e) {
//            throw new RuntimeException(e);
//        }
        JSONObject policyJson = new JSONObject();
        policyJson = getPolicies(ACCESSTOKEN);
        JSONArray test = (JSONArray) policyJson.get("backupExecutionList");
        List<String> listOfExecIds = new ArrayList<>();

        if (test != null) {
            test.forEach(item -> {
                JSONObject jsonObject = (JSONObject) item;
//                if (!jsonObject.get("state").toString().contains("STALL"))
                    listOfExecIds.add(jsonObject.get("backupExecutionId").toString());
            });
            resultJson.setExecuctionIds(listOfExecIds.toString());
        }

        resultJson.setRegion(REGION);
        resultJson.setOrgId(ORGID);
        resultJson.setCustomer(customerName);

        if (includeTokenBoolean) {
            resultJson.setAccessToken(ACCESSTOKEN);
            resultJson.setPolicyUrl(policyJson.get("policyUrl").toString());
            resultJson.setOrgDomain(orgDomain+"/services/data/v54.0/query/?q=");
        } else {
            resultJson.setAccessToken(null);
        }

        JSONArray backupArray = (JSONArray) policyJson.get("backupExecutionList");


        if (backupArray != null && policyJson.size() > 0) {
            int numberOfBackups = backupArray.size();
            if (numberOfBackups > 0) {
                JSONObject newestRun = (JSONObject) backupArray.get(backupArray.size() - 1);
                parseBackup(getBackupStats(newestRun, ACCESSTOKEN));
            } else
                resultJson.setNo_Backup_Executions("true");
        } else
            resultJson.setNo_Backup_Executions("true");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultJson);

    }

    private static void buildPartnerConnection() throws ConnectionException {

        ConnectorConfig partnerConfig = new ConnectorConfig();
        partnerConfig.setServiceEndpoint(partnerUrl);
        partnerConfig.setSessionId(ACCESSTOKEN);
        partnerConnection = Connector.newConnection(partnerConfig);
    }

    private static JSONObject getPolicies(String accessToken) throws IOException {
        String urlString = "https://" + DOMAIN + ".sf.k8sphoenix.com/" + ORGID + "/backup/execute?size=1000";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            InputStream ip = con.getInputStream();
            BufferedReader br1 =
                    new BufferedReader(new InputStreamReader(ip));

            StringBuilder response = new StringBuilder();
            String responseSingle = null;
            while ((responseSingle = br1.readLine()) != null) {
                response.append(responseSingle);
            }

            json = (JSONObject) parser.parse(response.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            con.disconnect();
        }
        json.put("policyUrl", urlString);
        return json;
    }

    public static JSONObject getBackupStats(JSONObject runJSon, String accessToken) throws IOException {
        if (executionId.isBlank()) {
            executionId = runJSon.get("backupExecutionId").toString();
        }

        String urlString = "https://" + DOMAIN + ".sf.k8sphoenix.com/" + ORGID + "/backup/" + runJSon.get("policyId") + "/execute/" + executionId;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();

        try {
            // url for microsoft congnitive server.
            // set the request method and properties.
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);

            InputStream ip = con.getInputStream();
            BufferedReader br1 =
                    new BufferedReader(new InputStreamReader(ip));

            StringBuilder response = new StringBuilder();
            String responseSingle = null;
            while ((responseSingle = br1.readLine()) != null) {
                response.append(responseSingle);
            }

            json = (JSONObject) parser.parse(response.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            con.disconnect();
        }

        if (includeTokenBoolean)
            resultJson.setStatsUrl(urlString);

        return json;
    }

    private static JSONObject getUserinfo() throws IOException {
        String domain = IS_SANBOX.booleanValue() ? "test" : "login";
        String urlString = "https://"+domain+".salesforce.com/services/oauth2/userinfo";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + ACCESSTOKEN);

            InputStream ip = con.getInputStream();
            BufferedReader br1 =
                    new BufferedReader(new InputStreamReader(ip));

            StringBuilder response = new StringBuilder();
            String responseSingle = null;
            while ((responseSingle = br1.readLine()) != null) {
                response.append(responseSingle);
            }

            json = (JSONObject) parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) json.get("urls");
            orgDomain = (String) jsonObject.get("custom_domain");
            partnerUrl = (String) jsonObject.get("partner");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            con.disconnect();
        }

        return json;
    }

    private static void parseBackup(JSONObject jsonObject) {
        AtomicReference<JSONObject> object = new AtomicReference<>(new JSONObject());
        AtomicReference<JSONObject> backupStatsSingle = new AtomicReference<>(new JSONObject());
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<JSONArray> uncommonErrorJson = new AtomicReference<>(new JSONArray());
        if (includeTokenBoolean) {
            result.set((String) jsonObject.get("url") + "\n");
        } else {
            result.set("");
        }

        try {

            JSONArray jsonArray = (JSONArray) jsonObject.get("backupExecutionBySObject");

            JSONObject backupStats = (JSONObject) jsonObject.get("backupStats");

            List<String> notVisList = new ArrayList<>();
            List<String> varCharList = new ArrayList<>();
            List<String> trinoCrashedList = new ArrayList<>();
            List<String> partitionList = new ArrayList<>();
            List<String> numberFormatList = new ArrayList<>();
            List<String> addRowList = new ArrayList<>();
            List<String> mismatchFieldType = new ArrayList<>();
            List<String> errorExecQueryArray = new ArrayList<>();
            List<String> notCompleteList = new ArrayList<>();
            List<String> allOpsOtherThanList = new ArrayList<>();
            List<String> splitBufferingList = new ArrayList<>();
            List<String> errorHiveSplitList = new ArrayList<>();
            List<String> errorWriteToHiveList = new ArrayList<>();
            List<String> errorProcessPartitionList = new ArrayList<>();
            List<String> errorFetchingResultsList = new ArrayList<>();

            HashMap<String,String> uncommonMap = new HashMap<>();

            Long ms = (Long) jsonObject.get("elapsedMs");

            StringBuffer text = new StringBuffer("");
            if (ms > DAY) {
                text.append(ms / DAY).append("days ");
                ms %= DAY;
            }
            if (ms > HOUR) {
                text.append(ms / HOUR).append("hours ");
                ms %= HOUR;
            }
            if (ms > MINUTE) {
                text.append(ms / MINUTE).append("minutes ");
                ms %= MINUTE;
            }
            if (ms > SECOND) {
                text.append(ms / SECOND).append("seconds ");
                ms %= SECOND;
            }
            text.append(ms + "ms");

            DecimalFormat formatter = new DecimalFormat("#,###");

            resultJson.setState((String) jsonObject.get("state"));
            resultJson.setStarted((String) jsonObject.get("executionStartTs"));
            resultJson.setCompleted((String) jsonObject.get("executionEndTs"));
            resultJson.setDuration(text.toString());
            resultJson.setTotalAffectedRecords(formatter.format(backupStats.get("totalAffectedRecords")));
            resultJson.setTotalRecordsFailed(formatter.format(backupStats.get("totalRecordsFailed")));
            resultJson.setPercentageCompleted((Double) backupStats.get("percentageCompleted"));

            AtomicLong totalEffectedRecords = new AtomicLong();

            jsonArray.forEach(json -> {
                object.set((JSONObject) json);
                JSONObject backupStatsForObject = (JSONObject) object.get().get("backupStats");
                JSONArray errorArray = (JSONArray) object.get().get("errorList");
                String state = (String) object.get().get("state");

                String errorMsg = object.get().toJSONString();
                Double percentageCompleted = (Double) backupStatsForObject.get("percentageCompleted");
                Long objectTotalRecords = (Long) backupStatsForObject.get("totalAffectedRecords");
                if (objectTotalRecords != null)
                    totalEffectedRecords.set(totalEffectedRecords.get() + objectTotalRecords);

                if (errorArray.size() > 0) {
                    if (percentageCompleted != null && percentageCompleted < 100 )
                        notCompleteList.add((String) object.get().get("sObjectType"));
                    if (errorMsg.contains("not visible"))
                        notVisList.add((String) object.get().get("sObjectType"));
                    else if (errorMsg.contains("Cannot truncate") || errorMsg.contains("table schema has changed"))
                        varCharList.add((String) object.get().get("sObjectType"));
                    else if (errorMsg.contains("Expected response code to be 200, but was 503") ||
                            errorMsg.contains("The node may have crashed or be under too much load") ||
                            errorMsg.contains("Unexpected response from http:\\/\\/trino-worker-")){
                        trinoCrashedList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Backup count query returned null despite being under the threshold for a single partition!")) {
                        partitionList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("NumberFormatException: For input string")) {
                        numberFormatList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("IOException writing ORC batch: Problem adding row to")) {
                        addRowList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Mismatched field types between") ||
                            errorMsg.contains("Insert query has mismatched column types")) {
                        mismatchFieldType.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Error executing query")) {
                        errorExecQueryArray.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("All operations other than the following update operations were completed")) {
                        allOpsOtherThanList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Split buffering for")) {
                        splitBufferingList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Error opening Hive split")) {
                        errorHiveSplitList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Error committing write to Hive")) {
                        errorWriteToHiveList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Error processing partition file:")) {
                        errorProcessPartitionList.add((String) object.get().get("sObjectType"));
                    } else if (errorMsg.contains("Error fetching results")) {
                        errorFetchingResultsList.add((String) object.get().get("sObjectType"));

                    } else {
                        uncommonMap.put((String) object.get().get("sObjectType"), errorArray.toString());
                    }
                }
            });

            resultJson.setUncommon_Errors(uncommonMap);
            if (notVisList.toArray().length > 0) {
                resultJson.setNot_Visible(Arrays.toString(notVisList.toArray()));
            }
            if (varCharList.toArray().length > 0) {
                resultJson.setVarchar(Arrays.toString(varCharList.toArray()));
            }
            if (trinoCrashedList.toArray().length > 0) {
                resultJson.setTrino_Crashed(Arrays.toString(trinoCrashedList.toArray()));
            }
            if (partitionList.toArray().length > 0) {
                resultJson.setPartition(Arrays.toString(partitionList.toArray()));
            }
            if (numberFormatList.toArray().length > 0) {
                resultJson.setNumberFormatException(Arrays.toString(numberFormatList.toArray()));
            }
            if (addRowList.toArray().length > 0) {
                resultJson.setProblem_adding_row(Arrays.toString(addRowList.toArray()));
            }
            if (mismatchFieldType.toArray().length > 0) {
                resultJson.setMismatched_field_types(Arrays.toString(mismatchFieldType.toArray()));
            }
            if (errorExecQueryArray.toArray().length > 0) {
                resultJson.setErrorExecQuery(Arrays.toString(errorExecQueryArray.toArray()));
            }
            if (allOpsOtherThanList.toArray().length > 0) {
                resultJson.setAllOpsOther(Arrays.toString(allOpsOtherThanList.toArray()));
            }
            if (notCompleteList.toArray().length > 0) {
                resultJson.setNotCompleted(Arrays.toString(notCompleteList.toArray()));
            }
            if (splitBufferingList.toArray().length > 0) {
                resultJson.setSplitBuffering(Arrays.toString(splitBufferingList.toArray()));
            }
            if (errorProcessPartitionList.toArray().length > 0) {
                resultJson.setErrorProcessPartition(Arrays.toString(errorProcessPartitionList.toArray()));
            }
            if (errorWriteToHiveList.toArray().length > 0) {
                resultJson.setErrorWriteHive(Arrays.toString(errorWriteToHiveList.toArray()));
            }
            if (errorHiveSplitList.toArray().length > 0) {
                resultJson.setErrorHiveSplit(Arrays.toString(errorHiveSplitList.toArray()));
            }
            if (errorFetchingResultsList.toArray().length > 0) {
                resultJson.setErrorFetchingResults(Arrays.toString(errorFetchingResultsList.toArray()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        return result.toString();
    }

    public static String pullFromProvisionDB() throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "psql -t -p 2500 -h 'localhost' -U '96SJfyWbscdUJKBh' -d 'provision_db' " +
                "-c \"SELECT to_json(resource.*) FROM public.resource WHERE org_id LIKE LOWER('%"+ORGID+"%')\"");

        Process processCli = null;
        try {
            processCli = processBuilder.start();
            return returnResults(processCli);
        } catch (IOException e) {
            e.printStackTrace();
            return "No resource";
        } finally {
            processCli.destroy();
        }
    }

    private static String returnResults(Process process) throws IOException {
        JSONObject resource = new JSONObject();
        JSONParser parser = new JSONParser();

        try (InputStream inputStream = process.getInputStream()) {
            resource = (JSONObject) parser.parse(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        process.getInputStream().close();

        REGION = (String) resource.get("region");
        ORGID = (String) resource.get("org_id");
        IS_SANBOX = (Boolean) resource.get("is_sandbox");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resource);
    }

    private static void removeAllErrors() {
        resultJson = new ResultJson();
        customerName = "";
        partnerConnection = null;
    }

    private static void setAccessToken() throws  IOException, InterruptedException{
        Runtime rt = Runtime.getRuntime();
        JSONObject json = new JSONObject();
        JSONParser parser = new JSONParser();

        Process process = rt.exec("kubectl --context " + ARN + " -n ns-" + ORGID + " port-forward deployment/salesforce-token-provider 51000:8081");
        int pid = (int) process.pid();

        Thread.sleep(12000);
        URL url = new URL("http://localhost:51000/oauth/access-token");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            InputStream ip = con.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(ip));

            StringBuilder response = new StringBuilder();
            String responseSingle = null;
            while ((responseSingle = br1.readLine()) != null) {
                response.append(responseSingle);
            }

            json = (JSONObject) parser.parse(response.toString());

        } catch (Exception e) {
            rt.exec("kill -9 " + pid);
        } finally {
            rt.exec("kill -9 " + pid);
            con.disconnect();
        }

        try {
            ACCESSTOKEN = json.get("accessToken").toString();
        } catch (NullPointerException e) {
            resultJson.setBadToken(json);
        }
    }

    private static void checkBarVersion() throws IOException, ApiException {
        String command = "kubectl get pods " +
                "--namespace ns-"+ORGID+" " +
                "--context "+ARN+" " +
                "-o jsonpath=\"{.items[*].spec.containers[*].image}\" " +
                "--field-selector metadata.name=bar-api-0";
//        String command = "kubectl get pods --namespace ns-00d1i000002kcuaua4 --context arn:aws:eks:us-west-1:410275221728:cluster/phoenix-prod-us-west-1 -o jsonpath=\"{.items[*].spec.containers[*].image}\" --field-selector metadata.name=bar-api-0";

        Process runtime = Runtime.getRuntime().exec(command);
        BufferedReader output_reader = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
        String version = "";
        String output = "";
        while ((output = output_reader.readLine()) != null) {
            version = output.substring(output.indexOf("bar-api:") + 8).replaceAll("\"", "");

        }
        resultJson.setBarVersion(version);
    }

    @Test
    public static void getPostgressCreds() throws IOException, ApiException {
//        String command = "kubectl exec " +
//                "--namespace ns-"+ORGID+" " +
//                "--context "+ARN+" " +
//                "bar-api-0 -- env";
        String command = "kubectl exec --namespace ns-00d4w000008hppvuas --context arn:aws:eks:us-east-1:410275221728:cluster/phoenix-prod-us-east-1 bar-api-0 -- env";

        Process runtime = Runtime.getRuntime().exec(command);
        BufferedReader output_reader = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
        String version = "";
        String output = "";
        String password = "";
        String username = "";
        while ((output = output_reader.readLine()) != null) {

            password = output.substring(output.indexOf("DATABASE_PASSWORD:"));
            username = output.substring(output.indexOf("DATABASE_USERNAME:"));

        }
//        resultJson.setBarVersion(version);
    }

    public String printLog() {

        try {
            if (REGION.isBlank()) {
                pullFromProvisionDB();
                createArn();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {

                    String command = "kubectl logs " +
                            "--namespace ns-"+ORGID+" " +
                            "--context "+ARN+" " +
                            "bar-api-0";

                    try {
                        Process runtime = Runtime.getRuntime().exec(command);
//                        Streams.copy(runtime.getInputStream(), System.out);
                        return new String(runtime.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//                        System.out.println(barLogs);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }

//            }
//        });

//        thread.start();

    }

        // file path to your KubeConfig
//        String kubeConfigPath = "/Users/dan.green/.kube/config";
//
//        // loading the out-of-cluster config, a kubeconfig from file-system
//        ApiClient client =
//                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
//
//        // set the global default api-client to the in-cluster one from above
//        Configuration.setDefaultApiClient(client);
//
//        // the CoreV1Api loads default api-client from global configuration.
//        CoreV1Api api = new CoreV1Api();
//
//        // invokes the CoreV1Api client
//        V1PodList list = api.listNamespacedPod("ns-00d7g0000008myeeay",
//                null, null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null);
//        PodLogs logs = new PodLogs();
//        InputStream is = null;
//        System.out.println("Listing all pods: ");
//        for (V1Pod item : list.getItems()) {
//            System.out.println(item.getMetadata().getName());
//            if (item.getMetadata().getName().contains("bar-api-0")) {
//                is = logs.streamNamespacedPodLog(item);
//
//            }
//        }

//        try {
//            ApiClient client = Config.defaultClient();
//            Configuration.setDefaultApiClient(client);
//            V1Pod pod = Kubectl.get(V1Pod.class)
//                    .namespace("ns-00d7g0000008myeeay")
//
//                    .name("bar-api-0")
//                    .execute();
//            String test1 = pod.getApiVersion();
//            Map<String, String> labels = pod.getMetadata().getLabels();
//            String namespace = pod.getMetadata().getNamespace();
//
//            InputStream logStream = Kubectl.log()
//                    .namespace("ns-00d2f0000000o0deaq")
//                    .name("bar-api-0")
//                    .container("bar-api")
//                    .execute();
//
//            Streams.copy(logStream, System.out);
//            logStream.close();
//
//
//        } catch (KubectlException e) {
//            throw new RuntimeException(e);
//        }
//

//        try {
//            ApiClient client = Config.defaultClient();
//            Configuration.setDefaultApiClient(client);
//            CoreV1Api api = new CoreV1Api();
//            V1PodList list =
//                    api.listNamespacedPod("ns-"+ORGID,"", null,null,null,null,null,null,null,null , null);
//            for (V1Pod item : list.getItems()) {
//                System.out.println(item.getMetadata().getName());
//            }
//        } catch (IOException | ApiException e) {
//            throw new RuntimeException(e);
//        }


}