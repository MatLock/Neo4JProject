package nClass;

import java.util.ArrayList;
import java.util.List;


import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

import Services.GDBSConnector;

public class User {
	
       List<Photo>photos;
       List<User>friends;
       private int U_ID;
       private String userName;
       
    public User(){
    	
    }
	public User(List<Photo> photos, List<User> friends,int id,String username) {
		super();
		this.userName = username;
		this.photos = photos;
		this.friends = friends;
	}
	
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}
    
//	public void saveUser(){
//		GraphDatabaseService gdbs = GDBSConnector.openConnection();
//		Transaction tx = gdbs.beginTx();
//		String u = "hola";
//		String h = "mundo";
//		Node node = gdbs.createNode();
//		node.setProperty("saludo", u);
//		Node secondNode = gdbs.createNode();
//		secondNode.setProperty("saludo", h);
//		Relationship relationship = node.createRelationshipTo(secondNode, RelationShip.RELATION);
//		relationship.setProperty("saludo", "pepito");
//		tx.success();
//		tx.finish();
//		
//	}
	
	public void logOut(){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		gdbs.shutdown();
		GDBSConnector.setNull();
	}
	
	public void saveUser(){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Transaction tx = gdbs.beginTx();
		Node nodeUser;
		try{
			nodeUser = gdbs.createNode();
			nodeUser.setProperty("USERNAME_KEY", this.getUserName());
			gdbs.index().forNodes("USERNAME_KEY").putIfAbsent(nodeUser, "USERNAME_KEY", this.getUserName());
			tx.success();
		}finally{
			tx.finish();
		}
		
	}
	
	public void savePhotos(List<Photo>photos){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Transaction tx = gdbs.beginTx();
		Index<Node> nodeIndex = gdbs.index().forNodes( "USERNAME_KEY" );
		try{
			Node user = nodeIndex.get("USERNAME_KEY",this.getUserName()).getSingle();
			for(Photo each : photos){
				Node newNode = each.save();
				user.createRelationshipTo(newNode, RelationShip.OWNER);
				tx.success();
			}
		}finally{
			tx.finish();
		}	
	}
	
	public void savePhoto(Photo photo){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Transaction tx = gdbs.beginTx();
		Index<Node> nodeIndex = gdbs.index().forNodes("USERNAME_KEY");
		try{
			Node node = nodeIndex.get("USERNAME_KEY",this.getUserName()).getSingle();
			Node p = photo.save();
			node.createRelationshipTo(p, RelationShip.OWNER);
			tx.success();
		}finally{
			tx.finish();
		}
	}
	
	public void addFriend(String username){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Transaction tx = gdbs.beginTx();
		Index<Node> nodeIndex = gdbs.index().forNodes("USERNAME_KEY");
		try{
			Node userNode = nodeIndex.get("USERNAME_KEY",username).getSingle();
			Node myNode = nodeIndex.get("USERNAME_KEY",this.getUserName()).getSingle();
			myNode.createRelationshipTo(userNode, RelationShip.KNOWS);
			tx.success();
		}finally{
			tx.finish();
		}
	}
	
	public ArrayList<Photo>askForPhotos(){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Index<Node> nodeIndex = gdbs.index().forNodes("USERNAME_KEY");
		Node user = nodeIndex.get("USERNAME_KEY", this.getUserName()).getSingle();
		Iterable<Relationship> x = user.getRelationships(RelationShip.OWNER, Direction.OUTGOING);
		ArrayList<Photo>photos= new ArrayList<Photo>();
		for(Relationship each : x){
			Node newNode = each.getEndNode();
		    Integer i = (Integer)newNode.getProperty("PHOTO_ID");
		    String name = (String)newNode.getProperty("NAME");
		    Photo photo = new Photo(i,name);
		    photos.add(photo);
		}
		return photos;		
	}
	
	public Iterable<Path> getFriends(){
		GraphDatabaseService gdbs = GDBSConnector.openConnection();
		Index<Node> nodeIndex = gdbs.index().forNodes("USERNAME_KEY");
		Node myNode = nodeIndex.get("USERNAME_KEY", this.getUserName()).getSingle();
		TraversalDescription td = Traversal.description();
		td.relationships(RelationShip.KNOWS,Direction.OUTGOING);
		td.evaluator(Evaluators.excludeStartPosition());
		td.evaluator(Evaluators.toDepth(3));
		return td.traverse(myNode);
	}
       public static void main(String[] args) {
    	   Photo p1 = new Photo(1, "diego");
    	   Photo p2 = new Photo(2, "lala");
    	   List<Photo> photos1 = new ArrayList<Photo>();
    	   photos1.add(p1);
    	   photos1.add(p2);
    	   User u2 = new User(null, null, 1, "fede");
    	   User u1= new User(null, null, 2, "fer");
    	   //u2.saveUser();
    	   //u1.saveUser();
    	  // u1.addFriend(u2.getUserName());
    	  // u1.savePhoto(p1);
    	   Iterable<Path> x = u1.getFriends();
    	   ArrayList<Node> nodes = new ArrayList<Node>();
    	   for(Path each : x){
    		  nodes.add(each.endNode());
    	   }
    	   System.out.println(nodes.get(0).getProperty("USERNAME_KEY"));
    	  
	}
}
