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
			Object[] paramValues)  {
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
		try {
			ht.call(null, envelope);
			if (envelope.getResponse() != null && envelope.getResponse() instanceof SoapPrimitive) {
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				result = response.toString();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "network_error#1";
		} catch (XmlPullParserException e) {
			return "network_error#1";
		}
		
		return result;
	}
	
	public boolean testConnection() throws InterruptedException {
		class ConnectionTester extends Thread {
			private String uri;
			private boolean result = false;
			public ConnectionTester(String uri) {
				this.uri = uri;
			}
			
			@Override
			public void run() {
				String naiveResult = WebService.getInstance().runFunction(
						uri, "testConnection", null);
				if(naiveResult.equals("1"))
					result = true;
			}
			
			public boolean getResult() {
				return result;
			}
		}
		ConnectionTester t1 = new ConnectionTester(ServerParam.USER_URL);
		ConnectionTester t2 = new ConnectionTester(ServerParam.USER_URL);
		ConnectionTester t3 = new ConnectionTester(ServerParam.USER_URL);
		t1.start();
		t2.start();
		t3.start();
		t1.join();
		t2.join();
		t3.join();
		if(t1.getResult() && t2.getResult() && t3.getResult())
			return true;
		else
			return false;
	}
}
