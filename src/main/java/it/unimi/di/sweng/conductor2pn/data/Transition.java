package it.unimi.di.sweng.conductor2pn.data;


public class Transition extends NetNode {
	
	private static final long serialVersionUID = 8268411914590072821L;
	public static final String ENAB = "enab";
	public static final String INF = "enab + " + Integer.MAX_VALUE;

	private String minTime = ENAB;
	private String maxTime = ENAB;
	private boolean weak = false;
	
	public Transition(){

	}

	public Transition(String name){
		super(name);
	}

	public Transition(String name, String tmin, String tmax){
		super(name);
		this.minTime =tmin;
		this.maxTime =tmax;
	}

	public Transition(String name, String tmin, String tmax, boolean wk){
		super(name);
		this.minTime = tmin;
		this.maxTime = tmax;
		this.weak = wk;
	}
	
	public String getMinTime(){
		return this.minTime;
	}
	public String getMaxTime(){
		return this.maxTime;
	}
	public void putMinTime(String tmin){
		this.minTime = tmin;
	}
	public void putMaxTime(String tmax){
		this.maxTime = tmax;
	}
	public boolean isWeak(){
		return this.weak;
	}
	public void setWeak(boolean wk){
		this.weak = wk;
	}

	public boolean equals(Object o){
		if(!(o.getClass().isInstance(this)))
			return false;
		else{
			Transition t=(Transition)o;
			return this.name.equals(t.name) && this.weak==t.weak && this.minTime.equals(t.minTime) &&
				this.maxTime.equals(t.maxTime);
		}
	}

	public String toString(){
		return "Transition " + this.getName() + ": tmin="+this.minTime +
			", tmax=" + this.maxTime + ", semantic=" + getSemanticsAsString();
	}

	public String getSemanticsAsString() {
		return weak ? "WEAK" : "STRONG";
	}
	
}
