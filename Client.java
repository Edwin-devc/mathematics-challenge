
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8889;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter login command (login <username> <registration_number): ");
            String loginCommand = scanner.nextLine();
            output.writeObject(loginCommand);
            String response = (String) input.readObject();
            System.out.println("Received response: " + response);

            if (!response.startsWith("Login successful")) {
                System.out.println("Login failed. Exiting...");
                return;
            }

            // Display the menu once after a successful login
            System.out.println("Menu:\n1. ViewApplicants\n2. Confirm Applicant\n3. Exit");

            while (true) {
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        output.writeObject("viewApplicants");
                        response = (String) input.readObject();
                        System.out.println("Received response: " + response);
                        break;
                    case "2":
                        System.out.print("Enter 'confirm yes/no' followed by the applicant's username: ");
                        String confirmCommand = scanner.nextLine();
                        output.writeObject(confirmCommand);
                        response = (String) input.readObject();
                        System.out.println("Received response: " + response);
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}






























/*import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8889;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter login command (login <username> <registration_number>): ");
            String loginCommand = scanner.nextLine();
            output.writeObject(loginCommand);
            String response = (String) input.readObject();
            System.out.println("Received response: " + response);

            if (!response.startsWith("Login successful")) {
                System.out.println("Login failed. Exiting...");
                return;
            }

            while (true) {
                System.out.println("Menu:\n1. View Applicants\n2. Confirm Applicant\n3. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        output.writeObject("viewApplicants");
                        response = (String) input.readObject();
                        System.out.println("Received response: " + response);
                        break;
                    case "2":
                        System.out.print("Enter 'confirm yes/no' followed by the applicant's username: ");
                        String confirmCommand = scanner.nextLine();
                        output.writeObject(confirmCommand);
                        response = (String) input.readObject();
                        System.out.println("Received response: " + response);
                        break;
                    case "3":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}*/
