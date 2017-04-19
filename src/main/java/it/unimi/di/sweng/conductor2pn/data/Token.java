package it.unimi.di.sweng.conductor2pn.data;


public class Token extends NetElement{
	
	private static final long serialVersionUID = -5458220992365236256L;
	
	protected String symbolicTimestamp;
	
	public Token(){
		this.symbolicTimestamp = "T0";
	}
	public Token(String st){
		this.symbolicTimestamp = st;
	}
	public Token(Token t){
		this.symbolicTimestamp = t.symbolicTimestamp;
	}
	
	public String getSymbolicTime(){
		return this.symbolicTimestamp;
	}
	public void putSymbolicTime(String st){
		this.symbolicTimestamp = st;
	}
	
	public boolean equals(Object obj){
		if(!(obj.getClass().isInstance(this)))
			return false;
		else
			return this.symbolicTimestamp.equals(((Token)obj).symbolicTimestamp);
	}
	
	public String toString(){
		return this.symbolicTimestamp;
	}
}
