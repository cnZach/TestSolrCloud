import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class TestSolrCloudUpdateRequest
{
    public static void main(String[] args)
    {
        System.out.println("Connecting to zookeeper");
        try
        {   //String zkUrl = "c4330-node4.coelab.cloudera.com:2181/solr";
            String zkUrl = "10.17.101.120:2181/solr";
            String collection = "test_collection";
            if (args.length > 1) {
                zkUrl = args[0].toString().trim();
                collection = args[1].toString().trim();
            }
            String id = "dp_7087";
            System.out.println("dp_7087 hash code is : " + id.hashCode());
            //System.exit(0);
            System.out.println(" zk: " + zkUrl +  "  collection: " + collection);
            CloudSolrClient cloud = new CloudSolrClient.Builder().withZkHost(zkUrl).build();
            cloud.setDefaultCollection(collection);

            UpdateRequest req = new UpdateRequest();

            //for (int i = 0 ; i < 99 ; i++) {
                int i= 100;
                SolrInputDocument solrDoc = new SolrInputDocument();
                solrDoc.addField("id", "dk_7087");
                Timestamp ts=new Timestamp(System.currentTimeMillis());
                Date date=new Date(ts.getTime());
                solrDoc.addField("name", "change.me : " + date);

                cloud.add(solrDoc);
                cloud.commit();
                Thread.currentThread().sleep(2000);

                SolrQuery solrQuery = new SolrQuery();
                solrQuery.setRequestHandler("/query");
                solrQuery.set("q", new String[]{"id:dk_7087"});
                solrQuery.set("wt", new String[]{"json"});

                QueryResponse resp = cloud.query(solrQuery);
                System.out.println("Before Update: " + resp.getResponse().get("response").toString());

                SolrInputDocument solrDocForUpdate = new SolrInputDocument();
                solrDocForUpdate.addField("id", "dk_7087");
                Thread.currentThread().sleep(2000);
                ts=new Timestamp(System.currentTimeMillis());
                solrDocForUpdate.addField("name", "dk_7087 " + new Date(ts.getTime()));
                req.add(solrDocForUpdate);
                req.process(cloud, collection);

                resp = cloud.query(solrQuery);
                System.out.println("After process: " + resp.getResponse().get("response").toString());

                Thread.currentThread().sleep(3000);
                req.commit(cloud, collection);
                resp = cloud.query(solrQuery);
                System.out.println("After commit: " + resp.getResponse().get("response").toString());

                System.out.println("Processing 10W docs...: "+ (new Date(new Timestamp(System.currentTimeMillis()).getTime())));
                List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
                for (int j = 0 ; j < 10000 ; j++) {
                    SolrInputDocument solrDocForInsert = new SolrInputDocument();
                    solrDocForInsert.addField("id", "dk_7087_" + j );
                    ts=new Timestamp(System.currentTimeMillis());
                    solrDocForInsert.addField("name", "dk_7087 " + new Date(ts.getTime()));
                    docs.add(solrDocForInsert);
                    //req.add(solrDocForInsert);
                    if ( docs.size() >= 1000) {
                        req.add(docs);
                        UpdateResponse uResp = req.process(cloud);
                        docs.clear();
                        String elapsTime;
                        String requestUrl;
                        String responseStr;
                        if (null == uResp)  {
                            elapsTime= "null";
                            requestUrl= "null";
                            responseStr = "null";
                        }
                        else {
                            elapsTime = String.valueOf(uResp.getElapsedTime());
                            requestUrl = uResp.getRequestUrl() == null? "null" : uResp.getRequestUrl().toString();
                            responseStr = uResp.getResponse() == null? "null" : uResp.getResponse().toString();
                        }
                        System.out.println("Processed 1K docs : " + (new Date(new Timestamp(System.currentTimeMillis()).getTime())) +
                                " ElapseTime: " + elapsTime + " uRespRequestURL: " + requestUrl +
                                " uResp: " + responseStr
                        );
                    }
                }

                System.out.println("After Processing 10W docs. : " + (new Date(new Timestamp(System.currentTimeMillis()).getTime())));
                //cloud.deleteById("dk_7087");
                //cloud.commit();
                //Thread.currentThread().sleep(1000);
            //}
            cloud.close();
            System.exit(0);
        }
        catch (MalformedURLException|SolrServerException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
}