package com.rands.couponproject.rest.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import com.sun.jersey.core.header.FormDataContentDisposition;
//import com.sun.jersey.multipart.FormDataParam;




import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/files")
public class UploadService {

	private static final String SERVER_UPLOAD_LOCATION_FOLDER = null; // "C:\\\\Users\\oded\\upload\\";
	static Logger logger = Logger.getLogger(UploadService.class);
	
	@Context
	HttpServletRequest request;

	/**
	 * Upload a File
	 */

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) throws Exception {
		
		//String saveAs = getPartData(request.getPart("saveas"));
		String saveAs = null;
		
		String uploadDir = SERVER_UPLOAD_LOCATION_FOLDER;//getInitParameter("uploadDir");
	    logger.debug("uploadDir = " + uploadDir);
	    
		String name = contentDispositionHeader.getName();
		String fileName = contentDispositionHeader.getFileName();
		String contentType = contentDispositionHeader.getType();
		long size = contentDispositionHeader.getSize();
		
		logger.debug("part name=" + name + " context type=" + contentType + " SubmittedFileName=" + fileName + " size=" + size);	    
	    
		// gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        logger.debug("applicationPath = " + applicationPath);	    
		
        // constructs path of the directory to save uploaded file
        String uploadFilePath;
        if (isAbsolute(uploadDir)) {
        	uploadFilePath = uploadDir;
        } else { 
        	if (isEmpty(uploadDir))
            	uploadFilePath = applicationPath;
            else
            	//uploadFilePath = applicationPath + File.separator + uploadDir;
            	uploadFilePath = applicationPath + uploadDir;
        }
          
        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadFilePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        logger.debug("Upload File Directory = " + fileSaveDir.getAbsolutePath());		
		

        // save the file
        String filePath = uploadFilePath + File.separator + (isEmpty(saveAs) ? fileName : saveAs);
        logger.info("saving : " + filePath);

        //part.write(filePath);        
        saveFile(fileInputStream, filePath);
        
        // set response
        String output = "File saved to server location : " + filePath;
        return Response.status(200).entity(output).build();
	}
	
	private boolean isEmpty(String str) {
		return null == str || str.isEmpty();
	}

	private boolean isAbsolute(String pathName) {
		if (isEmpty(pathName))
			return false;
		
		java.nio.file.Path p = Paths.get(pathName); 
    	return p.isAbsolute();
    }
	
	// save uploaded file to a defined location on the server
	private void saveFile(InputStream uploadedInputStream,	String serverLocation) throws Exception{

		OutputStream outpuStream = null;
		try {
			int read = 0;
			byte[] bytes = new byte[1024];

			outpuStream = new FileOutputStream(new File(serverLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
		} catch (IOException e) {
			logger.error("saveFile failed : " + e.toString());
			throw e;
		} finally {
			if (null != outpuStream)
				outpuStream.close();
		}

	}

}
