package cn.edu.nju.ws.camo.android.operate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.rdf.Property;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;


/**
 * @author Hang Zhang
 *
 */
public class View {
	
	/**
	 * @param inst
	 * @return 具有以inst为subject的三元组的instance
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public UriInstWithNeigh viewInstDown(UriInstance inst) throws IOException, XmlPullParserException {
		UriInstWithNeigh instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
		String mediaType = inst.getMediaType();
		Object[] params = {inst.getUri()};
		String naiveResult = WebService.getInstance().runFunction(ServerParam.VIEW_URL, "instViewDown", params);
		if(naiveResult.equals(""))
			return null;
		List<String> naiveTriples = SetSerialization.deserialize3(naiveResult);
		for(String naiveTriple : naiveTriples) {
			List<String> naiveResources = SetSerialization.deserialize2(naiveTriple);	//(p,o)
			Property property = RdfFactory.getInstance().createProperty(naiveResources.get(0), mediaType);	//p
			Resource value = null;	//o
			List<String> naiveObject = SetSerialization.deserialize1(naiveResources.get(1));	
			if(naiveObject.size() == 3) {
				value = RdfFactory.getInstance().createInstance(naiveObject.get(0), mediaType, naiveObject.get(2), naiveObject.get(1));
			} else {
				String valueStr = naiveObject.get(0);
				if(valueStr.startsWith("http://")) {
					value = RdfFactory.getInstance().createInstance(valueStr, mediaType);
				} else {
					value = RdfFactory.getInstance().createLiteral(valueStr, mediaType);
				}
			}
			Triple triple = RdfFactory.getInstance().createTriple(inst, property, value);
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
	public UriInstWithNeigh viewInstUp(UriInstance inst) throws IOException, XmlPullParserException {
		UriInstWithNeigh instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
		String mediaType = inst.getMediaType();
		Object[] params = {inst.getUri()};
		String naiveResult = WebService.getInstance().runFunction(ServerParam.VIEW_URL, "instViewUp", params);
		if(naiveResult.equals(""))
			return null;
		List<String> naiveTriples = SetSerialization.deserialize3(naiveResult);
		for(String naiveTriple : naiveTriples) {
			List<String> naiveResources = SetSerialization.deserialize2(naiveTriple);	//(s,p)
			Property property = RdfFactory.getInstance().createProperty(naiveResources.get(1), mediaType);	//p
			UriInstance subject = null;	//s
			List<String> naiveSubject = SetSerialization.deserialize1(naiveResources.get(0));	
			if(naiveSubject.size() == 3) {
				subject = RdfFactory.getInstance().createInstance(naiveSubject.get(0), mediaType, naiveSubject.get(2), naiveSubject.get(1));
			} else {
				String valueStr = naiveSubject.get(0);
				subject = RdfFactory.getInstance().createInstance(valueStr, mediaType);
			}
			Triple triple = RdfFactory.getInstance().createTriple(subject, property, inst);
			instWithNeigh.addTripleUp(triple);
		}
		return instWithNeigh;
	}
	
	/**
	 * @param searchText: 搜索的关键字，以空格分隔
	 * @param mediaType: movie/music/photo
	 * @return 搜索到的instance与其详细信息的映射(以instance为subject)
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public Map<UriInstance, UriInstWithNeigh> searchInstDown(String searchText, String mediaType) throws IOException, XmlPullParserException {
		Map<UriInstance, UriInstWithNeigh> result = new HashMap<UriInstance, UriInstWithNeigh>();
		Object[] params = {searchText, mediaType};
		String naiveResult = WebService.getInstance().runFunction(ServerParam.VIEW_URL, "textViewDown", params);
		if(naiveResult.equals(""))
			return null;
		List<String> naiveInstInfos = SetSerialization.deserialize5(naiveResult);
		for(String naiveInstInfo : naiveInstInfos) {	// instance
			UriInstance inst = null;	
			UriInstWithNeigh instWithNeigh = null;
			List<String> naiveInstTerm = SetSerialization.deserialize4(naiveInstInfo);
			List<String> instLabelType = SetSerialization.deserialize1(naiveInstTerm.get(0));
			if(instLabelType.size() == 3) {
				inst = RdfFactory.getInstance().createInstance(instLabelType.get(0), mediaType, instLabelType.get(2), instLabelType.get(1));
			} else {
				String instStr = instLabelType.get(0);
				inst = RdfFactory.getInstance().createInstance(instStr, mediaType);
			}
			instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
			List<String> naiveTriples = SetSerialization.deserialize3(naiveInstTerm.get(1));
			for(String naiveTriple : naiveTriples) {	// triples
				List<String> naiveResources = SetSerialization.deserialize2(naiveTriple);	//(p,o)
				Property property = RdfFactory.getInstance().createProperty(naiveResources.get(0), mediaType);	//p
				Resource object = null;	//o
				List<String> naiveObject = SetSerialization.deserialize1(naiveResources.get(1));	
				if(naiveObject.size() == 3) {
					object = RdfFactory.getInstance().createInstance(naiveObject.get(0), mediaType, naiveObject.get(2), naiveObject.get(1));
				} else {
					String objectStr = naiveObject.get(0);
					if(objectStr.startsWith("http://")) {
						object = RdfFactory.getInstance().createInstance(objectStr, mediaType);
					} else {
						object = RdfFactory.getInstance().createLiteral(objectStr, mediaType);
					}
				}
				Triple triple = RdfFactory.getInstance().createTriple(inst, property, object);
				instWithNeigh.addTripleDown(triple);
			}
			result.put(inst, instWithNeigh);
		}
		return result;
	}
	
	/**
	 * @param searchText: 搜索的关键字，以空格分隔
	 * @param mediaType: movie/music/photo
	 * @return 搜索到的instance与其详细信息的映射(以instance为object)
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public Map<UriInstance, UriInstWithNeigh> searchInstUp(String searchText, String mediaType) throws IOException, XmlPullParserException {
		Map<UriInstance, UriInstWithNeigh> result = new HashMap<UriInstance, UriInstWithNeigh>();
		Object[] params = {searchText, mediaType};
		String naiveResult = WebService.getInstance().runFunction(ServerParam.VIEW_URL, "textViewUp", params);
		if(naiveResult.equals(""))
			return null;
		List<String> naiveInstInfos = SetSerialization.deserialize5(naiveResult);
		for(String naiveInstInfo : naiveInstInfos) {	// instance
			UriInstance inst = null;	
			UriInstWithNeigh instWithNeigh = null;
			List<String> naiveInstTerm = SetSerialization.deserialize4(naiveInstInfo);
			List<String> instLabelType = SetSerialization.deserialize1(naiveInstTerm.get(0));
			if(instLabelType.size() == 3) {
				inst = RdfFactory.getInstance().createInstance(instLabelType.get(0), mediaType, instLabelType.get(2), instLabelType.get(1));
			} else {
				String instStr = instLabelType.get(0);
				inst = RdfFactory.getInstance().createInstance(instStr, mediaType);
			}
			instWithNeigh = RdfFactory.getInstance().createInstWithNeigh(inst);
			List<String> naiveTriples = SetSerialization.deserialize3(naiveInstTerm.get(1));
			for(String naiveTriple : naiveTriples) {	// triples
				List<String> naiveResources = SetSerialization.deserialize2(naiveTriple);	//(s,p)
				Property property = RdfFactory.getInstance().createProperty(naiveResources.get(0), mediaType);	//p
				UriInstance subject = null;	//s
				List<String> naiveSubject = SetSerialization.deserialize1(naiveResources.get(1));	
				if(naiveSubject.size() == 3) {
					subject = RdfFactory.getInstance().createInstance(naiveSubject.get(0), mediaType, naiveSubject.get(2), naiveSubject.get(1));
				} else {
					String subjectStr = naiveSubject.get(0);
					subject = RdfFactory.getInstance().createInstance(subjectStr, mediaType);
				}
				Triple triple = RdfFactory.getInstance().createTriple(subject, property, inst);
				instWithNeigh.addTripleUp(triple);
			}
			result.put(inst, instWithNeigh);
		}
		return result;
	}
	
	
	/**
	 * @param user
	 * @param mediaType 若mediaType与instType同时为null，则返回所有like的记录
	 * @param instType 如果instType为null，则返回mediaType的like记录
	 * @return
	 */
	public List<LikePrefer> viewLike(User user, String mediaType, String instType) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = {user.getId(),mediaType,instType,CommandFactory.LIKE};
		try {
			String naivePrefer = WebService.getInstance().runFunction(ServerParam.USER_URL, "getPreference", paramValues);
			List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
			for(String naiveTerm : naiveTerms) {
				List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
				UriInstance inst = RdfFactory.getInstance().createInstance(naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3), naiveUnits.get(4));
				boolean subscribe = false;
				if(naiveUnits.get(6).equals("1"))
					subscribe = true;
				LikePrefer like = new LikePrefer(user, inst, subscribe);
				likes.add(like);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return likes;
	}
	
	
	/**
	 * @param user
	 * @param mediaType 若mediaType与instType同时为null，则返回所有dislike的记录
	 * @param instType 如果instType为null，则返回mediaType的dislike记录
	 * @return
	 */
	public List<DislikePrefer> viewDislike(User user, String mediaType, String instType) {
		List<DislikePrefer> dislikes = new ArrayList<DislikePrefer>();
		Object[] paramValues = {user.getId(),mediaType,instType,CommandFactory.DISLIKE};
		try {
			String naivePrefer = WebService.getInstance().runFunction(ServerParam.USER_URL, "getPreference", paramValues);
			List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
			for(String naiveTerm : naiveTerms) {
				List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
				UriInstance inst = RdfFactory.getInstance().createInstance(naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3), naiveUnits.get(4));
				DislikePrefer disLike = new DislikePrefer(user, inst);
				dislikes.add(disLike);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return dislikes;
	}
	
	/**
	 * @param user
	 * @param mediaType 若mediaType与instType同时为null，则返回所有订阅的记录
	 * @param instType 如果instType为null，则返回mediaType的订阅记录
	 * @return
	 */
	public List<LikePrefer> viewSubscribe(User user, String mediaType, String instType) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = {user.getId(),mediaType,instType};
		try {
			String naivePrefer = WebService.getInstance().runFunction(ServerParam.USER_URL, "getSubscribe", paramValues);
			List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
			for(String naiveTerm : naiveTerms) {
				List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
				UriInstance inst = RdfFactory.getInstance().createInstance(naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3), naiveUnits.get(4));
				LikePrefer like = new LikePrefer(user, inst, true);
				likes.add(like);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return likes;
	}
}
