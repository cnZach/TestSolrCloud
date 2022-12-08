/**
 * Created by yxzhang on 21/07/2017.
 */

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestSolrCloudThreads
{
    public static void main(String[] args)
    {
        final String zkUrl = args[0];
        final String collection = args[1];
        final Integer threads= Integer.valueOf(args[2]);
        System.out.println("Starting to" + threads +" threads connect to solr: " + zkUrl + "/" + collection);

        for(int i=0; i<threads; i++) {
            new Thread("" + i) {
                public void run() {
                    System.out.println("Thread: " + getName() + " running");
                    try {
                        CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();
                        //CloudSolrClient cloud = new CloudSolrClient();
                        cloud.setDefaultCollection(collection);

                        SolrQuery solrQuery = new SolrQuery();
                        solrQuery.setRequestHandler("/query?");
                        solrQuery.set("q", new String[]{"*:*"});
                        solrQuery.set("wt", new String[]{"json"});

                        QueryResponse resp = cloud.query(collection, solrQuery);

                        System.out.println("response: " + resp);
                    } catch (SolrServerException | IOException e) {
                        e.printStackTrace();
                    }
                }}.start();
            }
    }
}
