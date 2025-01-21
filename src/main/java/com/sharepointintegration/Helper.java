

package  com.sharepointintegration;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;

import java.util.concurrent.ExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Set;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.SilentParameters;

public class Helper {

 
	
	private static final String CLIENT_ID = "";
    private static final String CERTIFICATE_PATH = ",,,,,.pfx"; 
    private static final String CERTIFICATE_PASSWORD = "";   
	private final static String AUTHORITY = "https://login.microsoftonline.com/,,,,,,,,,,,,,,/";	
	private final static Set<String> SCOPE = Collections.singleton("https://,,,,,,,,.sharepoint.com/.default");
	 
	

    public String getAccessToken() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException{
		File file = new File(CERTIFICATE_PATH);
		InputStream pkcs12Certificate = new FileInputStream(file); /* Containing PCKS12-formatted certificate*/
		
		IClientCredential credential = ClientCredentialFactory.createFromCertificate(pkcs12Certificate, CERTIFICATE_PASSWORD);
	
		ConfidentialClientApplication cca =
		        ConfidentialClientApplication
		                .builder(CLIENT_ID, credential)
		                .authority(AUTHORITY)
		                .build();
		
		IAuthenticationResult result;
	     try {
	         SilentParameters silentParameters =
	                 SilentParameters
	                         .builder(SCOPE)
	                         .build();

	       
	         result = cca.acquireTokenSilently(silentParameters).join();
	     } catch (Exception ex) {
	         if (ex.getCause() instanceof MsalException) {

	             ClientCredentialParameters parameters =
	                     ClientCredentialParameters
	                             .builder(SCOPE)
	                             .build();

	            
	             result = cca.acquireToken(parameters).join();
	         } else {
	             
	             throw ex;
	         }
	     }
	   
	     return result.accessToken();
	}
	
	
	public static String storeFile(File file , String accessToken, String fileName) {
		
		
		try {
			
			 HttpPost postRequest = new HttpPost("https://,,,,,,,,,,.sharepoint.com/sites/devcenter/_api/web/GetFolderByServerRelativeUrl('/sites/devcenter/DocumentStore')/Files/add(url='"+fileName+"',overwrite=true)");
			 postRequest.addHeader("Accept", "application/json;odata=verbose");
			 postRequest.setHeader("Authorization", "Bearer " + accessToken);
			 postRequest.setEntity(new FileEntity(file));
			 HttpClient client =HttpClientBuilder.create().build();// new DefaultHttpClient();
			 HttpResponse response = client.execute(postRequest);
			 String responseBody = EntityUtils.toString(response.getEntity());
			 
			 String msg="";
			 if(responseBody.toUpperCase().contains("error")) {
				 msg ="Error";
			 }else {
				 msg =fileName;
			 }
			 return msg;
				
			
		}catch(Exception ex) {
			 System.out.println(ex.toString());
			 return "Error";
		}
			
	}

	
	public static  InputStream retreiveFile (String filname, String accessToken) {
		
			try {
				 HttpGet postRequest = new HttpGet("https://,,,,,,,,.sharepoint.com/sites/devcenter/_api/web/GetFolderByServerRelativeUrl('/sites/devcenter/DocumentStore')/Files('"+filname+"')/$value");
				 postRequest.setHeader("Authorization", "Bearer " + accessToken);
			     HttpClient client =HttpClientBuilder.create().build();
			     HttpResponse response = client.execute(postRequest);
				 
			     if(response.getEntity().getContent() != null) {
				 return response.getEntity().getContent();
			     }else {
			    	 return null; 
			     }
		          
				
			}catch(Exception ex) {
				 System.out.println(ex.toString());
				 return null;
			}
			
			
		}
	

}
	
