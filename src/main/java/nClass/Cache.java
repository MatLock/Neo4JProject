package nClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import Services.GDBSConnector;





public class Cache {

	Node n;

	
	public Node getN() {
		return n;
	}

	public void setN(Node n) {
		this.n = n;
	}

	public void saveMejoresPromedios() {
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		String query = "MATCH (n) RETURN n ORDER BY n.porcentaje LIMIT 10";

		ExecutionEngine e = new ExecutionEngine(gdbs);
		Transaction tx = gdbs.beginTx();

		try {
			ExecutionResult result = e.execute(query);
			@SuppressWarnings("unchecked")
			Iterator<Node> i = (Iterator<Node>) result.columnAs("userName");

			Node nodeCache = gdbs.createNode();
			gdbs.index().forNodes("USER_NODE")
					.putIfAbsent(nodeCache, "USER_NODE", n);
			Integer x = 0;
			for (Node each : IteratorUtil.asIterable(i)) {

				nodeCache.setProperty("USER_NODE" + x.toString(), each);
				x++;
			}
			tx.success();
		} finally {
			tx.finish();
		}

	}

	public static void main(String[] args) {
		
	  	
 	   Photo p1 = new Photo(1, "diego");
 	   Photo p2 = new Photo(2, "lala");
 	   List<Photo> photos1 = new ArrayList<Photo>();
 	   photos1.add(p1);
 	   photos1.add(p2);
 	   User u2 = new User(null, null, 1, "fede");
 	   User u1= new User(null, null, 2, "fer");
 	   u1.addFriend(u2.getUserName());
		
 	   
 	  
		
		Cache c = new Cache();
		c. saveMejoresPromedios();

	}
}

