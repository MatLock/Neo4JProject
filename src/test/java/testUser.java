import static org.junit.Assert.*;

import java.util.ArrayList;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Traverser;

import Services.GDBSConnector;

import nClass.Photo;
import nClass.RelationShip;
import nClass.User;



public class testUser{
	
	ArrayList<Photo> photos = new ArrayList<Photo>();
	ArrayList<Photo> photos1 = new ArrayList<Photo>();
	ArrayList<Photo> photos2 = new ArrayList<Photo>();
	ArrayList<User> friends = new ArrayList<User>();
	ArrayList<User> friends1 = new ArrayList<User>();
	ArrayList<User> friends2 = new ArrayList<User>();
	User u,u1,u2;
    Photo p,p1,p2;
    GraphDatabaseService gdbs = GDBSConnector.openConnection();
    Index<Node> nodeIndex = gdbs.index().forNodes("USERNAME_KEY");
	Index<Node> index = gdbs.index().forNodes("ID_PHOTO");
	
	@Before
	public void setUp(){
		u = new User(photos,friends,0,"Pepe");
		u1 = new User(photos1,friends1,1,"Pepito");
		u2 = new User(photos2,friends2,2,"Pepita");
		p = new Photo(0,"EsDePepe");
		p1 = new Photo(1,"EsDePepito");
		p2 = new Photo(2,"EsDePepita");
		u.saveUser();
		u.savePhoto(p);
		u1.saveUser();
		u1.savePhoto(p1);
		u2.saveUser();
		u2.savePhoto(p2);
		u.addFriend("Pepito");
		u1.addFriend("Pepita");
	}
	
	@Test
	public void testSaveUser(){
		Node userNode = nodeIndex.get("USERNAME_KEY", "Pepe").getSingle();
		assertEquals(userNode.getProperty("USERNAME_KEY"),u.getUserName());
	}
	
	@Test
	public void testGetFriends(){
		Iterable<Path> result = u.getFriends();
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node nodePerson = nodeIndex.get("USERNAME_KEY", "Pepita").getSingle();
		for(Path p : result){
			nodes.add(p.endNode());
		}
		assertTrue(nodes.contains(nodePerson));
	}
	
	@Test
	public void testSavePhoto(){
		Node userNode = nodeIndex.get("USERNAME_KEY", "Pepe").getSingle();
		Relationship relation= userNode.getSingleRelationship(RelationShip.OWNER, Direction.OUTGOING);
		Node nodePhoto = relation.getEndNode();
		assertEquals(nodePhoto.getProperty("NAME"),"EsDePepe");
		assertEquals(nodePhoto.getProperty("ID_PHOTO"),0);
		
	}
	
	@After
	public void tearDown(){
		Transaction tx = gdbs.beginTx();
		Node node = nodeIndex.get("USERNAME_KEY", "Pepe").getSingle();
		Node node1 = nodeIndex.get("USERNAME_KEY", "Pepito").getSingle();
		Node node2 = nodeIndex.get("USERNAME_KEY", "Pepita").getSingle();
		Node photo = index.get("ID_PHOTO", 0).getSingle();
		Node photo1 = index.get("ID_PHOTO", 1).getSingle();
		Node photo2 = index.get("ID_PHOTO", 2).getSingle();
		Iterable<Relationship> relation = node.getRelationships();
	    for (Relationship r : relation){
	    	r.delete();
	    }
	    Iterable<Relationship> relationp = photo.getRelationships();
	    for (Relationship r : relationp){
	    	r.delete();
	    }
	    Iterable<Relationship> relation1 = node1.getRelationships();
	    for (Relationship r : relation1){
	    	r.delete();
	    }
	    Iterable<Relationship> relationp1 = photo1.getRelationships();
	    for (Relationship r : relationp1){
	    	r.delete();
	    }
	    Iterable<Relationship> relation2 = node2.getRelationships();
	    for (Relationship r : relation2){
	    	r.delete();
	    }
	    Iterable<Relationship> relationp2 = photo2.getRelationships();
	    for (Relationship r : relationp2){
	    	r.delete();
	    }
	    node.delete();
	    node1.delete();
	    node2.delete();
	    photo.delete();
	    photo1.delete();
	    photo2.delete();
	    tx.success();
	    tx.finish();
	}
}
