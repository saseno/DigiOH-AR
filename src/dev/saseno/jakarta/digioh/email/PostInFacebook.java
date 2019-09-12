package dev.saseno.jakarta.digioh.email;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import com.restfb.types.User;

public class PostInFacebook implements Runnable {

	private String photoPath = null;	
	private String accessToken = "";
	
	public PostInFacebook(String photoPath) {
		try {
			this.photoPath 	= photoPath;
									
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void postFacebook() {
		try {

			System.err.println("----------------------");
			System.err.println("post facebook START");
			System.err.println("----------------------");
			
			//String fileName = null;
			//byte[] data = null;			
			
			File initialFile = new File(photoPath);		    
		    byte[] bFile = Files.readAllBytes(Paths.get(photoPath));
			
			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);
			User user = facebookClient.fetchObject("me", User.class);
			
			System.err.println("User name: " + user.getName());

			FacebookType photo = facebookClient.publish("facebook_page_id/photos", FacebookType.class,
					BinaryAttachment.with(initialFile.getName(), bFile));

			System.err.println("----------------------");
			System.err.println("post facebook OK");
			System.err.println("----------------------");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			System.err.println("----------------------");
			System.err.println("post facebook END");
			System.err.println("----------------------");
		}
	}

	@Override
	public void run() {
		try {
			
			//postFacebook();
			postPhoto();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//experiment
	
	static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";

	private void postPhoto() throws IOException {
		URL url;
		HttpURLConnection urlConn;
		DataOutputStream printout;
		DataInputStream input;
		
		// -------------------------------------------------------------------

		File image = new File(photoPath);
		FileInputStream imgStream = new FileInputStream(image);
		byte[] buffer = new byte[(int) image.length()];
		imgStream.read(buffer);

		// -------------------------------------------------------------------

		// url = new URL ("https://graph.facebook.com/me/feed");
		url = new URL("https://graph.facebook.com/me/photos?access_token=" + accessToken);
		System.out.println("Before Open Connection");

		urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + getBoundaryString());

		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");

		String boundary = getBoundaryString();
		String boundaryMessage = getBoundaryMessage(boundary, "upload_field", image.getName(), "image/png");
		String endBoundary = "\r\n--" + boundary + "--\r\n";

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(boundaryMessage.getBytes());
		bos.write(buffer);
		bos.write(endBoundary.getBytes());

		printout = new DataOutputStream(urlConn.getOutputStream());
		// printout.writeBytes(content);
		printout.write(bos.toByteArray());
		printout.flush();
		printout.close();
		// Get response data.

		// input = new DataInputStream (urlConn.getInputStream ());

		if (urlConn.getResponseCode() == 400 || urlConn.getResponseCode() == 500) {
			input = new DataInputStream(urlConn.getErrorStream());
		} else {
			input = new DataInputStream(urlConn.getInputStream());
		}

		String str;
		while (null != ((str = input.readLine()))) {
			System.out.println(str);
		}
		input.close();

	}

	public static String getBoundaryString() {
		return BOUNDARY;
	}

	public static String getBoundaryMessage(String boundary, String fileField, String fileName, String fileType) {
		StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");
		res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"")
				.append(fileName).append("\"\r\n").append("Content-Type: ").append(fileType).append("\r\n\r\n");

		return res.toString();
	}

}