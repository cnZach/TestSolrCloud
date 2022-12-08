//import org.apache.htrace.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
//import org.apache.solr.client.solrj.impl.Krb5HttpClientConfigurer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import java.io.IOException;
import java.net.MalformedURLException;

public class TestSolrCloudInsert
{
    public static void main(String[] args)
    {

        try
        {
            String zkUrl = args[0];
            String collection = args[1];
            String jaas_file = args[2];
            Integer totalRecs = Integer.valueOf(args[3]);
            Integer batchSize = Integer.valueOf(args[4]);
            //HttpClientUtil.setConfigurer(new Krb5HttpClientConfigurer());
            System.setProperty("java.security.auth.login.config", jaas_file);
            System.out.println("Starting to connect to zookeeper");
            CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();

            cloud.setDefaultCollection(collection);

            long start = System.nanoTime();
            Random ran = new Random();
            try {
                for (int i = 0; i < totalRecs; ++i) {
                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField("sku", "book-"+ ran.nextLong() + "-" + i);
                    doc.addField("id", "id-" + ran.nextLong() + "-" + i);
                    doc.addField("name", "The Legend of the Hobbit part " + i);
                    cloud.add(doc);
                    if (i % batchSize == 0) {
                        System.out.println(" Every " + batchSize + " records flush it");
                        cloud.commit(); // periodically flush
                    }
                }
                cloud.commit();
            }catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.nanoTime();
            long seconds = TimeUnit.NANOSECONDS.toSeconds(end - start);
            System.out.println(totalRecs +" records are indexed, took " + seconds + " seconds, batchSize in " + batchSize);
            System.exit(0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
