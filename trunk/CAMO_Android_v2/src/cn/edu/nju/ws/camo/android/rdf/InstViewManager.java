package cn.edu.nju.ws.camo.android.rdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.UtilParam;

/**
 * @author Hang Zhang
 * 
 */
public class InstViewManager {

	
	/**
	 * @param inst
	 * @return 具有以inst为subject的三元组的instance
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static UriInstWithNeigh viewInstDown(UriInstance inst) throws IOException,
			XmlPullParserException {
		UriInstWithNeigh instWithNeigh = RdfFactory.getInstance()
				.createInstWithNeigh(inst);
		String mediaType = inst.getMediaType();
		Object[] params = { inst.getUri() };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.VIEW_URL, "instViewDown", params);
		if (naiveResult.equals("") || naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
		Map<Property, Map<String, UriInstance>> propToValues = new HashMap<Property, Map<String, UriInstance>>();
		List<String> naiveTriples = SetSerialization.deserialize3(naiveResult);
		for (String naiveTriple : naiveTriples) {
			List<String> naiveResources = SetSerialization
					.deserialize2(naiveTriple); // (p,o)
			Property property = RdfFactory.getInstance().createProperty(
					naiveResources.get(0), mediaType); // p
			if(isExProp(property))
				continue;
			setPropName(property);
			Resource value = null; // o
			List<String> naiveObject = new ArrayList<String>();
			if (naiveResources.size() > 1)
				naiveObject = SetSerialization.deserialize1(naiveResources
						.get(1));
			else
				continue;
			if (naiveObject.size() == 3) {
				value = RdfFactory.getInstance().createInstance(
						naiveObject.get(0), mediaType, naiveObject.get(2),
						SetSerialization.instNameNomalize(naiveObject.get(1)));
			} else {
				String valueStr = naiveObject.get(0);
				if (valueStr.startsWith("http://")) {
					value = RdfFactory.getInstance().createInstance(valueStr,
							mediaType);
					if(property.getUri().equals("http://xmlns.com/foaf/0.1/homepage") || 
							property.getUri().equals("http://dbpedia.org/property/hasPhotoCollection") ||
							property.getUri().equals("http://xmlns.com/foaf/0.1/img") ||
							property.getUri().equals("http://xmlns.com/foaf/0.1/page"))
						value = RdfFactory.getInstance().createLiteral(valueStr, mediaType);
				} else {
					value = RdfFactory.getInstance().createLiteral(
							SetSerialization.instNameNomalize(valueStr), mediaType);
				}
			}
			if(value instanceof UriInstance && ((UriInstance)value).getName().trim().length()>0) {
				if(propToValues.containsKey(property) && propToValues.get(property).containsKey(((UriInstance)value).getName().trim().toLowerCase())) {
					UriInstance oldInst = propToValues.get(property).get(((UriInstance)value).getName().trim().toLowerCase());
					if(((UriInstance)value).getUri().startsWith("http://dbpedia.org")) {
						oldInst = (UriInstance)value;
					}
					continue;
				} else {
					if(propToValues.containsKey(property)) {
						propToValues.get(property).put(((UriInstance)value).getName().trim().toLowerCase(),(UriInstance)value);
					} else {
						Map<String, UriInstance> valueSet = new HashMap<String, UriInstance>();
						valueSet.put(((UriInstance)value).getName().trim().toLowerCase(),(UriInstance)value);
						propToValues.put(property, valueSet);
					}
				}
			}
			if(property.getName().length()==0)
				continue;
			Triple triple = RdfFactory.getInstance().createTriple(inst,
					property, value);
			instWithNeigh.addTripleDown(triple);
		}
		return instWithNeigh;
	}

	/**
	 * @param inst
	 * @return 具有以inst为object的三元组的instance
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static UriInstWithNeigh viewInstUp(UriInstance inst) throws IOException,
			XmlPullParserException {
		UriInstWithNeigh instWithNeigh = RdfFactory.getInstance()
				.createInstWithNeigh(inst);
		String mediaType = inst.getMediaType();
		Object[] params = { inst.getUri() };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.VIEW_URL, "instViewUp", params);
		if (naiveResult.equals("") || naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
		List<String> naiveTriples = SetSerialization.deserialize3(naiveResult);
		for (String naiveTriple : naiveTriples) {
			List<String> naiveResources = SetSerialization
					.deserialize2(naiveTriple); // (s,p)
			Property property = RdfFactory.getInstance().createProperty(
					naiveResources.get(1), mediaType); // p
			if(isExProp(property))
				continue;
			setPropName(property);
			setUpPropTrans(property);
			UriInstance subject = null; // s
			List<String> naiveSubject = SetSerialization
					.deserialize1(naiveResources.get(0));
			if (naiveSubject.size() == 3) {
				subject = RdfFactory.getInstance().createInstance(
						naiveSubject.get(0), mediaType, naiveSubject.get(2),
						SetSerialization.instNameNomalize(naiveSubject.get(1)));
			} else {
				String valueStr = naiveSubject.get(0);
				subject = RdfFactory.getInstance().createInstance(valueStr,
						mediaType);
			}
			Triple triple = RdfFactory.getInstance().createTriple(subject,
					property, inst);
			instWithNeigh.addTripleUp(triple);
		}
		return instWithNeigh;
	}
	
	public static List<UriInstance> searchInst(String searchText,
			String mediaType) throws IOException, XmlPullParserException {
		List<UriInstance> instList = new ArrayList<UriInstance>();
		Object[] params = { searchText, mediaType };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.VIEW_URL, "textView", params);
		if (naiveResult.equals("") || naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return instList;
		List<String> naiveInstInfos = SetSerialization
				.deserialize2(naiveResult);
		for (String naiveInstInfo : naiveInstInfos) { // instance
			List<String> terms = SetSerialization.deserialize1(naiveInstInfo);
			String inst = terms.get(0);
			String label = SetSerialization.instNameNomalize(terms.get(1));
			String clsType = terms.get(2);
			UriInstance newInst = RdfFactory.getInstance().createInstance(inst, mediaType, clsType, label);
			instList.add(newInst);
		}
		return instList;
	}
	
	private static boolean isExProp(Property prop) {
		return UtilParam.EXCLUDED_PROPS.contains(prop.getUri());
	}
	
	private static void setPropName(Property prop) {
		Log.v("*********", "orP:" + UtilParam.PROP_TO_NAME_DOWN.get(prop.getUri()));
		Log.v("*********", "orSize:" + UtilParam.PROP_TO_NAME_DOWN.size());
		
		if(UtilParam.PROP_TO_NAME_DOWN.containsKey(prop.getUri()))
			prop.setName(UtilParam.PROP_TO_NAME_DOWN.get(prop.getUri()).trim());
		
	}
	
	private static void setUpPropTrans(Property prop) {
		if(UtilParam.UP_PROP_TRANS_DOWN.containsKey(prop.getUri()))
			prop.setName(UtilParam.UP_PROP_TRANS_DOWN.get(prop.getUri()));
		else if(prop.getName().length()>0) {
			prop.setName(prop.getName() + " of");
		}
	}
}
