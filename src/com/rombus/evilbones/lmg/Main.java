package com.rombus.evilbones.lmg;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.rombus.evilbones.lmg.I_SessionNotifier.Commands;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Simple GUI for issuing commands to a melted server over Telnet.
 * Works with the default preview unit "U0".
 * 
 * @author rombus
 *
 * 23/07/2016 14:13:39
 */
public class Main extends Application implements Initializable {
	@FXML private TextField hostnameInput;
	@FXML private TextField portInput;	
	@FXML private TextField producerInput;
	@FXML private TextArea cmdInput;
	@FXML private TextArea outputText;
	
	@FXML private Button connectBtn;
	@FXML private Button disconnectBtn;
	@FXML private Button loadBtn;
	@FXML private Button apndBtn;
	@FXML private Button playBtn;
	@FXML private Button pauseBtn;
	@FXML private Button stopBtn;
	@FXML private Button rewBtn;
	@FXML private Button ffBtn;
	@FXML private Button listBtn;
	@FXML private Button ustaBtn;
	@FXML private Button cleanBtn;
	@FXML private Button wipeBtn;
	@FXML private Button cleanLogBtn;
	@FXML private Button sendCmdBtn;
	@FXML private Button clearCmdBtn;
	@FXML private Button shutdownBtn;
	@FXML private Button removeBtn;
	
	private String unit = "U0"; // Default unit
	private TelnetSession session;
	private I_SessionNotifier notifier;
	
	
	//Start javafx app
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox page = (VBox) FXMLLoader.load(Main.class.getResource("/assets/gui.fxml"));
		Scene scene = new Scene(page);		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Limited Melted GUI");
		primaryStage.titleProperty().set("Limited Melted GUI");
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/assets/ricon128.png")));
		primaryStage.show();
		
		// Kill all threads before exit
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	       @Override
	       public void handle(WindowEvent e) {
	          Platform.exit();
	          System.exit(0);
	       }
	    });
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		notifier = new SessionNotifier(outputText);
		outputText.setStyle("-fx-font-family: monospace");
		
		setDragDrop(producerInput, true);
		setDragDrop(cmdInput, false);		
		
		connectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String hostname = hostnameInput.getText().trim();
				int port = Integer.parseInt(portInput.getText().trim());
								
				try {
					session = new TelnetSession(hostname, port, notifier);
				} catch (IOException e) {
					notifier.writeOutput("Server unavailable. Connection not established!!!");
				}
			}
		});
		
		disconnectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("BYE");
				session.disconnect();
				session = null;
			}
		});
		
		loadBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("LOAD "+unit+" "+producerInput.getText().trim());
			}
		});
		
		apndBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("APND "+unit+" "+producerInput.getText().trim());
			}
		});
		
		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("PLAY "+unit);
			}
		});
		
		stopBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("STOP "+unit);
			}
		});
		
		pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("PAUSE "+unit);
			}
		});
		
		stopBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("STOP "+unit);
			}
		});
		
		rewBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("REW "+unit);
			}
		});
		
		listBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("LIST "+unit, Commands.LIST);
			}
		});
		
		ffBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("FF "+unit);
			}
		});
		
		ustaBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("USTA "+unit, Commands.USTA);
			}
		});
		
		cleanBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("CLEAN "+unit);
			}
		});
		
		shutdownBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {				
				sendCommand("SHUTDOWN");
				session.disconnect();
			}
		});
		
		wipeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("WIPE "+unit);
			}
		});
		
		removeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand("REMOVE "+unit);
			}
		});
		
		cleanLogBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				outputText.clear();
			}
		});
		
		sendCmdBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				sendCommand(cmdInput.getText().trim());
			}
		});
		
		clearCmdBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				cmdInput.clear();
			}
		});
	}
	
	// This is a helper method to check for connection to a melted server before sending a command.
	private void sendCommand(String command){
		try {
			this.session.sendCommand(command);
			notifier.setSentMsg(Commands.NO_CMD); // Por defecto mando NO_CMD
			
			notifier.writeOutput(">> "+command);
		} catch (NullPointerException e) {
			notifier.writeOutput("Not connected to melted!!!");
		}
	}
	private void sendCommand(String command, Commands cmd){
		try {
			this.session.sendCommand(command);
			notifier.setSentMsg(cmd);
			
			notifier.writeOutput(">> "+command);
		} catch (NullPointerException e) {
			notifier.writeOutput("Not connected to melted!!!");
		}
	}
	
	
	
	// Helper method for activating drag&drop on TextInputControl components.
	private void setDragDrop(final TextInputControl target, final boolean erasePreviousText){
		// Para el drag & drop en la barra de producer
		target.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
			}
		});
		target.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        if(erasePreviousText){
                        	target.setText(filePath);
                        }
                        else {
                        	target.appendText(" "+filePath);
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
			}
		});
	}
}
