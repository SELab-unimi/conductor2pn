package it.unimi.di.sweng.conductor2pn.data;


public class Arc extends NetElement{
	
	private static final long serialVersionUID = -6776257318567436680L;
	
	private NetNode source;
	private NetNode target;
	private int weight=1; //??
	
	public Arc(NetNode src, NetNode tgt){
		this.source=src;
		this.target=tgt;
	}
	/*public Arc(Place src, Transition tgt){
		this.source=src;
		this.target=tgt;
	}
	public Arc(Transition src, Place tgt){
		this.source=src;
		this.target=tgt;
	}*/
	public void setWeight(int w){
		this.weight=w;
	}
	
	public NetNode getSource(){
		return this.source;
	}
	public NetNode getTarget(){
		return this.target;
	}
	public int getWeight(){
		return this.weight;
	}
	
	public String toString(){
		return this.source.getName()+" -> "+this.target.getName();
	}
}
