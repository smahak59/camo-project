package cn.edu.nju.ws.camo.android.rdf;

/**
 * @author Hang Zhang
 *
 */
public class Triple {

	private UriInstance s;
	private Property p;
	private Resource o;
	
	Triple(UriInstance subj, Property pred, Resource obj) {
		s = subj;
		p = pred;
		o = obj;
	}

	public UriInstance getSubject() {
		return s;
	}

	public Property getPredicate() {
		return p;
	}

	public Resource getObject() {
		return o;
	}
}
