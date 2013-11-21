package Services;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class GDBSConnector {
	
	private static GraphDatabaseService instance;
	
	
	public static GraphDatabaseService openConnection(){
		if(instance == null){
//			instance = new RestGraphDatabase("http://localhost:7474/db/data");
			instance = new EmbeddedGraphDatabase("./target/db");
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					instance.shutdown();
				}
			});
		}
		return instance;
	}
	
	public static void setNull(){
		instance = null;
	}
}
