package it.unimi.di.sweng.conductor2pn.data;


public class Token extends NetElement{
	
	private static final long serialVersionUID = -5458220992365236256L;
	
	protected String symTime;
	
	public Token(){
		this.symTime="T0";
	}
	public Token(String st){
		this.symTime=st;
	}
	public Token(Token t){
		this.symTime=t.symTime;
	}
	
	public String getSymbolicTime(){
		return this.symTime;
	}
	public void putSymbolicTime(String st){
		this.symTime=st;
	}
	
	public boolean equals(Object obj){
		if(!(obj.getClass().isInstance(this)))
			return false;
		else
			return this.symTime.equals(((Token)obj).symTime);
	}
	
	public String toString(){
		return this.symTime;
	}
}
