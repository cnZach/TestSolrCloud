/**
 * Created by yxzhang on 21/07/2017.
 */
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class TestSolrCloud
{
    public static void main(String[] args)
    {
        System.out.println("Starting to connect to zookeeper");
        try
        {   String zkUrl = args[0];
            String collection = args[1];
            CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();
            cloud.setDefaultCollection(collection);

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setRequestHandler("/query?");
            solrQuery.set("q", new String[] { "*:*" });
            solrQuery.set("wt", new String[] { "json" });

            QueryResponse resp = cloud.query(solrQuery);

            System.out.println(resp);
        }
        catch (IOException|SolrServerException e)
        {
            e.printStackTrace();
        }
    }
}
