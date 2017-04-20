package it.unimi.di.sweng.conductor2pn.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.di.sweng.conductor2pn.core.ConductorToPn;

import java.util.ArrayList;


public class TBNet {

	String name;
	String constraint;
	private ArrayList<NetNode> nodes;
	private ArrayList<Arc> arcs;
	
	public TBNet(){
		this.nodes=new ArrayList<NetNode>();
		this.arcs=new ArrayList<Arc>();
	}
	public TBNet(String n){
		this.name=n;
		this.nodes=new ArrayList<NetNode>();
		this.arcs=new ArrayList<Arc>();
	}
	public TBNet(TBNet net){
		this.nodes=net.nodes;
		this.arcs=net.arcs;
	}
	
	public ArrayList<NetNode> getNodes(){
		return this.nodes;
	}
	public ArrayList<Arc> getArcs(){
		return this.arcs;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String n){
		this.name = n;
	}
	public void setConstraint(String c){
		this.constraint = c;
	}
	public String getConstraint(){
		return this.constraint;
	}
	public void addNode(NetNode p){
		this.nodes.add(p);
	}
	public void addArc(Arc a){
		this.arcs.add(a);
	}
	
	public ArrayList<Place> getPlaces(){
		ArrayList<Place> list=new ArrayList<Place>();
		Place p=new Place();
		for(NetNode n: this.nodes)
			if(n.getClass().isInstance(p))
				list.add((Place)n);
		return list;
	}
	
	public ArrayList<Place> getMarkedPlaces(){
		ArrayList<Place> list=new ArrayList<Place>();
		for(Place p: this.getPlaces())
			if(!(p.getTokens().isEmpty()))
				list.add(p);
		return list;
	}
	
	public ArrayList<Transition> getTransitions(){
		ArrayList<Transition> list=new ArrayList<Transition>();
		Transition t=new Transition();
		for(NetNode n: this.nodes)
			if(n.getClass().isInstance(t))
				list.add((Transition)n);
		return list;
	}
	
	public ArrayList<Transition> getStrongTransitions(){
		ArrayList<Transition> list=new ArrayList<Transition>();
		for(Transition t: this.getTransitions())
			if(!t.isWeak())
				list.add(t);
		return list;
	}
	
	public ArrayList<Transition> getWeakTransitions(){
		ArrayList<Transition> list=new ArrayList<Transition>();
		for(Transition t: this.getTransitions())
			if(t.isWeak())
				list.add(t);
		return list;
	}
	
	public ArrayList<NetNode> getPreset(NetNode n){
		ArrayList<NetNode> list=new ArrayList<NetNode>();
		for(Arc a: this.getArcs())
			if(n.equals(a.getTarget()))
				list.add(a.getSource());
		return list;
	}
	
	public ArrayList<NetNode> getPostset(NetNode n){
		ArrayList<NetNode> list=new ArrayList<NetNode>();
		for(Arc a: this.getArcs())
			if(n.equals(a.getSource()))
				list.add(a.getTarget());
		return list;
	}
	
	public ArrayList<Transition> getPotentialStrongTransition(){
		ArrayList<Transition> list=new ArrayList<Transition>();
		ArrayList<Place> markedPlaces=this.getMarkedPlaces();
		for(Transition t: this.getStrongTransitions())
			if(markedPlaces.containsAll(this.getPreset(t)))
				list.add(t);
		return list;
	}
	
	public ArrayList<Transition> getPotentialWeakTransition(){
		ArrayList<Transition> list=new ArrayList<Transition>();
		ArrayList<Place> markedPlaces=this.getMarkedPlaces();
		for(Transition t: this.getWeakTransitions())
			if(markedPlaces.containsAll(this.getPreset(t)))
				list.add(t);
		return list;
	}
	
	public String toString(){
		String r="TB Net: "+this.name+"\nConstraint: "+this.constraint+"\n";
		for(NetNode n: this.nodes)
			r+=n.toString()+"\n";
		for(Arc a: this.arcs)
			r+=a.toString()+"\n";
		return r;
	}

	public void createWorkflow(JsonElement workflowElement) {
	}

	public Place getPlace(String name) {
		for(Place p: getPlaces())
			if(p.getName().equals(name))
				return p;
		return null;
	}

    public Transition getTransition(String name) {
        for(Transition t: getTransitions())
            if(t.getName().equals(name))
                return t;
        return null;
    }

}
