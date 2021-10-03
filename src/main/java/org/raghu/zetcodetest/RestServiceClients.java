package org.raghu.zetcodetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 * @author raghavendragr
 * 
 *
 */
public class RestServiceClients {
	
private static final String USER_AGENT = "Mozilla/5.0";
	
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	private static Scanner scanner = new Scanner(System.in);
	
	
	private String username = null;
	private String password = null;
	private String authToken = null;
	private boolean IsAuthProvided = false;
	
	private URL url =  null;
	private HttpURLConnection connection = null;
	
	
	public RestServiceClients() {
	}
	
	public RestServiceClients(String urlString) throws MalformedURLException {
		this.url = new URL(urlString);
	}

	/**
	 *  
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		scanner = new Scanner(System.in);
		String url = scanner.next().trim();
		RestServiceClients  restServiceClients = new RestServiceClients(url);
		restServiceClients.connection = (HttpURLConnection) restServiceClients.url.openConnection();
		
		System.out.println("Is Authentication needed");
		
		restServiceClients.IsAuthProvided = scanner.nextBoolean();
		
		if(restServiceClients.IsAuthProvided) {
			
			System.out.println("Provide below option");
			System.out.println("Enter 1 -> Basic Authentication");
			System.out.println("Enter 2 -> Authorization Token");
			
			int authChoice = scanner.nextInt();
			
			switch(authChoice) {
				case 1:  System.out.println("Enter the username");
						 restServiceClients.username = scanner.next();
						 System.out.println("Enter the password");
						 restServiceClients.password = scanner.next();
						 String auth = restServiceClients.username + ":" + restServiceClients.password;
						 byte encodedAuth[] = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
						 String authHeaderValue = "Basic" + new String(encodedAuth);
						 restServiceClients.connection.setRequestProperty(HEADER_AUTHORIZATION, authHeaderValue);
						 break;
				
				case 2:	System.out.println("Enter the Auth Token");
				
						restServiceClients.authToken = BEARER + scanner.next();
						restServiceClients.connection.setRequestProperty(HEADER_AUTHORIZATION, restServiceClients.authToken);
						break;
						
				default: System.out.println("Invalid Input "+ authChoice +" Unable to proceed");;
			}
		}

		String requestType = scanner.next().trim();
		
		Map<String,String> requestParameter = new HashMap<>();
		int num_of_paramerter = scanner.nextInt();

		while(num_of_paramerter-- > 0) {
			String key = scanner.next();
			String value = scanner.next();
			if(key == null) {
				System.out.println("Key is null");
				return;
			}
			if(key.isEmpty()) {
				System.out.println("Key is empty");
				return;
			}
			requestParameter.put(key,value);
		}
		
		String result = null;
		
		switch(requestType) {
			
			case "GET" : 	result = restServiceClients.getRequest(url,requestType);
						 	System.out.println("Result is given below \n" + result);
						 	break;
						 	
			case "POST" :  	result = restServiceClients.postRequest(url,requestType,requestParameter);
							System.out.println("Result is given below \n" + result);
						 	break;
			case "FILE" :   String charSet = "utf-8";
							HttpPostMultipart httpPostMultipart = null;
							try {
								  httpPostMultipart = new HttpPostMultipart(url, charSet, requestParameter);
								  System.out.println("Enter the number of form paramerters");
								  int formparameters = scanner.nextInt();
								  while(formparameters-- > 0) {
									  System.out.println("Enter the name of the paramerter");
									  String name = scanner.next().trim();
									  System.out.println("Entrt the value of the parameter");
									  String value = scanner.next().trim();
									  httpPostMultipart.addFormField(name, value);
								  }
								  System.out.println("Enter number of file uploads");
								  int numberOfFileUpload = scanner.nextInt();
								  while(numberOfFileUpload-- > 0) {
									  System.out.println("enter the file path");
									  String path = scanner.next().trim();
									  File file = new File(path);
									  System.out.println("Enter the field name");
									  String fieldname = scanner.next().trim();
									  httpPostMultipart.addFilePart(fieldname, file);
								  }
								  result = httpPostMultipart.finish();
								  System.out.println("Result is given below \n" + result);
								  
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
			
			case "DELETE":  break;
			
			case "PUT":     break;
			default: throw new Exception();
		}
	}
	
	// http://localhost:8080/cities
	public String getRequest(String urlString, String requestType) {
		
		try {
			 connection = (HttpURLConnection) url.openConnection();
			 connection.setRequestMethod(requestType);
			 connection.setInstanceFollowRedirects(false);
			 connection.setRequestProperty("Content-length", "0");
			 connection.setUseCaches(false);
			 connection.setAllowUserInteraction(false);
			 connection.setConnectTimeout(1000);
			 connection.setReadTimeout(10000);
			 connection.setRequestProperty("accept", "application/json");
			 connection.setRequestMethod("POST");
			 int status = connection.getResponseCode();
			 BufferedReader br = null;
			 StringBuilder result = new StringBuilder();
			 String line = null;
			 
			 switch(status) {
			 case 200:
			 case 201: br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 		   while((line = br.readLine()) != null) {
			 			   result.append(line + "\n");
			 		   }
			 		   br.close();
			 		   return result.toString();
			 case 404: br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			 		   while((line = br.readLine()) != null) {
			 			   result.append(line + "\n");
			 		   }
			 		   br.close();
			 		   return result.toString();
			 		   
			 }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String postRequest(String urlString, String requestType, Map<String,String> params) {

		try {
			  connection = (HttpURLConnection) url.openConnection();
			  connection.setRequestMethod(requestType);
			  connection.setRequestProperty("User-Agent", USER_AGENT);
			  connection.setRequestProperty("Content-Type", "application/json; utf-8");
			  connection.setDoOutput(true);
			  OutputStream out = connection.getOutputStream();
			  ObjectMapper objectMapper = new ObjectMapper();
			  String postParams = objectMapper.writeValueAsString(params);
			  byte[] paramBytes = postParams.getBytes();
			  out.write(paramBytes, 0, paramBytes.length);
			  out.flush();
			  out.close();
			  
			  int responseCode = connection.getResponseCode();
			  System.out.println("POST Response Code " + responseCode);
			  if(responseCode == HttpURLConnection.HTTP_OK) {
				  BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				 BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }
			  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String fileUploadRequest(String urlString, String requestType, Map<String,String> params) {
		try {
			  connection = (HttpURLConnection) url.openConnection();
			  connection.setRequestMethod(requestType);
			  connection.setRequestProperty("User-Agent", USER_AGENT);
			  connection.setRequestProperty("Content-Type", "application/json; utf-8");
			  connection.setDoOutput(true);
			  OutputStream out = connection.getOutputStream();
			  ObjectMapper objectMapper = new ObjectMapper();
			  String postParams = objectMapper.writeValueAsString(params);
			  byte[] paramBytes = postParams.getBytes();
			  out.write(paramBytes, 0, paramBytes.length);
			  out.flush();
			  out.close();
			  
			  int responseCode = connection.getResponseCode();
			  System.out.println("POST Response Code " + responseCode);
			  if(responseCode == HttpURLConnection.HTTP_OK) {
				  BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				 BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }
			  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String deleteRequest(String urlString, String requestType, Map<String,String> params) {
		try {
			  connection = (HttpURLConnection) url.openConnection();
			  connection.setRequestMethod(requestType);
			  connection.setRequestProperty("User-Agent", USER_AGENT);
			  connection.setRequestProperty("Content-Type", "application/json; utf-8");
			  connection.setDoOutput(true);
			  OutputStream out = connection.getOutputStream();
			  ObjectMapper objectMapper = new ObjectMapper();
			  String postParams = objectMapper.writeValueAsString(params);
			  byte[] paramBytes = postParams.getBytes();
			  out.write(paramBytes, 0, paramBytes.length);
			  out.flush();
			  out.close();
			  
			  int responseCode = connection.getResponseCode();
			  System.out.println("POST Response Code " + responseCode);
			  if(responseCode == HttpURLConnection.HTTP_OK) {
				  BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				 BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				  StringBuffer result = new StringBuffer();
				  String responseLine = null;
				  while ((responseLine = br.readLine()) != null) {
					  result.append(responseLine.trim());
				  }
				  br.close();
				  return result.toString();
			 }
			  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static Scanner getScanner() {
		return scanner;
	}

	public static void setScanner(Scanner scanner) {
		RestServiceClients.scanner = scanner;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public boolean isIsAuthProvided() {
		return IsAuthProvided;
	}

	public void setIsAuthProvided(boolean isAuthProvided) {
		IsAuthProvided = isAuthProvided;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public HttpURLConnection getConnection() {
		return connection;
	}

	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}

	public static String getUserAgent() {
		return USER_AGENT;
	}
	
	
}
