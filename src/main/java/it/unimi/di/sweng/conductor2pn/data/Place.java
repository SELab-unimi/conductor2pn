package it.unimi.di.sweng.conductor2pn.data;

import java.util.ArrayList;

public class Place extends NetNode{
	
	private static final long serialVersionUID = -6792032704318395762L;
	
	private ArrayList<Token> tokens;
	
	public Place(){
		this.tokens=new ArrayList<Token>();
	}
	public Place(String name){
		super(name);
		this.tokens=new ArrayList<Token>();
	}
	public Place(Place pl){
		super(pl.name);
		this.tokens=new ArrayList<Token>();
		this.tokens.addAll(pl.tokens);
	}
	public ArrayList<Token> getTokens(){
		return this.tokens;
	}
	public void putToken(Token t){
		this.tokens.add(t);
	}
	public void removeToken(Token t){
		this.tokens.remove(t);
	}
	public void removeAllTokens(){
		this.tokens.clear();
	}

	public void putTokens(int n, String symbolicTimestamp) {
		for(int i=0; i<n; i++)
		    putToken(new Token(symbolicTimestamp));
	}

	public boolean equals(Object obj){
		if(!(obj.getClass().isInstance(this)))
			return false;
		else
			return this.name.equals(((Place)obj).name);
	}
	
	public String toString(){
		String r="Place "+this.getName()+": Tokens{";
		for(Token t: this.tokens)
			r+=t.toString()+", ";
		return r+"}";
	}
}
