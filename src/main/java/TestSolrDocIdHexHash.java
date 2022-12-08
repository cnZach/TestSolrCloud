/**
 * Created by yxzhang on 02/09/2020.
 */


import org.apache.solr.common.util.Hash;

public class TestSolrDocIdHexHash {
    public static void main(String[] args) {
        try {
            String id = "dk_7087";
            int solrHash = Hash.murmurhash3_x86_32(id, 0, id.length(), 0);
            System.out.println(id + " solr doc id hash hex value is : " + Integer.toHexString(solrHash));
            System.exit(0);

        }catch (Exception e){

        }
    }
}