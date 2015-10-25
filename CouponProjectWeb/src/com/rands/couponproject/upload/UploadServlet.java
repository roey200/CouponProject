package com.rands.couponproject.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

// The @MultipartConfig annotation indicates that the servlet expects requests to made using the multipart/form-data MIME type.
@WebServlet(name = "UploadServlet", urlPatterns = { "/files/upload" }
		, initParams = { @WebInitParam(name = "uploadDir", value = "uploads"), @WebInitParam(name = "myParam2", value = "222") }
)
@MultipartConfig(
		 //location = "/tmp"
		 fileSizeThreshold	= 1024 * 1024 * 10 	// 10 MB 
		//,maxFileSize		= 1024 * 1024 * 50 	// 50 MB
		//,maxRequestSize 	= 1024 * 1024 * 100 // 100 MB
)
/**
 * FileUploadServlet : a servlet for uploading files to the server.
 * it requires servlet 3.1 (java 7).
 * the method shoul be POST 
 * @author oded
 *
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 282281710399709683L;

	static Logger logger = Logger.getLogger(UploadServlet.class);

	String uploadDir; // = "C://Users/oded/upload/";
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException{
		super.init(servletConfig);
		
		uploadDir = servletConfig.getInitParameter("uploadDir");
	    logger.debug("uploadDir = " + uploadDir);
	  }


	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get the "content" part and save it.
		String partName = "file";
		Part part = request.getPart(partName);
		if (null == part) {
			logger.error("Multipart header named " + partName + " dose not exist");
			throw new ServletException("Multipart header named " + partName + " dose not exist");
		}
		
		String saveAs = getPartData(request.getPart("saveas"));
		
		uploadDir = getInitParameter("uploadDir");
	    logger.debug("uploadDir = " + uploadDir);
		
		String name = part.getName();
		String fileName = part.getSubmittedFileName();
		String contentType = part.getContentType();
		
		logger.debug("part name=" + name + " context type=" + contentType + " SubmittedFileName=" + fileName);
		
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
		//part.write(uploadDir + fileName);
        String filePath = uploadFilePath + File.separator + (isEmpty(saveAs) ? fileName : saveAs);        
        logger.info("saving : " + filePath);
        part.write(filePath);
        
        // set response
        final PrintWriter writer = response.getWriter();
        writer.println("<br/> Saved : " + filePath);


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
	
	private String getPartData(Part part) {
		if (null == part)
			return "";
		try {
			InputStream is = part.getInputStream();
			java.util.Scanner scanner = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
			String data = scanner.hasNext() ? scanner.next() : "";
			return data;
		} catch (Exception e) {
			logger.error("getPartData failed, part name = " + part.getName() + " : " + e.toString());
			return "";
		}
	}

}