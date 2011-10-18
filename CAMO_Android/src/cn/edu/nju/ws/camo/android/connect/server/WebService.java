package cn.edu.nju.ws.camo.android.connect.server;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Hang Zhang
 *
 */
public class WebService {

	private static WebService instance = null;

	private WebService() {
	}

	public static WebService getInstance() {
		if (instance == null)
			instance = new WebService();
		return instance;
	}

	public String runFunction(String url, String methodName,
			Object[] paramValues) throws IOException, XmlPullParserException {
		String result = "";
		SoapObject request = new SoapObject(ServerParam.NAMESPACE, methodName);
		int paramIdx = 0;
		if (paramValues != null) {
			for (Object paramValue : paramValues) {
				request.addProperty("arg" + paramIdx, paramValue);
				paramIdx++;
			}
		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(request);
		HttpTransportSE ht = new HttpTransportSE(url);
		ht.call(null, envelope);
		if (envelope.getResponse() != null && envelope.getResponse() instanceof SoapPrimitive) {
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			result = response.toString();
		}
		return result;
	}
}
