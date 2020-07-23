package simulator;

import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	private static Logger log = Logger.getLogger("Server");
	private int port;
	private static Properties prop = new Properties();
	private static final String config = "responseMap.properties";
	
	public Server (int port) {
		this.port = port;
		loadConfig();
		onInit();
	};
	
	public void loadConfig() {
		String fileName = config;
		InputStream is = null;
		try {
		    is = getClass().getClassLoader().getResourceAsStream(fileName);
		} catch (Exception ex) {
			log.error("Config file: responseMap.properties is not found");
		}
		try {
		    prop.load(is);
		    log.info("Properties loaded, size: " + prop.size());
		    Set<Object> uriList = prop.keySet();
		    for (Object o: uriList) {
		    	String uri = (String) o;
		    	String value = prop.getProperty(uri);
		    	log.info("Property pair - key: " + uri + ", value: " + value);
		    }
		} catch (IOException ex) {
			log.error("Error loading config file.");
		} finally {
			close(is);
		}
	}
	
	private void onInit() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
	        server.createContext("/", new RequestHandler());
	        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
	        server.start();
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	
	private class RequestHandler implements HttpHandler {
		
        public void handle(HttpExchange t) {
        	InputStream is = null;
        	OutputStream os = null;
        	try {
	            String uri = t.getRequestURI().getPath();
        		log.info("Request URI: " + uri);
	            log.info("Request method: " + t.getRequestMethod());
	            is = t.getRequestBody();
	            String body = IOUtils.toString(is, StandardCharsets.UTF_8);
	            log.info("Request body: " + body);
	        	
	            boolean uriFlag = checkIfContainsUri(uri);
	            log.info("Properties contains URI: " + uriFlag);
	            
	            if (uriFlag) {
		        	String fileName = prop.getProperty(uri);
		        	log.info("Response document get: " + fileName);
		        	byte[] output = readFileToByteArray(fileName);
		        	log.info("Response document length: " + output.length);
		            t.sendResponseHeaders(200, output.length);
		            os = t.getResponseBody();
		            os.write(output);
	            } else {
	            	String response = "Invalid Request, URI path is not found.";
		            t.sendResponseHeaders(404, response.length());
		            os = t.getResponseBody();
		            os.write(response.getBytes());
	            }
	            
        	} catch (IOException ioe) {
        		log.error(ioe.toString());
        	} finally {
        		close(is);
        	    close(os);
        	}
        }
	}
	
	private boolean checkIfContainsUri(String uri) {
		boolean result = false;
		if (!prop.isEmpty() && prop.containsKey(uri)) {
			result = true;
		}
		
		return result;
	}
	
	private byte[] readFileToByteArray(String fileName) {
		byte[] result = null;
		InputStream is = null;
		try {
			is = getClass().getClassLoader().getResourceAsStream(fileName);
			result = IOUtils.toByteArray(is);
		} catch (IOException ioe) {
    		log.error("IO Exception: " + ioe.getMessage());
    	} finally {
    		close(is);
    	}
		return result;
	}
	
	private static void close(Closeable c) {
	     if (c == null) return; 
	     try {
	         c.close();
	     } catch (IOException e) {
	    	 log.error(e.toString());
	     }
	  }
}
