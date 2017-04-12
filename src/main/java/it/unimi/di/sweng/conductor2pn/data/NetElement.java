package it.unimi.di.sweng.conductor2pn.data;

public abstract class NetElement implements java.io.Serializable{
	/**
	 * Determines if a de-serialized file is compatible with this class.
	 *
	 * Maintainers must change this value if and only if the new version
	 * of this class is not compatible with old versions. See Sun docs
	 * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
	 * /serialization/spec/version.doc.html> details. </a>
	 *
	 * Not necessary to include in first version of the class, but
	 * included here as a reminder of its importance.
	 */
	private static final long serialVersionUID = -908315574907737564L;
	
	protected String id; //TODO: eliminare? per cosa viene usato?
	
	protected String name;
	public NetElement(){
		
	}
	public void setName(String n){
		this.name=n;
	}
	public String getName(){
		return this.name;
	}
	public void setId(String _id){
		this.id=_id;
	}
	public String getId(){
		return this.id;
	}
}
