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
	private int msgCounter; // Helper para interpretar respuestas que tienen más de un mensaje
	
	public SessionNotifier(TextArea outputText){
		this.outputText = outputText;
		this.executedCommand = Commands.NO_CMD;
		this.msgCounter = 0;
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
				
				
				switch(executedCommand){
				case USTA:
					// Muestro el mensaje de OK
					if(msg.contains(" OK")){
						outputText.appendText(msg+"\n");
					}
					// Proceso la respuesta del USTA
					else {
						String msgs[] = msg.split(" ");
						
						if(msgs.length == 17) {	//Valido que haya sido OK la respuesta, si no tiene todos estos campos fue fail
							outputText.appendText("\n");
							//outputText.appendText("UNIT: "+msgs[0]+"\n"); // No lo muestro pq ya se que es la U0
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
							System.out.println("else");
							outputText.appendText(msg+"\n");		
						}
					}
					break;
				
				case LIST:
					// Muestro el mensaje de OK
					if(msg.contains(" OK")){
						outputText.appendText(msg+"\n");
						msgCounter = 0;
					}
					// Proceso la respuesta del USTA
					else if (msgCounter == 0){
						msgCounter++;
						outputText.appendText("\n");
						outputText.appendText("Modificaciones a la playlist: "+msg+"\n\n");
					}
					else {
						msgCounter++;	// Son multiples mensajes que tengo que procesar
						String msgs[] = msg.split(" ");
						
						if(msgs.length == 7) {	//Valido que haya sido OK la respuesta, si no tiene todos estos campos fue fail
							outputText.appendText("--------------------\n");
							outputText.appendText("  clip index:\t\t"+msgs[0]+"\n"); // No lo muestro pq ya se que es la U0
							outputText.appendText("  path:\t\t\t"+msgs[1]+"\n");
							outputText.appendText("  in-point:\t\t"+msgs[2]+"\n");
							outputText.appendText("  out-point:\t\t"+msgs[3]+"\n");
							outputText.appendText("  length:\t\t"+msgs[4]+"\n");
							outputText.appendText("  calculated len:\t"+msgs[5]+"\n");
							outputText.appendText("  fps:\t\t\t"+msgs[6]+"\n");
						}
						else{
							outputText.appendText(msg+"\n");		
						}
					}
					break;
					
				default:
					outputText.appendText(msg+"\n");
				}	
			}
		});		
	}

	
	@Override
	public void setSentMsg(Commands cmd) {
		this.executedCommand = cmd;
	}
}
