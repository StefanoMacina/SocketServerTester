package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static JTextArea logArea;
    private static JButton startButton;
    private static JButton stopButton;
    private static JLabel statusLabel;
    private static JTextField portField;
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static Thread serverThread;

    public static void main(String[] args) {
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Socket Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(new JLabel("Porta:"));
        portField = new JTextField("5000", 5);
        controlPanel.add(portField);

        startButton = new JButton("Avvia Server");
        stopButton = new JButton("Ferma Server");
        stopButton.setEnabled(false);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        statusLabel = new JLabel("Server fermato");
        statusLabel.setForeground(Color.RED);
        controlPanel.add(statusLabel);

        frame.add(controlPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

        frame.setVisible(true);
    }

    private static void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            logMessage("Errore: porta non valida");
            return;
        }

        final int serverPort = port;
        running.set(true);

        serverThread = new Thread(() -> {
            runServer(serverPort);
        });

        serverThread.start();

        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        statusLabel.setText("Server in esecuzione sulla porta " + port);
        statusLabel.setForeground(Color.GREEN);
        logMessage("Server avviato sulla porta " + port);
    }

    private static void stopServer() {
        running.set(false);
        if (serverThread != null) {
            serverThread.interrupt();
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Server fermato");
        statusLabel.setForeground(Color.RED);
        logMessage("Server fermato");
    }

    private static void logMessage(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private static void runServer(int port) {
        while(running.get()) {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                logMessage("Server in ascolto su " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

                serverSocket.setSoTimeout(1000);

                while(running.get()) {
                    try {
                        Socket socket = serverSocket.accept();
                        logMessage("Connessione accettata da " + socket.getInetAddress());

                        new Thread(() -> handleClient(socket)).start();
                    } catch (java.net.SocketTimeoutException e) {
                        continue;
                    } catch (IOException e) {
                        logMessage("Errore durante l'accettazione della connessione: " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception e) {
                logMessage("Errore del server " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            while(true) {
                String echoString = input.readLine();

                if(echoString == null) {
                    logMessage("Client disconnesso");
                    break;
                }

                if(echoString.equalsIgnoreCase("CB,01,off".trim())) {
                    logMessage("Ricevuto comando di uscita");
                    break;
                }

                logMessage("Ricevuto dal client: " + echoString);
                Random r = new Random();
                Integer num = r.nextInt(1000);
                String response = "%s,%d".formatted(echoString, num);
                output.println(response);
                logMessage("Inviato al client: " + response);
            }
        } catch (IOException e) {
            logMessage("Errore nella comunicazione con il client: " + e.getMessage());
        } finally {
            try {
                socket.close();
                logMessage("""
                        Connessione con il client chiusa
                        --------------------------------
                        """);
            } catch (IOException e) {
                logMessage("Errore durante la chiusura del socket: " + e.getMessage());
            }
        }
    }
}