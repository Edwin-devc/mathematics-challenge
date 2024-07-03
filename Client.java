import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                boolean registered = false;
                while (!registered) {
                    System.out.print("Enter command: ");
                    String input = reader.readLine();
                    String[] tokens = input.split("\\s+");
                    if (tokens.length >= 2) {
                        String command = tokens[0];
                        if (command.equalsIgnoreCase("Register") && tokens.length == 8) {
                            String registerCommand = String.format("REGISTER %s %s %s %s %s %s %s",
                                    tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                            System.out.println("Sending command to server: " + registerCommand);
                            registered = sendCommand(registerCommand);
                        } else if (command.equalsIgnoreCase("ViewChallenges")) {
                            sendCommand("ViewChallenges");
                        } else {
                            System.out.println("Unknown command or incorrect number of arguments.");
                        }
                    } else {
                        System.out.println("Invalid input format.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading input: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            String command = args[0];
            if (command.equalsIgnoreCase("Register")) {
                if (args.length == 8) {
                    String imagePath = args[7];
                    String registerCommand = String.format("REGISTER %s %s %s %s %s %s %s",
                            args[1], args[2], args[3], args[4], args[5], args[6], imagePath.replace(':', '\\'));
                    System.out.println("Sending command to server: " + registerCommand);
                    sendCommand(registerCommand);
                } else {
                    System.out.println("Invalid number of arguments for Register command.");
                    printUsage();
                }
            } else if (command.equalsIgnoreCase("ViewChallenges")) {
                sendCommand("ViewChallenges");
            } else {
                System.out.println("Unknown command: " + command);
                printUsage();
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("Register <username> <firstname> <lastname> <emailAddress> <date_of_birth> <school_registration_number> <image_file.png>");
        System.out.println("ViewChallenges");
    }

    private static boolean sendCommand(String command) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Send command to server
            out.println(command);
            System.out.println("Command sent to server: " + command);

            // Read and print response from server
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server response: " + response);
                if (response.equals("Registration successful")) {
                    return true;
                } else if (response.equals("Username already taken, please choose another one")) {
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
