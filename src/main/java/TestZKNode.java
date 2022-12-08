import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class TestZKNode {
    private static ZooKeeper zk;
    private static ZookeeperConnection conn;

    // Method to check existence of znode and its status, if znode is available.
    public static Stat znode_exists(String path) throws
            KeeperException,InterruptedException {
        return zk.exists(path, true);
    }

    public static void main(String[] args) throws InterruptedException,KeeperException {
        String path = "/MyFirstZnode"; // Assign znode to the specified path
        String host = "localhost";
        if (args.length < 1) {
            host = "10.17.101.240";
            path = "/solr";
        } else {
            host = args[0];
            path = args[1];
        }

        try {
            conn = new ZookeeperConnection();
                zk = conn.connect(host);
                Stat stat = znode_exists(path); // Stat checks the path of the znode

                if (stat != null) {
                    System.out.println("Node " + path + " exists and the node version is " +
                            stat.getVersion());
                } else {
                    System.out.println("Node does not exists");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage()); // Catches error messages
        }

    }
}