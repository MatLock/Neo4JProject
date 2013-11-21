package nClass;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import Services.GDBSConnector;

public class Photo {
	
	private int id_Photo;
	private String name;
	

	public int getId_Photo() {
		return id_Photo;
	}

	public void setId_Photo(int id_Photo) {
		this.id_Photo = id_Photo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public Photo(int id_Photo, String name) {
		super();
		this.id_Photo = id_Photo;
		this.name = name;
	}
	
	public Node save(){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Transaction tx = gdbs.beginTx();
		try{
			Node newNode = gdbs.createNode();
			newNode.setProperty("ID_PHOTO", this.getId_Photo());
			newNode.setProperty("NAME", this.getName());
			gdbs.index().forNodes("ID_PHOTO").putIfAbsent(newNode, "ID_PHOTO", this.getId_Photo());
			tx.success();
			return newNode;
		}finally{
			tx.finish();
		}
	}

	
	

}
