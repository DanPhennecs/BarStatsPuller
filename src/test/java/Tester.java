import com.sforce.async.*;
import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.*;
import org.apache.commons.compress.utils.*;
import org.junit.*;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;


public class Tester {
    static PartnerConnection partnerConnection = null;
    static BulkConnection bulkConnection = null;
    private static BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));
    static DecimalFormat formatter = new DecimalFormat("#,###");

    public static void main(String[] args) {
        Tester tester = new Tester();
        List<String> listOfTables = new ArrayList<>();
//        List<String> listOfTables = Stream.of("permissionsetassignment","permissionset").collect(Collectors.toList());
        if (tester.login()) {
            try {

                DescribeGlobalResult test = partnerConnection.describeGlobal();
                DescribeGlobalSObjectResult[] sobjects = test.getSobjects();
                for(DescribeGlobalSObjectResult sobject:sobjects) {
                    String table = sobject.getName();
                    listOfTables.add(table);
//                    if (table.toLowerCase().contains("permissionsetassignment")) {
                    if (sobject.isQueryable()) {
//                            && !table.contains("ContentDocumentLink") && !table.contains("FeedComment")
//                            && !table.contains("FeedItem") && !table.contains("KnowledgeArticleVersion")
//                            && !table.contains("Knowledge__kav") && !table.contains("UserProfileFeed")
//                            && !table.contains("Vote") && !table.contains("LinkedBusinessUnit")) {
                        DescribeSObjectResult describeSobject = partnerConnection.describeSObject(table);
                        int numberOfFields = describeSobject.getFields().length;
//                        if (numberOfFields > 100) {
//                            System.out.println("Table:" + table +
//                                    " NumberOfFields:" + formatter.format(numberOfFields));
//                        }
                        int numberOfRecords = 0;
//                        if (numberOfFields > 100) {
                        try {
                            QueryResult queryResult = partnerConnection.query("SELECT COUNT() FROM "+table);
                            numberOfRecords = queryResult.getSize();
                            if (numberOfRecords > 100000) //Only print out if records larger than 100k
                                System.out.println("Table:"+table+" NumberOfFields:"+formatter.format(numberOfFields)+" NumberOfRecords:"+formatter.format(numberOfRecords));
                        } catch (Exception e) {
                            System.out.println("---Failed Table:"+ table);
                        }
                    }

                }

            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
            int numberOfTables = listOfTables.size();

            int chunkSize = 300;
            AtomicInteger counter = new AtomicInteger();
            final Collection<List<String>> partitionedList =
                    listOfTables.stream().collect(Collectors.groupingBy(i -> counter.getAndIncrement() / chunkSize))
                            .values();
            for(List<String> subList : partitionedList) {
                System.out.println(subList);
            }
//            System.out.println(listOfTables.stream().
//                    map(Object::toString).
//                    collect(Collectors.joining(",")).toString());
        }
    }

    private boolean login() {
        boolean success = false;
//        String username = getUserInput("Enter username: ");
        String password = "**********";
        String apiVersion = "52.0";
        Boolean isSandbox = false;
        String url = "https://slfslfd.my.salesforce.com/services/Soap/u/{version}";
        String authEndPoint = "https://" + (isSandbox ? "test" : "login") + ".salesforce.com/services/Soap/u/" + apiVersion;

        File file = new File("/Users/dan.green/Documents/Dev/BarStatsPuller/traceLogs.txt");

        try {
            boolean result = Files.deleteIfExists(file.toPath());
            ConnectorConfig config = new ConnectorConfig();
//            config.setUsername(username);
            config.setSessionId(password);
            config.setServiceEndpoint(url);

            config.setAuthEndpoint(authEndPoint);
            config.setTraceFile("traceLogs.txt");
            config.setTraceMessage(true);
            config.setPrettyPrintXml(true);

            partnerConnection = new PartnerConnection(config);
//            bulkConnection = new BulkConnection(config);

//            System.out.println(bulkConnection.getRestEndpoint());

            success = true;
        } catch (ConnectionException ce) {
            ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
//        } catch (AsyncApiException e) {
//            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return success;
    }

    @Test
    public void test()
    {

        System.out.println("The stream after applying "
                + "the function is : ");

        // Creating a list of Integers
        List<String> list = Arrays.asList("geeks", "gfg", "g",
                "e", "e", "k", "s");

        // Using Stream map(Function mapper) to
        // convert the Strings in stream to
        // UpperCase form
        List<String> answer = list.stream().map(String::toUpperCase).
                collect(Collectors.toList());

        // displaying the new stream of UpperCase Strings
        System.out.println(answer);
    }
}
