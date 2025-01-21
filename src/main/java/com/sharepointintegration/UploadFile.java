
package  com.sharepointintegration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;



/**
 * Servlet implementation class UploadFile
 */
@WebServlet("/UploadFile")
@MultipartConfig(
        location ="C:\\FileUpload",
        fileSizeThreshold=1024*1024, //1mb
        maxFileSize =1024*1024*10, //10mb
        maxRequestSize=1024*1024*11 //11mb

)

public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	private final int ARBITARY_SIZE =  1024 * 100;;
	
    public UploadFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String mySelect = request.getParameter("mySelect");
		
		String message ="";
		if(mySelect.trim().equals("upload")) {
			try{
	        Part part = request.getPart("file");
	        String fileID =java.util.UUID.randomUUID().toString();
	        fileID = fileID.replaceAll("-", "");
	        String fileName =getFileName(part);
	        String fileExtension =fileName;
	       
	        if(fileName.lastIndexOf(".")>1) {  	
	        	fileExtension =fileName.substring(fileName.lastIndexOf(".")+1);
	        }
	        String newFileName ="Test-"+fileID+"."+fileExtension;
	        
	        part.write(newFileName);
	        String tempPath ="C:\\FileUpload\\";
	        File file = new File(tempPath+newFileName);
	        message = setFile(file,"Test-"+fileID+"."+fileExtension);
	      if(message.toLowerCase() !="error") {
	    	  org.apache.commons.io.FileUtils.forceDelete(file);
	      }
	        
	        request.setAttribute("message", message);
		       request.getRequestDispatcher("message.jsp").forward(request, response);
	       
	        }catch(Exception ex) {
	        	System.out.println(ex.toString());
	        }
		}
		
		if(mySelect.trim().equals("search")) {
	        
	        try {
	        	
	        	
	        	String fileIDTxt = request.getParameter("fileIDTxt");
	        	InputStream inputStream =GetFile(fileIDTxt.trim());
	        	if(inputStream != null) {
	        		
	        		File file = new File(fileIDTxt);
	        	    URLConnection connection = file.toURL().openConnection();
	        	    String mimeType = connection.getContentType();
	        	    System.out.println("mimeType "+mimeType);
	        	    
	        		byte[] buffer =inputStream.readAllBytes();
	        		response.setContentType(mimeType);
	 	            response.setHeader("Content-disposition", "attachment; filename="+fileIDTxt);
	 	            response.getOutputStream().write(buffer);
	 	            
	        	}else {
	        		message ="Error";
	        	}
	        	 request.setAttribute("message", message);
			       request.getRequestDispatcher("downloadfile.jsp").forward(request, response);
	        }catch(Exception ex) {
	        	System.out.println(ex.toString());
	        }
	        
		}
	}

	    private String getFileName(Part part){
	        String contentDiposition = part.getHeader("content-disposition");
	        if(!contentDiposition.contains("filename=")){
	            return null;
	        }
	        
	        int bIndex = contentDiposition.indexOf("filename=")+10;
	        int eIndex = contentDiposition.length()-1;
	        
	        return contentDiposition.substring(bIndex,eIndex);
	    }

	    private String setFile(File file, String fileName) {
	    	
	    	try {
	    		Helper helper = new Helper();
	    	String accessToken = helper.getAccessToken();
	    	
	    	System.out.println("Starting");
	    	return Helper.storeFile(file, accessToken, fileName);
	    	
	    	
	    	}catch(Exception ex) {
	    		
	    		System.out.println("Errrrr "+ex.toString());
	    		return "File Not Successfully Uploaded";
	    		
	    	}
	    	
	    }
	    
	    private InputStream GetFile(String fileID) {
	    	InputStream inputStream = null;
	    	try {
	    		Helper helper = new Helper();
		    	String accessToken = helper.getAccessToken();
		    	inputStream = Helper.retreiveFile (fileID,accessToken);
	    	}catch(Exception ex) {
	    		System.out.println(ex.toString());	
	    	}
	    	return inputStream;
	    }
}
