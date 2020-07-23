package simulator;

import org.apache.log4j.Logger;

public class StartServer {
	
	static Logger log = Logger.getLogger("StartServer");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			if(args != null) {
				int port = Integer.valueOf(args[0]);
				new Server(port);
				log.info("Server started on port: " + port);
			} else {
				throw new Exception("Port is not provided. Please provide only port number in start command args.");
			}
		} catch (Exception e) {
			log.error("Error starting server.");
		}
	}

}
