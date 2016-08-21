package com.rombus.evilbones.lmg;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * @author rombus
 *
 * 20/08/2016 20:51:59
 */
public class SessionNotifier implements I_SessionNotifier{
	private TextArea outputText;
	private I_SessionNotifier.Commands executedCommand;
	
	public SessionNotifier(TextArea outputText){
		this.outputText = outputText;
	}

	@Override
	public void writeOutput(final String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// Si es un mensaje hacia melted, imprimo y salgo.
				if(msg.contains(">>")){
					outputText.appendText(msg+"\n");
					return;
				}
				
				// Si mandó un USTA
				if(executedCommand == Commands.USTA){
					// Muestro el mensaje de OK
					if(msg.contains("OK")){
						outputText.appendText(msg+"\n");
					}
					// Proceso la respuesta del USTA
					else {
						String []msgs = msg.split(" ");
						
						if(msgs.length == 17) {
							//outputText.appendText("UNIT: "+msgs[0]+"\n");
							outputText.appendText("\n");
							outputText.appendText("  mode:\t\t\t"+msgs[1]+"\n");
							outputText.appendText("  path:\t\t\t"+msgs[2]+"\n");
							outputText.appendText("  cur frame:\t\t"+msgs[3]+"\n");
							outputText.appendText("  speed %:\t\t"+msgs[4]+"\n");
							outputText.appendText("  fps:\t\t\t"+msgs[5]+"\n");
							outputText.appendText("  in-point:\t\t"+msgs[6]+"\n");
							outputText.appendText("  out-point:\t\t"+msgs[7]+"\n");
							outputText.appendText("  length:\t\t"+msgs[8]+"\n");
							outputText.appendText("  tail clip name:\t"+msgs[9]+"\n");
							outputText.appendText("  tail pos:\t\t"+msgs[10]+"\n");
							outputText.appendText("  tail in-point:\t"+msgs[11]+"\n");
							outputText.appendText("  tail out-point:\t"+msgs[12]+"\n");
							outputText.appendText("  tail length:\t\t"+msgs[13]+"\n");
							outputText.appendText("  seekable flag:\t"+msgs[14]+"\n");
							outputText.appendText("  playlist num:\t\t"+msgs[15]+"\n");
							outputText.appendText("  clip index:\t\t"+msgs[16]+"\n");
							outputText.appendText("\n");
						}
						else{
							outputText.appendText(msg+"\n");		
						}
						
						executedCommand = null;	
					}
				}
				else {
					outputText.appendText(msg+"\n");
					executedCommand = null;
				}
			}
		});		
	}

	
	@Override
	public void setSentMsg(Commands cmd) {
		this.executedCommand = cmd;
	}
}
