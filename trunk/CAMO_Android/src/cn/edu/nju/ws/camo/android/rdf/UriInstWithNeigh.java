package cn.edu.nju.ws.camo.android.rdf;

import java.util.ArrayList;

/**
 * @author Hang Zhang
 *
 */
public class UriInstWithNeigh extends UriInstance {

	private ArrayList<Triple> triplesDown = new ArrayList<Triple>();
	private ArrayList<Triple> triplesUp = new ArrayList<Triple>();
	
	UriInstWithNeigh(UriInstance inst) {
		super(inst.getUri(), inst.getMediaType());
		this.setClassType(inst.getClassType());
		this.setName(inst.getName());
	}
	
	UriInstWithNeigh(UriInstance inst, ArrayList<Triple> infosDown, ArrayList<Triple> infosUp) {
		this(inst);
		this.triplesDown = infosDown;
		this.triplesUp = infosUp;
	}
	
	public void addTripleDown(Triple triple) {
		triplesDown.add(triple);
	}
	
	public void addTripleUp(Triple triple) {
		triplesUp.add(triple);
	}
	
	public ArrayList<Triple> getTriplesDown() {
		return this.triplesDown;
	}
	
	public ArrayList<Triple> getTriplesUp() {
		return this.triplesUp;
	}
	
	public boolean hasTriplesDown() {
		if(triplesDown.size() == 0)
			return false;
		return true;
	}
	
	public boolean hasTriplesUp() {
		if(triplesUp.size() == 0) 
			return false;
		return true;
	}
}
