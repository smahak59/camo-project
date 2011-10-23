package cn.edu.nju.ws.camo.android.operate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.rdf.Property;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.UtilParam;

/**
 * @author Hang Zhang
 * 
 */
public class InstViewOperation {

	
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
						naiveObject.get(1));
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
							literalNomalize(valueStr), mediaType);
				}
			}
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
			UriInstance subject = null; // s
			List<String> naiveSubject = SetSerialization
					.deserialize1(naiveResources.get(0));
			if (naiveSubject.size() == 3) {
				subject = RdfFactory.getInstance().createInstance(
						naiveSubject.get(0), mediaType, naiveSubject.get(2),
						naiveSubject.get(1));
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
	
	public static Set<UriInstance> searchInst(String searchText,
			String mediaType) throws IOException, XmlPullParserException {
		Set<UriInstance> instList = new HashSet<UriInstance>();
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
			String label = terms.get(1);
			String clsType = terms.get(2);
			UriInstance newInst = RdfFactory.getInstance().createInstance(inst, mediaType, clsType, label);
			instList.add(newInst);
		}
		return instList;
	}

	/**
	 * @param searchText
	 *            : 搜索的关键字，以空格分隔
	 * @param mediaType
	 *            : movie/music/photo
	 * @return 搜索到的instance与其详细信息的映射(以instance为subject)
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Map<UriInstance, UriInstWithNeigh> searchInstDown(String searchText,
			String mediaType) throws IOException, XmlPullParserException {
		Map<UriInstance, UriInstWithNeigh> result = new HashMap<UriInstance, UriInstWithNeigh>();
		Object[] params = { searchText, mediaType };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.VIEW_URL, "textViewDown", params);
		if (naiveResult.equals("") || naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return result;
		List<String> naiveInstInfos = SetSerialization
				.deserialize5(naiveResult);
		for (String naiveInstInfo : naiveInstInfos) { // instance
			UriInstance inst = null;
			UriInstWithNeigh instWithNeigh = null;
			List<String> naiveInstTerm = SetSerialization
					.deserialize4(naiveInstInfo);
			List<String> instLabelType = SetSerialization
					.deserialize1(naiveInstTerm.get(0));
			if (instLabelType.size() == 3) {
				inst = RdfFactory.getInstance().createInstance(
						instLabelType.get(0), mediaType, instLabelType.get(2),
						instLabelType.get(1));
			} else {
				String instStr = instLabelType.get(0);
				inst = RdfFactory.getInstance().createInstance(instStr,
						mediaType);
			}
			instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
			List<String> naiveTriples = SetSerialization
					.deserialize3(naiveInstTerm.get(1));
			for (String naiveTriple : naiveTriples) { // triples
				List<String> naiveResources = SetSerialization
						.deserialize2(naiveTriple); // (p,o)
				Property property = RdfFactory.getInstance().createProperty(
						naiveResources.get(0), mediaType); // p
				if(isExProp(property))
					continue;
				setPropName(property);
				Resource object = null; // o
				List<String> naiveObject = new ArrayList<String>();
				if (naiveResources.size() > 1) {
					naiveObject = SetSerialization.deserialize1(naiveResources
							.get(1));
				} else
					continue;
				if (naiveObject.size() == 3) {
					object = RdfFactory.getInstance().createInstance(
							naiveObject.get(0), mediaType, naiveObject.get(2),
							naiveObject.get(1));
				} else {
					String objectStr = naiveObject.get(0);
					if (objectStr.startsWith("http://")) {
						object = RdfFactory.getInstance().createInstance(
								objectStr, mediaType);
						if(property.getUri().equals("http://xmlns.com/foaf/0.1/homepage") || 
								property.getUri().equals("http://dbpedia.org/property/hasPhotoCollection") ||
								property.getUri().equals("http://xmlns.com/foaf/0.1/img") ||
								property.getUri().equals("http://xmlns.com/foaf/0.1/page"))
							object = RdfFactory.getInstance().createLiteral(objectStr, mediaType);
					} else {
						object = RdfFactory.getInstance().createLiteral(
								literalNomalize(objectStr), mediaType);
					}
				}
				Triple triple = RdfFactory.getInstance().createTriple(inst,
						property, object);
				instWithNeigh.addTripleDown(triple);
			}
			result.put(inst, instWithNeigh);
		}
		return result;
	}

	/**
	 * @param searchText
	 *            : 搜索的关键字，以空格分隔
	 * @param mediaType
	 *            : movie/music/photo
	 * @return 搜索到的instance与其详细信息的映射(以instance为object)
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Map<UriInstance, UriInstWithNeigh> searchInstUp(String searchText,
			String mediaType) throws IOException, XmlPullParserException {
		Map<UriInstance, UriInstWithNeigh> result = new HashMap<UriInstance, UriInstWithNeigh>();
		Object[] params = { searchText, mediaType };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.VIEW_URL, "textViewUp", params);
		if (naiveResult.equals("") || naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return result;
		List<String> naiveInstInfos = SetSerialization
				.deserialize5(naiveResult);
		for (String naiveInstInfo : naiveInstInfos) { // instance
			UriInstance inst = null;
			UriInstWithNeigh instWithNeigh = null;
			List<String> naiveInstTerm = SetSerialization
					.deserialize4(naiveInstInfo);
			List<String> instLabelType = SetSerialization
					.deserialize1(naiveInstTerm.get(0));
			if (instLabelType.size() == 3) {
				inst = RdfFactory.getInstance().createInstance(
						instLabelType.get(0), mediaType, instLabelType.get(2),
						instLabelType.get(1));
			} else {
				String instStr = instLabelType.get(0);
				inst = RdfFactory.getInstance().createInstance(instStr,
						mediaType);
			}
			instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
			List<String> naiveTriples = SetSerialization
					.deserialize3(naiveInstTerm.get(1));
			for (String naiveTriple : naiveTriples) { // triples
				List<String> naiveResources = SetSerialization
						.deserialize2(naiveTriple); // (s,p)
				Property property = RdfFactory.getInstance().createProperty(
						naiveResources.get(0), mediaType); // p
				if(isExProp(property))
					continue;
				setPropName(property);
				UriInstance subject = null; // s
				List<String> naiveSubject = SetSerialization
						.deserialize1(naiveResources.get(1));
				if (naiveSubject.size() == 3) {
					subject = RdfFactory.getInstance().createInstance(
							naiveSubject.get(0), mediaType,
							naiveSubject.get(2), naiveSubject.get(1));
				} else {
					String subjectStr = naiveSubject.get(0);
					subject = RdfFactory.getInstance().createInstance(
							subjectStr, mediaType);
				}
				Triple triple = RdfFactory.getInstance().createTriple(subject,
						property, inst);
				instWithNeigh.addTripleUp(triple);
			}
			result.put(inst, instWithNeigh);
		}
		return result;
	}

	private static String literalNomalize(String str) {
		String result = str;
		int endIdx = str.indexOf("^^");
		if (endIdx > 0)
			result = str.substring(0, endIdx);
		endIdx = result.indexOf("@en");
		if (endIdx > 0)
			result = str.substring(0, endIdx);
		endIdx = result.indexOf("(");
		if (endIdx > 0)
			result = str.substring(0, endIdx);
		result.replaceAll("%", " ");
		result.replaceAll("_", " ");
		return result;
	}
	
	private static boolean isExProp(Property prop) {
		return UtilParam.EXCLUDED_PROPS.contains(prop.getUri());
	}
	
	private static void setPropName(Property prop) {
		if(UtilParam.PROP_TO_NAME_DOWN.containsKey(prop.getUri()))
			prop.setName(UtilParam.PROP_TO_NAME_DOWN.get(prop.getUri()));
	}
}
