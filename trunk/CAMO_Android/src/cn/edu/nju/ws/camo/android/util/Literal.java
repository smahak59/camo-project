package cn.edu.nju.ws.camo.android.util;

public class Literal extends Resource {

	private String name;
	
	Literal(String name, String mediaType) {
		super(mediaType);
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return (name+getMediaType()).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Literal) {
			Literal literal = (Literal) obj;
			if ((name+getMediaType()).equals(literal.name+literal.getMediaType())) {
				return true;
			}
		}
		return false;
	}
}
