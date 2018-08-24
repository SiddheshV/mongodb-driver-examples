package DatabaseThroughputTest;
import java.net.UnknownHostException;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;


public class CollectionThroughputTests {
    
    public static void main(String[] args) throws UnknownHostException{
                
        // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
        String mongoUri = System.getenv("MONGODB_URI");
        if (mongoUri == null) {
          System.out.println("Expected MONGODB_URI env parameter to be set");
		  System.exit(1);
        }
        
        MongoClientURI uri = new MongoClientURI(mongoUri);
        MongoClient client = new MongoClient(uri);
		
		/*
		  * Create a shared throughput database 
		  */
		MongoDatabase database = client.getDatabase("shardThroughputDatabase");
		System.out.println("Created a new sharedThroughput database  ");
		database.runCommand(new Document("customAction", "CreateDatabase").append("offerThroughput", 50000));		
        
        /*
         * Create 2 collections under the sharded database using shardCollection command
		 * https://docs.mongodb.com/manual/reference/command/shardCollection/
         */
        System.out.println("Created a new collection: sampleCollection1 under database: shardThroughputDatabase ");
		database.runCommand(new Document("shardCollection", "shardThroughputDatabase.sampleCollection1").append("key", new Document("_id", "hashed")));
        MongoCollection collection1 = database.getCollection("sampleCollection1");
		collection1.insertOne(new Document("_id", 1).append("a", 1));
		
		System.out.println("Created a new collection: sampleCollection2 under database: shardThroughputDatabase with offerThroughput: 8000");
		database.runCommand(new Document("customAction", "CreateCollection").append("collection", "sampleCollection2").append("offerThroughput", 8000).append("shardKey", "_id"));
		MongoCollection collection2 = database.getCollection("sampleCollection2");		
		collection2.insertOne(new Document("_id", 1).append("b", 1));
		
		/*
         * Increase the provisioned throughput on the collection from 5k RUs to 10k RUs
         */
		System.out.println("Updating the throughput of sampleCollection2 from 8k to 10k RUs");
		database.runCommand(new Document("customAction", "UpdateCollection").append("collection", "sampleCollection2").append("offerThroughput", 10000));
        
		/*
         * Since this is an example, we'll clean up after ourselves.
         */
        //database.drop();
        
		/*
         * Only close the connection when your app is terminating
         */
        client.close();
    }
}
