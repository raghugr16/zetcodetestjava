package org.raghu.zetcodetest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class HttpPostMultipart {
	
	private static final String LINE = "\r\n";
	private String boundary;
	private HttpURLConnection httpConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;
	private File file;
	private PrintWriter filePrintWriter;
	
	
	public HttpPostMultipart(String requestURL, String charSet, Map<String,String> headers) throws IOException, MalformedURLException {
		this.charset = charSet;
		boundary = UUID.randomUUID().toString();
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		//httpConn.setRequestProperty("Accept", "text/plain");
		if(headers != null && headers.size() > 0) {
			Iterator<String> it = headers.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = headers.get(key);
				httpConn.addRequestProperty(key, value);
			}
		}
		file = new File("/Users/raghavendragr/Desktop/Raghu/InputFile/text.txt");
		outputStream = httpConn.getOutputStream();
		filePrintWriter = new PrintWriter(file);
		writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
	}
	
	/*
	 * 
	 */
	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE);
		writer.append("Content-Type: text/plain; charset=" +charset).append(LINE);
		writer.append(LINE);
		writer.append(value).append(LINE);
		writer.flush();
		
		filePrintWriter.append("--" + boundary).append(LINE);
		filePrintWriter.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE);
		filePrintWriter.append("Content-Type: text/plain; charset=" +charset).append(LINE);
		filePrintWriter.append(LINE);
		filePrintWriter.append(value).append(LINE);
		filePrintWriter.flush();
	}
	
	/*
	 * 
	 * 
	 */
	public void addFilePart(String fieldName, File uploadFile) throws IOException{
		String fileName = uploadFile.getName().trim();
		writer.append("--" + boundary).append(LINE);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE);
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
		writer.append("Content-Transfer-Encoding: binary").append(LINE);
		writer.append(LINE);
		writer.flush();
		
		filePrintWriter.append("--" + boundary).append(LINE);
		filePrintWriter.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE);
		filePrintWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
		filePrintWriter.append("Content-Transfer-Encoding: binary").append(LINE);
		filePrintWriter.append(LINE);
		filePrintWriter.flush();
		
		FileInputStream fileInputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int byteReads = -1;
		while((byteReads = fileInputStream.read(buffer)) != -1) {
			outputStream.write(buffer,0,byteReads);
		}
		outputStream.flush();
		fileInputStream.close();
		
		writer.append(LINE);
		writer.flush();
		
		filePrintWriter.append(LINE);
		filePrintWriter.flush();
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	
	public String finish() throws IOException{
		String response = "";
        writer.flush();
        writer.append("--" + boundary + "--").append(LINE);
        writer.close();
        
        filePrintWriter.flush();
        filePrintWriter.append("--" + boundary + "--").append(LINE);
        filePrintWriter.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        switch(status) {
        
        case HttpURLConnection.HTTP_OK:
        case HttpURLConnection.HTTP_ACCEPTED:
									        	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
									            byte[] buffer = new byte[1024];
									            int length;
									            while ((length = httpConn.getInputStream().read(buffer)) != -1) {
									                byteArrayOutputStream.write(buffer, 0, length);
									            }
									            response = byteArrayOutputStream.toString(this.charset);
									            httpConn.disconnect();
									            break;
        
        case HttpURLConnection.HTTP_NOT_FOUND:  BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
											  	StringBuffer result = new StringBuffer();
											  	String responseLine = null;
											  	while ((responseLine = br.readLine()) != null) {
											  		result.append(responseLine.trim());
											  	}
											  	br.close();
											  	return result.toString();  
        											
        }
        return response;
	}
	
}


/**
 * 
 *  
 *  POST /index HTTP/1.1
	Host: localhost
	User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36
	Content-Type: multipart/form-data; boundary=--eab528da-8c70-424d-8746-c18ee89f036c
	
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="username"
	Content-Type: text/plain; charset=utf-8
	
	test_name
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="password"
	Content-Type: text/plain; charset=utf-8
	
	test_psw
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="imgFile"; filename="test.png"
	Content-Type: image/png
	Content-Transfer-Encoding: binary
	
	(data)
	--eab528da-8c70-424d-8746-c18ee89f036c--
 * 
 */
