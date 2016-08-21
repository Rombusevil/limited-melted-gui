package com.rombus.evilbones.lmg;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Thread that listens to melted responses and prints 
 * them through a SessionNotifier interface.
 * 
 * @author rombus
 *
 * 23/07/2016 12:18:21
 */
public class SessionListener implements Runnable {
	private boolean keepListening;
	private I_SessionNotifier notifier;
	private BufferedReader socketIn;
	
	public SessionListener(BufferedReader socketIn, I_SessionNotifier notifier){
		this.keepListening = true;
		this.socketIn = socketIn;
		this.notifier = notifier;
	}
	
	public void stopListening() throws IOException{
		this.keepListening = false;
	}
	
	@Override
	public void run() {
		while(keepListening){
        	try {
        		String result = socketIn.readLine();        		
        		if(result != null){
        			notifier.writeOutput(result);
        		}
			} catch (IOException e1) {}
        	
        	try { 
        		Thread.sleep(5); 
        	} catch (InterruptedException e) { 
        		return;
        	}
        }
		
		try {
			socketIn.close();
		} catch (IOException e) { }
	}
}
