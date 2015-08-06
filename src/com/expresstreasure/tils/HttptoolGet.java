package com.expresstreasure.tils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttptoolGet {

	/**
	 * HttpGet请求
	 **/
	public String httpget(String URL) throws ConnectTimeoutException {
		HttpGet httpRequest = new HttpGet(URL);
		try {

			/* 建立HTTP Get对象 */
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				return strResult;

			} else {
				// DataManger.instance.getMainActivity().checkNetwork();
			}
		} catch (ClientProtocolException e) {
			// DataManger.instance.getMainActivity().checkNetwork();
		} catch (IOException e) {

		} catch (Exception e) {
			// DataManger.instance.getMainActivity().checkNetwork();
		}
		return "";
	}

}
