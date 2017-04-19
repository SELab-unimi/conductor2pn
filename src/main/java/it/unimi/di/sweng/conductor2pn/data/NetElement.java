package it.unimi.di.sweng.conductor2pn.data;

public abstract class NetElement implements java.io.Serializable{

	private static final long serialVersionUID = -908315574907737564L;
	
	protected String name; // uid
	public NetElement(){
		
	}
	public void setName(String n){
		this.name = n;
	}
	public String getName(){
		return this.name;
	}
}
