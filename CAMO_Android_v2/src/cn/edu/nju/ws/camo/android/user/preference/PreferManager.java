package cn.edu.nju.ws.camo.android.user.preference;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.command.Command;
import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.util.SetSerialization;

public class PreferManager {

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
				1 };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return likes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					SetSerialization.instNameNomalize(naiveUnits.get(4)));
			LikePrefer like = new LikePrefer(user, inst);
			likes.add(like);
		}
		return likes;
	}

	
	/**
	 * @param user
	 * @param mediaType
	 * @return
	 */
	public static List<LikePrefer> viewLike(User user, String mediaType) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, "",
				1 };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return likes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					SetSerialization.instNameNomalize(naiveUnits.get(4)));
			LikePrefer like = new LikePrefer(user, inst);
			likes.add(like);
		}
		return likes;
	}
	
	/**
	 * @param user
	 * @return
	 */
	public static List<LikePrefer> viewLike(User user) {
		List<LikePrefer> likes = new ArrayList<LikePrefer>();
		Object[] paramValues = { user.getId(), "", "",
				1 };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return likes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					SetSerialization.instNameNomalize(naiveUnits.get(4)));
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
				0 };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return dislikes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					SetSerialization.instNameNomalize(naiveUnits.get(4)));
			DislikePrefer disLike = new DislikePrefer(user, inst);
			dislikes.add(disLike);
		}
		return dislikes;
	}

	/**
	 * @param user
	 * @param mediaType
	 * @return
	 */
	public static List<DislikePrefer> viewDislike(User user, String mediaType) {
		List<DislikePrefer> dislikes = new ArrayList<DislikePrefer>();
		Object[] paramValues = { user.getId(), mediaType, "",
				0 };
		String naivePrefer = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getPreference", paramValues);
		if (naivePrefer.length() == 0 || naivePrefer.equals(ServerParam.NETWORK_ERROR1))
			return dislikes;
		List<String> naiveTerms = SetSerialization.deserialize2(naivePrefer);
		for (String naiveTerm : naiveTerms) {
			List<String> naiveUnits = SetSerialization.deserialize1(naiveTerm);
			UriInstance inst = RdfFactory.getInstance().createInstance(
					naiveUnits.get(1), naiveUnits.get(2), naiveUnits.get(3),
					SetSerialization.instNameNomalize(naiveUnits.get(4)));
			DislikePrefer disLike = new DislikePrefer(user, inst);
			dislikes.add(disLike);
		}
		return dislikes;
	}
	
	
	public static List<LikePrefer> viewCommonLike(User user1, User user2, String mediaType) {
		List<LikePrefer> commomLikes = new ArrayList<LikePrefer>();
		List<LikePrefer> likes1 = viewLike(user1, mediaType);
		List<LikePrefer> likes2 = viewLike(user2, mediaType);
		for(LikePrefer prefer1 : likes1) {
			for(LikePrefer prefer2 : likes2) {
				if(prefer1.getInst().getUri().equals(prefer2.getInst().getUri())) {
					commomLikes.add(prefer2);
				}
			}
		}
		return commomLikes;
	}
	
	public static List<LikePrefer> viewCommonLike(User user1, User user2) {
		List<LikePrefer> commomLikes = new ArrayList<LikePrefer>();
		List<LikePrefer> likes1 = viewLike(user1);
		List<LikePrefer> likes2 = viewLike(user2);
		for(LikePrefer prefer1 : likes1) {
			for(LikePrefer prefer2 : likes2) {
				if(prefer1.getInst().getUri().equals(prefer2.getInst().getUri())) {
					commomLikes.add(prefer2);
				}
			}
		}
		return commomLikes;
	}
	
	public static Command createLikeCmd(LikePrefer prefer) {
		class LikeCommand implements Command {
			private LikePrefer prefer;
			LikeCommand(LikePrefer prefer) {
				this.prefer = prefer;
			}
			public void execute() {
				Object[] paramValues = { prefer.getUser().getId(),
						prefer.getInst().getUri(), prefer.getInst().getMediaType(),
						prefer.getInst().getClassType(), prefer.getInst().getName(),
						1, 0 };
				WebService.getInstance().runFunction(ServerParam.USER_URL,
						"addPreference", paramValues);
			}
		}
		return new LikeCommand(prefer);
	}
	
	public static Command createCancelPreferCmd(Preference prefer) {
		class DelPreferCommand implements Command {
			private Preference prefer;
			public DelPreferCommand(Preference prefer) {
				this.prefer = prefer;
			}
			public void execute() {
				Object[] paramValues = { prefer.getUser().getId(),
						prefer.getInst().getUri() };
				WebService.getInstance().runFunction(ServerParam.USER_URL,
						"delPreference", paramValues);
			}
		}
		return new DelPreferCommand(prefer);
	}
	
	public static Command createDislikeCmd(DislikePrefer prefer) {
		class DislikeCommand implements Command {
			private DislikePrefer prefer;
			DislikeCommand(DislikePrefer prefer) {
				this.prefer = prefer;
			}
			public void execute() {
				Object[] paramValues = { prefer.getUser().getId(),
						prefer.getInst().getUri(), prefer.getInst().getMediaType(),
						prefer.getInst().getClassType(), prefer.getInst().getName(),
						0, 0 };
				WebService.getInstance().runFunction(ServerParam.USER_URL,
						"addPreference", paramValues);
			}
		}
		return new DislikeCommand(prefer);
	}
	
}
