

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 8889;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/view";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static ApplicantManager applicantManager;
    private static Map<String, Integer> loggedUsers = new HashMap<>();

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (connection != null) {
                    System.out.println("Connected to the database successfully!");
                    applicantManager = new ApplicantManager(connection);
                    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                        System.out.println("Server is listening on port " + PORT);

                        while (true) {
                            Socket socket = serverSocket.accept();
                            System.out.println("New client connected");
                            new ClientHandler(socket, applicantManager).start();
                        }
                    }
                } else {
                    System.out.println("Failed to connect to the database.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (IOException | SQLException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private ApplicantManager applicantManager;

        public ClientHandler(Socket socket, ApplicantManager applicantManager) {
            this.socket = socket;
            this.applicantManager = applicantManager;
        }

        public void run() {
            try (
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                String command;
                while ((command = (String) input.readObject()) != null) {
                    System.out.println("Received command: " + command);
                    String response = handleCommand(command);
                    System.out.println("Sending response: " + response);
                    output.writeObject(response);
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client handler exception: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private String handleCommand(String command) {
            String[] parts = command.split("\\s+");
            String action = parts[0];

            switch (action) {
                case "login":
                    if (parts.length == 3) {
                        String username = parts[1];
                        int regno;
                        try {
                            regno = Integer.parseInt(parts[2].trim());
                        } catch (NumberFormatException e) {
                            return "Invalid registration number format.";
                        }
                        loggedUsers.put(username, regno);
                        return applicantManager.login(username, regno);
                    } else {
                        return "Usage: login <username> <registration_number>";
                    }
                case "viewApplicants":
                    return applicantManager.viewApplicants();
                case "confirm":
                    if (parts.length == 3) {
                        String decision = parts[1];
                        String applicantUsername = parts[2];
                        int loggedRegno = loggedUsers.values().iterator().next();
                        return applicantManager.confirmApplicant(decision, applicantUsername, loggedRegno);
                    } else {
                        return "Usage: confirm <yes/no> <username>";
                    }
                default:
                    return "Unknown command: " + command;
            }
        }
    }
}


