import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.impl.HttpClientUtil;

public class TestSolrCloud2
{
    public static void main(String[] args)
    {

        try
        {
            String zkUrl = args[0];
            String collection = args[1];
            String jaas_file = args[2];
            System.setProperty("java.security.auth.login.config", jaas_file);
            System.out.println("Starting to connect to zookeeper");
            // set HttpClientConfigurer
            //HttpClientUtil.setConfigurer(new Krb5HttpClientConfigurer());
            CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();

            cloud.setDefaultCollection(collection);

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setRequestHandler("/query?");
            solrQuery.set("q", new String[] { "*:*" });
            solrQuery.set("wt", new String[] { "json" });

            QueryResponse resp = cloud.query(collection, solrQuery);

            System.out.println(resp);
        }
        catch (IOException|SolrServerException e)
        {
            e.printStackTrace();
        }
    }
}
