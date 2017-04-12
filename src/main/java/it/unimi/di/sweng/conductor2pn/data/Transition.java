package it.unimi.di.sweng.conductor2pn.data;


public class Transition extends NetNode {
	
	private static final long serialVersionUID = 8268411914590072821L;
	
	private String staticMinTime="enab";
	private String staticMaxTime="enab";
	private boolean weak=false;
	
	public Transition(){
		
	}
	public Transition(String name){
		super(name);
	}
	public Transition(String name, String tmin, String tmax){
		super(name);
		this.staticMinTime=tmin;
		this.staticMaxTime=tmax;
	}
	public Transition(String name, String tmin, String tmax, boolean wk){
		super(name);
		this.staticMinTime=tmin;
		this.staticMaxTime=tmax;
		this.weak=wk;
	}
	
	public String getMinTime(){
		return this.staticMinTime;
	}
	public String getMaxTime(){
		return this.staticMaxTime;
	}
	public void putMinTime(String tmin){
		this.staticMinTime=tmin;
	}
	public void putMaxTime(String tmax){
		this.staticMaxTime=tmax;
	}
	public boolean isWeak(){
		return this.weak;
	}
	public void setWeak(boolean wk){
		this.weak=wk;
	}
	public boolean equals(Object o){
		if(!(o.getClass().isInstance(this)))
			return false;
		else{
			Transition t=(Transition)o;
			return this.name.equals(t.name) && this.weak==t.weak && this.staticMinTime.equals(t.staticMinTime) &&
				this.staticMaxTime.equals(t.staticMaxTime);
		}
	}
	public String toString(){
		return "Transition "+this.getName()+": tmin="+this.staticMinTime
			+", tmax="+this.staticMaxTime+", semantic="+(this.weak ? "WEAK" : "STRONG");
	}
	
}
