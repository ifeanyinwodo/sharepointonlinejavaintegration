package com.sharepointintegration;

public class FileResponseMdl {
	
	//public String fileName {get; set;}
	
	private String fileName;
	private String fileExtension;
	private byte[] fileContents;
    public String getFileName() {
    	
    	return fileName;    	
    };
    public void setFileName(String N)
    {
    	this.fileName = N;
    }
    
    public String getFileExtension() {
    	
    	return fileExtension;    	
    };
    public void setFileExtension(String N)
    {
    	this.fileExtension = N;
    }
    
    public byte[] getFileContents() {
    	
    	return fileContents;    	
    };
    public void setFileContents(byte[] N)
    {
    	this.fileContents = N;
    }

}
