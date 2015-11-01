package com.rands.couponproject.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
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
@WebServlet(name = "UploadDownloadServlet", urlPatterns = { "/uploads/*" }
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
 * the method should be POST / PUT
 */
public class UploadDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 282281710399709683L;

	static Logger logger = Logger.getLogger(UploadDownloadServlet.class);

	String uploadDir; // = "C://Users/me/upload/";
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException{
		super.init(servletConfig);
		
		uploadDir = servletConfig.getInitParameter("uploadDir");
	    logger.debug("uploadDir = " + uploadDir);
	  }

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		download(request,response);
    }
    
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		upload(request,response);
	}
	
	private File getUploadDir(HttpServletRequest request,boolean create) {
		uploadDir = getInitParameter("uploadDir");
	    logger.debug("uploadDir = " + uploadDir);
	    
		// gets absolute path of the web application
        String applicationPath = request.getServletContext().getRealPath("");
        logger.debug("applicationPath = " + applicationPath);
        
        // constructs path of the directory to save uploaded file
        String uploadPath;
        if (isAbsolute(uploadDir)) {
        	uploadPath = uploadDir;
        } else {
        	if (isEmpty(uploadDir))
            	uploadPath = applicationPath;
            else
            	//uploadFilePath = applicationPath + File.separator + uploadDir;
            	uploadPath = applicationPath + uploadDir;
        }
          
        // creates the directory if it does not exists
        File fDir = new File(uploadPath);
        if (create && !fDir.exists()) {
            fDir.mkdirs();
        }
        logger.debug("Upload Directory = " + fDir.getAbsolutePath());
        return fDir;
	}

	public void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get the "content" part and save it.
		String partName = "file";
		Part part = request.getPart(partName);
		if (null == part) {
			logger.error("Multipart header named " + partName + " dose not exist");
			throw new ServletException("Multipart header named " + partName + " dose not exist");
		}
		
		String saveAs = getPartData(request.getPart("saveas"));
		
		String name = part.getName();
		String fileName = part.getSubmittedFileName();
		String contentType = part.getContentType();
		
		logger.debug("part name=" + name + " context type=" + contentType + " SubmittedFileName=" + fileName);

        String uploadFilePath = getUploadDir(request, true).getAbsolutePath();

        // save the file
		//part.write(uploadDir + fileName);
        String filePath = uploadFilePath + File.separator + (isEmpty(saveAs) ? fileName : saveAs);        
        logger.debug("Uploading : " + filePath);
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

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    protected void download(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Get requested file by path info.
        String requestedFile = request.getPathInfo();

        // Check if file is actually supplied to the request URI.
        if (requestedFile == null) {
            logger.error("no file was requested");
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
        
        File uploadDir = getUploadDir(request, false);
        // Decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(uploadDir, URLDecoder.decode(requestedFile, "UTF-8"));
        logger.debug("Downloading : " + file.getAbsolutePath());

        // Check if file actually exists in filesystem.
        if (!file.exists()) {
            logger.error("file = " + file.getAbsolutePath() + " does not exist");
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Get content type by filename.
        String contentType = getServletContext().getMimeType(file.getName());

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Init servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

		try {
			// Open streams.
			input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			} 
		} catch (Exception e) {
			logger.error("download of " + requestedFile + " failed : " + e.toString());
		} finally {
			close(output);
			close(input);
		}
    }

	private static void close(Closeable resource) {
		if (null == resource)
			return;
		try {
			resource.close();
		} catch (IOException e) {
			logger.error("failed to close resource : " + e.toString());
		}
	}	
}