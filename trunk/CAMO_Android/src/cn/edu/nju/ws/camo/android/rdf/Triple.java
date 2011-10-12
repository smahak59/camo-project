package cn.edu.nju.ws.camo.android.rdf;

/**
 * @author Hang Zhang
 *
 */
public class Triple {

	private Resource s;
	private Resource p;
	private Resource o;
	
	public Triple(Resource subj, Resource pred, Resource obj) {
		s = subj;
		p = pred;
		o = obj;
	}

	public Resource getSubject() {
		return s;
	}

	public Resource getPredicate() {
		return p;
	}

	public Resource getObject() {
		return o;
	}
}
