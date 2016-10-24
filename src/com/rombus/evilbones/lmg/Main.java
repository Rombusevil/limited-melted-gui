package com.rombus.evilbones.lmg;

import com.rombus.evilbones.lmg.I_SessionNotifier.Commands;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Simple GUI for issuing commands to a melted server over Telnet.
 *
 * @author rombus
 *
 * 23/07/2016 14:13:39
 */
public class Main extends Application implements Initializable {
    @FXML
    private TabPane tabPane;
    @FXML
    private TextField hostnameInput;
    @FXML
    private TextField portInput;
    @FXML
    private TextField producerInput;
    @FXML
    private TextArea cmdInput;
    @FXML
    private TextArea outputText;
    @FXML
    private ChoiceBox<Integer> unitChoiceBox;

    @FXML
    private Button connectBtn;
    @FXML
    private Button disconnectBtn;
    @FXML
    private Button loadBtn;
    @FXML
    private Button apndBtn;
    @FXML
    private Button playBtn;
    @FXML
    private Button pauseBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button rewBtn;
    @FXML
    private Button ffBtn;
    @FXML
    private Button listBtn;
    @FXML
    private Button ustaBtn;
    @FXML
    private Button cleanBtn;
    @FXML
    private Button wipeBtn;
    @FXML
    private Button cleanLogBtn;
    @FXML
    private Button sendCmdBtn;
    @FXML
    private Button clearCmdBtn;
    @FXML
    private Button shutdownBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private Button statusBtn;
    @FXML
    private Button ulsBtn;
    @FXML
    private ListView<String> recentProducers;
    @FXML
    private ListView<String> recentCommands;

    private static final String VERSION = "v1.0.1";
    private String unit = "U0"; // Default unit
    private TelnetSession session;
    private I_SessionNotifier notifier;
    private Tooltip cleanBtnTooltip, removeBtnTooltip, wipeBtnTooltip;
    private final ObservableList<String> producersList = FXCollections.observableArrayList();
    private final ObservableList<String> commandsList = FXCollections.observableArrayList();

    //Start javafx app
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox page = (VBox) FXMLLoader.load(Main.class.getResource("/assets/gui.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Limited Melted GUI - "+VERSION);
        primaryStage.titleProperty().set("Limited Melted GUI - "+VERSION);
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

        // Pongo como activa la tab de MEDIA
        tabPane.getSelectionModel().select(1);

        // Pongo algunos tooltips
        cleanBtnTooltip = new Tooltip("Removes all but the playing clip.");
        removeBtnTooltip = new Tooltip("Removes a clip from the specified clip index or\nposition relative to the currently playing clip index.");
        wipeBtnTooltip = new Tooltip("Removes all clips before the playing clip.");

        cleanBtn.setTooltip(cleanBtnTooltip);
        removeBtn.setTooltip(removeBtnTooltip);
        wipeBtn.setTooltip(wipeBtnTooltip);

        // Sincronizo la lista de Producers Recientes con la producerList
        recentProducers.setItems(producersList);
        recentProducers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if(event.getClickCount() >= 2){
                    String cur = recentProducers.getSelectionModel().getSelectedItem();
                    producerInput.setText(cur);
                }
            }
        });

        // Sincronizo la lista de Comandos Recientes con la commandsList
        recentCommands.setItems(commandsList);
        recentCommands.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if(event.getClickCount() >= 2){
                    String cur = recentCommands.getSelectionModel().getSelectedItem();
                    cmdInput.setText(cur);
                }
            }
        });

        // Cargo el combo selector de unidad
        unitChoiceBox.setItems(FXCollections.observableArrayList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        unitChoiceBox.setValue(0);
        unitChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>(){
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                unit = "U"+String.valueOf(newValue);
            }
        });


        // Defino los botones
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

        // Intento conectarme ni bien abrís la app
        connectBtn.fire();

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
                String clip = producerInput.getText().trim();
                sendCommand("LOAD " + unit + " " + clip);
                addToRecent(clip);
            }
        });

        apndBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                String clip = producerInput.getText().trim();
                sendCommand("APND " + unit + " " + clip);
                addToRecent(clip);
            }
        });

        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("PLAY " + unit);
            }
        });

        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("STOP " + unit);
            }
        });

        pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("PAUSE " + unit);
            }
        });

        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("STOP " + unit);
            }
        });

        rewBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("REW " + unit);
            }
        });

        listBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("LIST " + unit, Commands.LIST);
            }
        });

        ffBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("FF " + unit);
            }
        });

        ustaBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("USTA " + unit, Commands.USTA);
            }
        });

        cleanBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("CLEAN " + unit);
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
                sendCommand("WIPE " + unit);
            }
        });

        removeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("REMOVE " + unit);
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
                String cmd = cmdInput.getText().trim();
                sendCommand(cmd);
                addToRecentCmd(cmd);
            }
        });

        clearCmdBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                cmdInput.clear();
            }
        });

        statusBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("STATUS");
            }
        });

        ulsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                sendCommand("ULS");
            }
        });
    }

    // This is a helper method to check for connection to a melted server before sending a command.
    private void sendCommand(String command) {
        try {
            this.session.sendCommand(command);
            notifier.setSentMsg(Commands.NO_CMD); // Por defecto mando NO_CMD

            notifier.writeOutput(">> " + command);
        } catch (NullPointerException e) {
            notifier.writeOutput("Not connected to melted!!!");
        }
    }

    private void sendCommand(String command, Commands cmd) {
        try {
            this.session.sendCommand(command);
            notifier.setSentMsg(cmd);

            notifier.writeOutput(">> " + command);
        } catch (NullPointerException e) {
            notifier.writeOutput("Not connected to melted!!!");
        }
    }

    // Helper method for adding producers and commands to the recent list without duplicates
    private void addToRecent(String path){
        if(!producersList.contains(path)){
            producersList.add(path);
        }
    }
    private void addToRecentCmd(String cmd){
        if(!commandsList.contains(cmd)){
            commandsList.add(cmd);
        }
    }


    // Helper method for activating drag&drop on TextInputControl components.
    private void setDragDrop(final TextInputControl target, final boolean erasePreviousText) {
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
                    for (File file : db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        if (erasePreviousText) {
                            target.setText(filePath);
                        } else {
                            target.appendText(" " + filePath);
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
}
