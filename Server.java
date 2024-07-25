import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class Server {
    /* database variables */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/math_challenge";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";

    /* socket variables */
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in = null;
    private static PrintWriter out;

    /* class level variable */
    private static Integer participantId = null;
    private static final String FILE_PATH = "applicants.txt";
    private static String userType;
    private static String representativeSchoolRegistrationNumber = null;

    /* email variables */
    private static final String SENDER_EMAIL = "essaotys5@gmail.com";
    private static final String SENDER_PASSWORD = "xqaz jyix vzsl qdgp";
    private static final String SMTP_HOST = "smtp.gmail.com";

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        serverSocket = new ServerSocket(1234);
        System.out.println("Server is running. Waiting for a client to connect...");

        clientSocket = serverSocket.accept();
        System.out.println("Client connected.");

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;

        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {
            while ((inputLine = in.readLine()) != null) {

                String[] command = inputLine.split(" ");
                switch (command[0]) {
                    case "login_as":
                        // login_as logic
                        if (command.length == 2) {
                            userType = command[1];
                            if (userType.equals("participant") || userType.equals("representative")) {
                                out.println("acknowledged");
                                out.println(userType);
                            } else {
                                out.println("Invalid Command");
                            }
                        } else {
                            out.println("Invalid Command Length for login_as");
                        }
                        break;

                    case "login":
                        if (command.length == 3) {
                            if (userType == null) {
                                out.println("Please specify user type with 'login_as' command first.");
                            } else if (userType.equals("participant")) {
                                participantId = participantLogin(command[1], command[2], out);
                            } else if (userType.equals("representative")) {
                                representativeSchoolRegistrationNumber = representativeLogin(command[1], command[2],
                                        out);
                                if (representativeSchoolRegistrationNumber != null) {
                                    handleRepresentativeCommands(connection);
                                }
                            } else {
                                out.println("Invalid user type");
                            }
                        } else {
                            out.println("Invalid command length for login");
                        }
                        break;

                    case "Register":
                        handleRegisterRequest(command, out);
                        break;

                    case "ViewChallenges":
                        viewChallenges(out);

                        break;

                    case "attemptChallenge":
                        if (command.length == 2) {
                            if (participantId != null) {
                                int challengeNumber = Integer.parseInt(command[1]);

                                // Check attempt count
                                int attemptCount = getAttemptCount(participantId, challengeNumber);
                                if (attemptCount >= 3) {
                                    out.println("You have exhausted your maximum attempts for this challenge.");
                                    break;
                                }

                                out.println("You chose to attempt challenge: " + challengeNumber);

                                // Fetch challenge details
                                Challenge challenge = fetchChallengeDetails(challengeNumber, statement);

                                // Fetch random questions for the selected challenge
                                List<Question> questions = fetchRandomQuestions(challengeNumber, statement);

                                // Handle the challenge attempt
                                handleChallengeAttempt(challenge, questions, out, in, participantId);

                            } else {
                                out.println("Login required to attempt challenges.");
                            }

                        } else {
                            System.out
                                    .println("Invalid command format. Please use 'attemptChallenge challengeNumber'.");
                        }

                        break;

                    default:
                        out.println("Invalid command, yes");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }

    }




        // REPRESENTATIVE METHODS
        public static String representativeLogin(String username, String password, PrintWriter out) {
            username = username.trim();
            password = password.trim();

            String query = "SELECT s.registration_number FROM representatives r JOIN schools s ON r.school_id = s.school_id WHERE r.email = ? AND r.password = ?";

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String registrationNumber = rs.getString("registration_number");
                    out.println("Login successful!");
                    return registrationNumber;
                } else {
                    out.println("Invalid login credentials");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Error during login");
            }
            return null;
        }

        private static void handleRepresentativeCommands(Connection connection) throws IOException {

            while (true) {
                String choice = in.readLine();

                switch (choice) {
                    case "1":
                        List<String> applicants = ApplicantManager.readApplicants();
                        displayApplicants(applicants);
                        break;
                    case "2":
                        out.println("Enter command: ");
                        String command = in.readLine();
                        confirmApplicant(command, connection);
                        break;
                    default:
                        out.println("Invalid choice. Please try again.");
                }
            }
        }

        private static void displayApplicants(List<String> applicants) {

            for (String applicant : applicants) {
                out.println(applicant);
            }
        }

        private static void confirmApplicant(String command, Connection connection) {
            String[] parts = command.split(" ");
            if (parts.length != 3 || !parts[0].equals("confirm")) {
                out.println("Invalid command. Use: confirm yes/no username");
                return;
            }
            String action = parts[1];
            String username = parts[2];

            List<String> applicants = ApplicantManager.readApplicants();
            String applicantDetails = null;
            for (String applicant : applicants) {
                if (applicant.contains(username)) {
                    applicantDetails = applicant;
                    break;
                }
            }

            if (applicantDetails == null) {
                out.println("Applicant not found.");
                return;
            }

            String[] details = applicantDetails.split(",");

            String applicantRegNo = details[5].replace("School Registration Number:", "").trim();
            String representativeRegNo = representativeSchoolRegistrationNumber.trim();

            if (details.length < 6 || !applicantRegNo.equals(representativeRegNo)) {
                out.println("You can only accept/reject applicants that belong to your school.");
                return;
            }

            if (action.equals("yes")) {
                activateApplicant(details, connection);
            } else if (action.equals("no")) {
                rejectApplicant(details, connection);
            } else {
                out.println("Invalid action. Use: confirm yes/no username");
            }

            ApplicantManager.removeApplicant(username);
        }

        private static void activateApplicant(String[] details, Connection connection) {
            try {

                // Extract and clean the details
                String username = details[0].split(":")[1].trim();
                String firstname = details[1].split(":")[1].trim();
                String lastname = details[2].split(":")[1].trim();
                String email = details[3].split(":")[1].trim();
                String dateOfBirth = details[4].split(":")[1].trim();
                String schoolRegistrationNumber = details[5].split(":")[1].trim();
                String imagePath = details[6].split(":")[1].trim();

                String randomPassword = ApplicantManager.generateRandomPassword();

                String insertParticipant = "INSERT INTO participants (username, firstname, lastname, email, date_of_birth, school_registration_number, image_path, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertPs = connection.prepareStatement(insertParticipant);
                insertPs.setString(1, username); // username
                insertPs.setString(2, firstname); // firstname
                insertPs.setString(3, lastname); // lastname
                insertPs.setString(4, email); // email
                insertPs.setString(5, dateOfBirth); // date_of_birth
                insertPs.setString(6, schoolRegistrationNumber); // school_registration_number
                insertPs.setString(7, imagePath); // image_path
                insertPs.setString(8, randomPassword); // password
                insertPs.executeUpdate();

                sendEmailNotification(email, "Application Accepted ",
                        "Your application has been accepted.Your password is: " + randomPassword);
                out.println("Applicant " + details[0] + " activated.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void rejectApplicant(String[] details, Connection connection) {
            try {

                // Extract and clean the details
                String username = details[0].split(":")[1].trim();
                String firstname = details[1].split(":")[1].trim();
                String lastname = details[2].split(":")[1].trim();
                String email = details[3].split(":")[1].trim();
                String dateOfBirth = details[4].split(":")[1].trim();
                String schoolRegistrationNumber = details[5].split(":")[1].trim();
                String imagePath = details[6].split(":")[1].trim();

                String insertRejected = "INSERT INTO rejected (username, firstname, lastname, email, date_of_birth, school_registration_number, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertPs = connection.prepareStatement(insertRejected);
                insertPs.setString(1, username); // username
                insertPs.setString(2, firstname); // firstname
                insertPs.setString(3, lastname); // lastname
                insertPs.setString(4, email); // email
                insertPs.setString(5, dateOfBirth); // date_of_birth
                insertPs.setString(6, schoolRegistrationNumber); // school_registration_number
                insertPs.setString(7, imagePath); // image_path
                insertPs.executeUpdate();

                sendEmailNotification(email, "Application Declined", "Your application has been rejected.");
                out.println("Applicant " + details[0] + " rejected.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void sendEmailNotification(String recipient, String subject, String content) {
            // Configure properties for SMTP
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", "587");

            // Create session
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setText(content);
                message.setSubject(subject);

                // Send message
                Transport.send(message);
                System.out.println("Email sent successfully to: " + recipient);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email to " + recipient, e);
            }
        }

        public class ApplicantManager {

            private static final String APPLICANTS_FILE = "applicants.txt";
            private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            private static final int PASSWORD_LENGTH = 10;
            private static final SecureRandom RANDOM = new SecureRandom();

            public static List<String> readApplicants() {
                List<String> applicants = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(APPLICANTS_FILE))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        applicants.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return applicants;
            }

            public static void removeApplicant(String username) {
                List<String> applicants = readApplicants();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPLICANTS_FILE))) {
                    for (String applicant : applicants) {
                        if (!applicant.contains(username)) {
                            bw.write(applicant);
                            bw.newLine();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public static String generateRandomPassword() {
                StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
                for (int i = 0; i < PASSWORD_LENGTH; i++) {
                    password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
                }
                return password.toString();
            }
        }

        //
        //
        //
        //
        // Participant Login
        public static Integer participantLogin(String username, String password, PrintWriter out) {
        String cleaned_username = username.trim();
        String cleaned_password = password.trim();

        String query = "SELECT participant_id, username, password FROM participants WHERE username = ? AND password = ?";

        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, cleaned_username);
            ps.setString(2, cleaned_password);

            ResultSet resultSet = ps.executeQuery(); // No need to pass the query again

            if (resultSet.next()) {
                int participantId = resultSet.getInt("participant_id");
                out.println("Login successful!");
                return participantId;
            } else {
                out.println("Invalid login credentials");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error during login");
        }
        return null;
    }

    // Participant register command
    private static void handleRegisterRequest(String[] parts, PrintWriter out) {
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
            } else if (isRejected(firstName, lastName, schoolRegNumber)) {
                out.println("You can't be registered after being rejected.");
            } else {

                if (isSchoolRegistered(schoolRegNumber)) {
                    if (registerApplicant(username, firstName, lastName, email, dob, schoolRegNumber, imageFilePath)) {
                        sendConfirmationEmail(email, schoolRegNumber);
                        out.println("Registration successful. Please wait for the confirmation");

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
            out.println("IO error during username check: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isSchoolRegistered(String schoolRegNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM schools WHERE registration_number = ?")) {
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

    private static boolean isRejected(String firstname, String lastname, String schoolRegNumber) {
        String query = "SELECT * FROM rejected WHERE firstname = ? AND lastname = ? AND school_registration_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, schoolRegNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error during rejection check: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static final String UPLOADS_FOLDER = "public/uploads/";

    