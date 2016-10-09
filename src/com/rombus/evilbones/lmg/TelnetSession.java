package com.rombus.evilbones.lmg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Platform;

/**
 * Class responsible for connecting/disconnecting to the melted server, and issuing the requested commands.
 *
 * @author rombus
 *
 * 23/07/2016 11:33:51
 */
public class TelnetSession {

    private int port;
    private String hostName;
    private Socket meltedServer = null;
    private PrintWriter send = null;
    private BufferedReader receive = null;
    private SessionListener listener;
    private Thread listenerThread;

    public TelnetSession(String server, int port, I_SessionNotifier notifier) throws UnknownHostException, IOException {
        this.port = port;
        this.hostName = server;
        connect(notifier);
    }

    public void connect(I_SessionNotifier notifier) throws UnknownHostException, IOException {
        this.meltedServer = new Socket(this.hostName, this.port);

        this.send = new PrintWriter(meltedServer.getOutputStream(), true);
        this.receive = new BufferedReader(new InputStreamReader(meltedServer.getInputStream()));
        this.listener = new SessionListener(receive, notifier);
        this.listenerThread = new Thread(listener);
        this.listenerThread.start();
    }

    public void disconnect() {
        try {
            this.listener.stopListening();
            this.listenerThread.interrupt();
            this.send.close();
            this.meltedServer.close();
        } catch (NullPointerException e) {
        } catch (IOException e) {
        }
    }

    public void sendCommand(final String command) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                send.println(command);
            }
        });
    }
}
