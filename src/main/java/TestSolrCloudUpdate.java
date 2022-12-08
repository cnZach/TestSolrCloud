import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.Date;

public class TestSolrCloudUpdate
{
    public static void main(String[] args)
    {
        System.out.println("Starting to connect to zookeeper");
        try
        {   String zkUrl = "c3330-node2:2181/solr";
            String collection = "test_collection";
            String id = "dp_7087";
            System.out.println("dp_7087 hash code is : " + id.hashCode());
            System.exit(0);
            System.out.println(" zk: " + zkUrl +  "  collection:" + collection);
            CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();
            cloud.setDefaultCollection(collection);

            for (int i = 0 ; i < 99 ; i++) {
                SolrInputDocument solrDoc = new SolrInputDocument();
                solrDoc.addField("id", "dk_7087");
                Timestamp ts=new Timestamp(System.currentTimeMillis());
                Date date=new Date(ts.getTime());
                solrDoc.addField("name", "change.me : " + date);

                cloud.add(solrDoc);
                cloud.commit();
                Thread.currentThread().sleep(1000);

                SolrQuery solrQuery = new SolrQuery();
                solrQuery.setRequestHandler("/query");
                solrQuery.set("q", new String[]{"id:dk_7087"});
                solrQuery.set("wt", new String[]{"json"});

                QueryResponse resp = cloud.query(solrQuery);
                System.out.println(i + " : Before Atomic Update: " + resp.getResponse().get("response").toString());

                SolrInputDocument solrDocForUpdate = new SolrInputDocument();
                solrDocForUpdate.addField("id", "dk_7087");
                HashMap<String, Object> fieldModifier = new HashMap<>(1);
                fieldModifier.put("set", 10000 + i);
                solrDocForUpdate.addField("cluster_number_i", fieldModifier);
                solrDocForUpdate.addField("cluster_number_i", fieldModifier);

                cloud.add(solrDocForUpdate);
                cloud.commit();
                Thread.currentThread().sleep(1000);

                resp = cloud.query(solrQuery);
                System.out.println(i + " : After Atomic Update: " + resp.getResponse().get("response").toString());

                //cloud.deleteById("dk_7087");
                //cloud.commit();
                //Thread.currentThread().sleep(1000);
            }
            System.exit(0);
        }
        catch (MalformedURLException|SolrServerException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}