package com.expresstreasure.tils;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class UploadFile {
	public static String postFile(String url, String filePath, String md5File,
			String cid, String goods_id, String sp_fee, String buyer_fee) {
		// DefaultHttpClient client = new DefaultHttpClient();
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		String BOUNDARY = "*****"; // 边界标识
		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, null);

		// 上传文件的path
		final File imageFile = new File(filePath);

		FileBody fileBody = new FileBody(imageFile);
		entity.addPart("xia.jpg", fileBody);

		// 上传的其他参数
		try {
			FormBodyPart formBodyPart1 = new FormBodyPart("cid",
					new StringBody(cid));
			FormBodyPart formBodyPart2 = new FormBodyPart("id", new StringBody(
					goods_id));
			FormBodyPart formBodyPart3 = new FormBodyPart("md5_key",
					new StringBody(md5File));
			FormBodyPart formBodyPart4 = new FormBodyPart("sp_fee",
					new StringBody(sp_fee));
			FormBodyPart formBodyPart5 = new FormBodyPart("buyer_fee",
					new StringBody(buyer_fee));
			entity.addPart(formBodyPart1);
			entity.addPart(formBodyPart2);
			entity.addPart(formBodyPart3);
			entity.addPart(formBodyPart4);
			entity.addPart(formBodyPart5);
			post.setEntity(entity);

			// 响应
			HttpResponse httpResponse;
			httpResponse = client.execute(post);
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			String response = EntityUtils.toString(httpResponse.getEntity(),
					HTTP.UTF_8);
			if (statusCode == HttpStatus.SC_OK) {
				return "success";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
				client = null;
			}
		}
		return null;

	}
}
