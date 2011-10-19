package cn.edu.nju.ws.camo.android.operate;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;

public class PreferViewOperation {

	/**
	 * @param user
	 * @param mediaType
	 *            若mediaType与instType同时为null，则返回所有like的记录
	 * @param instType
	 *            如果instType为null，则返回mediaType的like记录
	 * @return
	 */
	public static List<LikePrefer> viewLike(User user, String mediaType,
			String instType) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, instType,
				CommandFactory.LIKE };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return likes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					naiveUnits.get(4));
			// boolean subscribe = false;
			// if(naiveUnits.get(6).equals("1"))
			// subscribe = true;
			LikePrefer like = new LikePrefer(user, inst);
			likes.add(like);
		}
		return likes;
	}

	public static List<LikePrefer> viewLike(User user, String mediaType) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, "",
				CommandFactory.LIKE };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return likes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					naiveUnits.get(4));
			// boolean subscribe = false;
			// if(naiveUnits.get(6).equals("1"))
			// subscribe = true;
			LikePrefer like = new LikePrefer(user, inst);
			likes.add(like);
		}
		return likes;
	}

	/**
	 * @param user
	 * @param mediaType
	 *            若mediaType与instType同时为null，则返回所有dislike的记录
	 * @param instType
	 *            如果instType为null，则返回mediaType的dislike记录
	 * @return
	 */
	public static List<DislikePrefer> viewDislike(User user, String mediaType,
			String instType) {
		List<DislikePrefer> dislikes = new ArrayList<DislikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, instType,
				CommandFactory.DISLIKE };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return dislikes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					naiveUnits.get(4));
			DislikePrefer disLike = new DislikePrefer(user, inst);
			dislikes.add(disLike);
		}
		return dislikes;
	}

	public static List<DislikePrefer> viewDislike(User user, String mediaType) {
		List<DislikePrefer> dislikes = new ArrayList<DislikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, "",
				CommandFactory.DISLIKE };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return dislikes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					naiveUnits.get(4));
			DislikePrefer disLike = new DislikePrefer(user, inst);
			dislikes.add(disLike);
		}
		return dislikes;
	}
	
}
