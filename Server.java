import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Server {
    private static final int PORT = 9876;
    private static final String FILE_PATH = "applicants.txt";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/register";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String request = in.readLine();
                    System.out.println("Received request: " + request);

                    if ("CHECK_DB_CONNECTION".equals(request)) {
                        out.println("Database connection checking is disabled");
                    } else if (request.startsWith("REGISTER")) {
                        String[] parts = request.split(" ", 8);
                        if (parts.length == 8) {
                            String username = parts[1];
                            String firstName = parts[2];
                            String lastName = parts[3];
                            String email = parts[4];
                            String dob = parts[5];
                            String schoolRegNumber = parts[6];
                            String imageFilePath = parts[7];

                            if (isUsernameTaken(username)) {
                                out.println("Username already taken, please choose another one");
                            } else {
                                System.out.println("Registering applicant with the following details:");
                                System.out.println("Username: " + username);
                                System.out.println("First Name: " + firstName);
                                System.out.println("Last Name: " + lastName);
                                System.out.println("Email: " + email);
                                System.out.println("Date of Birth: " + dob);
                                System.out.println("School Registration Number: " + schoolRegNumber);
                                System.out.println("Image File Path: " + imageFilePath);

                                if (isSchoolRegistered(schoolRegNumber)) {
                                    if (registerApplicant(username, firstName, lastName, email, dob, schoolRegNumber, imageFilePath)) {
                                        out.println("Registration successful");
                                        sendConfirmationEmail(email, schoolRegNumber);
                                    } else {
                                        out.println("Registration failed");
                                    }
                                } else {
                                    out.println("Your school is not among the registered schools");
                                }
                            }
                        } else {
                            out.println("Invalid registration command");
                        }
                    } else {
                        out.println("Unknown request");
                    }
                } catch (IOException e) {
                    System.out.println("Server exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + PORT);
            e.printStackTrace();
        }
    }

    private static boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Username: " + username + ",")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("IO error during username check: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isSchoolRegistered(String schoolRegNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM schools WHERE school_registration_number = ?")) {
            stmt.setString(1, schoolRegNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error during school registration number check: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean registerApplicant(String username, String firstName, String lastName, String email, String dob, String schoolRegNumber, String imageFilePath) {
        System.out.println("Simulating registration process...");
        System.out.println("Username: " + username);
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Date of Birth: " + dob);
        System.out.println("School Registration Number: " + schoolRegNumber);
        System.out.println("Image File Path: " + imageFilePath);

        Path imagePath = Paths.get(imageFilePath);
        if (Files.exists(imagePath)) {
            System.out.println("Image file exists at: " + imageFilePath);
            try {
                byte[] imageBytes = Files.readAllBytes(imagePath);
                System.out.println("Read image file successfully, size: " + imageBytes.length + " bytes");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                    writer.write(String.format("Username: %s, First Name: %s, Last Name: %s, Email: %s, Date of Birth: %s, School Registration Number: %s, Image File Path: %s%n",
                            username, firstName, lastName, email, dob, schoolRegNumber, imageFilePath));
                } catch (IOException e) {
                    System.out.println("IO error during writing to file: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
                return true;
            } catch (IOException e) {
                System.out.println("IO error during applicant registration: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Image file does not exist at: " + imageFilePath);
            return false;
        }
    }

    
    private static void sendConfirmationEmail(String recipientEmail, String schoolRegNumber) {
        // Replace with your own SMTP server details
        String senderEmail = "shadianankya979@gmail.com";
        String senderPassword = "zevj fybi gfie mbnw";
        String host = "smtp.gmail.com";
    
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
    
        // Create session with authentication
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });
    
        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);
    
            // Set From: header field of the header
            message.setFrom(new InternetAddress(senderEmail));
    
            // Set To: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(getSchoolRepresentativeEmail(schoolRegNumber)));
    
            // Set Subject: header field
            message.setSubject("Confirmation Required for Applicant Registration");
    
            // Now set the actual message
            message.setText("Dear School Representative,\n\n"
                    + "Please confirm the registration of an applicant with school registration number "
                    + schoolRegNumber + ".\n\nBest regards,\nYour Organization");
    
            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully to school representative.");
        } catch (MessagingException mex) {
            System.out.println("Failed to send email. Exception details:");
            mex.printStackTrace();
        }
    }
    

    private static String getSchoolRepresentativeEmail(String schoolRegNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT email_of_representative FROM schools WHERE school_registration_number = ?")) {
            stmt.setString(1, schoolRegNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email_of_representative");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error during school representative email retrieval: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
