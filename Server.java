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
/* email packages */
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

    private static boolean registerApplicant(String username, String firstName, String lastName, String email,
            String dob, String schoolRegNumber, String imageFilePath) {

        Path imagePath = Paths.get(imageFilePath);
        if (Files.exists(imagePath)) {
            System.out.println("Image file exists at: " + imageFilePath);
            try {
                byte[] imageBytes = Files.readAllBytes(imagePath);
                System.out.println("Read image file successfully, size: " + imageBytes.length + " bytes");

                // Create uploads folder if it does not exist
                Path uploadsDir = Paths.get(UPLOADS_FOLDER);
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                    System.out.println("Created uploads directory at: " + UPLOADS_FOLDER);
                }

                // Copy image to uploads folder
                Path targetPath = uploadsDir.resolve(imagePath.getFileName());
                Files.copy(imagePath, targetPath);
                System.out.println("Copied image to uploads folder: " + targetPath.toString());

                // Get the file name and extension only
                String imageFileName = imagePath.getFileName().toString();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                    writer.write(String.format(
                            "Username: %s, First Name: %s, Last Name: %s, Email: %s, Date of Birth: %s, School Registration Number: %s, Image File Path: %s%n",
                            username, firstName, lastName, email, dob, schoolRegNumber, imageFileName));
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
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(getSchoolRepresentativeEmail(schoolRegNumber)));

            // Set Subject: header field
            message.setSubject("Confirmation Required for Applicant Registration");

            // Now set the actual message
            message.setText("Dear School Representative,\n\n"
                    +
                    "Please confirm the registration of an applicant with school registration number "
                    + schoolRegNumber + ".\n\nBest regards,\nMathematics Challenge Team");

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
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT representative_email FROM schools WHERE registration_number = ?")) {
            stmt.setString(1, schoolRegNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("representative_email");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error during school representative email retrieval: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    // Participant View Challenges
    public static void viewChallenges(PrintWriter out) {
        String query = "SELECT title, challenge_id FROM challenges where is_valid ='true'";

        try (
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement.executeQuery(query);
            StringBuilder response = new StringBuilder("Challenges:\n");

            while (resultSet.next()) {
                int challengeId = resultSet.getInt(("challenge_id"));
                String challengeName = resultSet.getString("title");
                response.append("- ").append(challengeName).append(" : ").append(challengeId).append("\n");

            }

            if (response.length() > "Challenges:\n".length()) {
                out.println(response.toString());

            } else {
                out.println("No challenges found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error retrieving challenges.");
        }

    }

    // attemptChallenge command
    private static Challenge fetchChallengeDetails(int challengeNumber, Statement statement) throws SQLException {
        // SQL query to fetch challenge details
        String query = "SELECT challenge_id, title, duration, number_of_questions FROM challenges WHERE challenge_id = "
                + challengeNumber;
        ResultSet resultSet = statement.executeQuery(query);

        Challenge challenge = null;
        if (resultSet.next()) {
            int challengeId = resultSet.getInt("challenge_id");
            String challengeName = resultSet.getString("title");
            int duration = resultSet.getInt("duration");
            int numberOfQns = resultSet.getInt("number_of_questions");
            challenge = new Challenge(challengeId, challengeName, duration, numberOfQns);
        }

        resultSet.close();
        return challenge;
    }

    private static List<Question> fetchRandomQuestions(int challengeNumber, Statement statement) throws SQLException {
        // SQL query to fetch random questions for the selected challenge
        String query = "SELECT q.question_id, q.text, q.marks, a.answer " +
                "FROM questions q " +
                "JOIN answers a ON q.question_id = a.question_id " +
                "WHERE q.challenge_id = " + challengeNumber +
                " ORDER BY RAND()"; // Fetch random questions

        ResultSet resultSet = statement.executeQuery(query);
        List<Question> questions = new ArrayList<>();

        while (resultSet.next()) {
            int questionId = resultSet.getInt("q.question_id");
            String questionText = resultSet.getString("q.text");
            int questionMarks = resultSet.getInt("q.marks");
            String answerText = resultSet.getString("a.answer");
            questions.add(new Question(questionId, questionText, questionMarks, answerText));
        }

        resultSet.close();
        return questions;
    }

    private static void handleChallengeAttempt(Challenge challenge, List<Question> questions, PrintWriter out,
            BufferedReader in, int participantId) throws IOException {
        int totalScore = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + challenge.getDuration() * 60000; // duration in minutes

        for (int i = 0; i < questions.size(); i++) {
            if (System.currentTimeMillis() > endTime) {
                out.println("Time's up! Challenge closed.");
                break;
            }

            Question question = questions.get(i);

            long questionTimeStart = System.currentTimeMillis();

            out.println("Remaining Questions: " + (questions.size() - i));
            out.println("Time left: " + (endTime - System.currentTimeMillis()) / 1000 + " seconds");
            out.println("Text: " + question.getQuestionText());
            out.println("Marks: " + question.getQuestionMarks());
            out.println("Instructions: Type your answer, type '-' if you do not know");
            out.println("Your answer: ");
            String userAnswer = in.readLine().trim();

            long questionTimeTaken = (System.currentTimeMillis() - questionTimeStart) / 1000; // Time taken for the
                                                                                              // question in seconds

            if (userAnswer.equals(question.getAnswerText())) {
                totalScore += question.getQuestionMarks();
            } else if (userAnswer.equals("-")) {
                out.println("Score is 0");
            } else {
                totalScore -= 3; // Deduct 3 marks for wrong answer
            }
            question.setQuestionTimeTaken(questionTimeTaken);
        }

        long totalTimeTaken = (System.currentTimeMillis() - startTime) / 1000;
        out.println("Challenge completed!");
        out.println("Total Score: " + totalScore);
        out.println("Total Time Taken: " + totalTimeTaken + " seconds");

        // Insert attempt details into the database

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {

            String insertQuery = String.format(
                    "INSERT INTO attempts (participant_id, challenge_id, start_time, end_time, total_time_taken, total_score) "
                            +
                            "VALUES (%d, %d, '%s', '%s', %d, %d)",
                    participantId, challenge.getChallengeId(), new Timestamp(startTime),
                    new Timestamp(System.currentTimeMillis()),
                    totalTimeTaken,
                    totalScore);
            int rowsInserted = statement.executeUpdate(insertQuery);
            if (rowsInserted > 0) {
                out.println("Attempt details saved successfully.");
                String updateQuery = String.format(
                        "UPDATE participants " +
                                "SET total_attempts = total_attempts + 1, total_challenges = total_challenges + 1 " +
                                "WHERE participant_id = %d",
                        participantId);
                int rowsUpdated = statement.executeUpdate(updateQuery);
                if (rowsUpdated > 0) {
                    out.println("Participant's total attempts and total challenges updated successfully.");
                } else {
                    out.println("Failed to update participant's total attempts and total challenges.");
                }
            } else {
                out.println("Failed to save attempt details.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error saving attempt details to the database.");
        }
        // display report to the client
        generateReport(questions, totalScore, totalTimeTaken, out);

    }

    private static void generateReport(List<Question> questions, int totalScore, long totalTimeTaken, PrintWriter out) {
        // report generation logic here

        out.println("Report:");
        out.println("Total Score: " + totalScore);
        out.println("Total Time Taken: " + totalTimeTaken + " seconds");

        // Print details for each question attempted
        for (Question question : questions) {
            out.println("Question ID: " + question.getQuestionId());
            out.println("Text: " + question.getQuestionText());
            out.println("Marks: " + question.getQuestionMarks());
            out.println("Time taken: " + question.getQuestionTimeTaken() + " seconds");
            // Separate questions
        }
        out.println();

    }

    private static int getAttemptCount(int participantId, int challengeId) {
        String query = String.format("SELECT COUNT(*) FROM attempts WHERE participant_id = %d AND challenge_id = %d",
                participantId, challengeId);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class Challenge {
        private int challengeId;
        private String challengeName;
        private int duration;
        private int numberOfQns;

        public Challenge(int challengeId, String challengeName, int duration, int numberOfQns) {
            this.challengeId = challengeId;
            this.challengeName = challengeName;
            this.duration = duration;
            this.numberOfQns = numberOfQns;
        }

        public int getChallengeId() {
            return challengeId;
        }

        public String getChallengeName() {
            return challengeName;
        }

        public int getDuration() {
            return duration;
        }

        public int getNumberOfQns() {
            return numberOfQns;
        }

        @Override
        public String toString() {
            return "Challenge ID: " + challengeId + ", Name: " + challengeName + ", Duration: " + duration
                    + " minutes, Number of Questions: " + numberOfQns;
        }
    }

    public static class Question {
        private int questionId;
        private String questionText;
        private int questionMarks;
        private String answerText;
        private long presentationTime;
        private long questionTimeTaken;

        public Question(int questionId, String questionText, int questionMarks, String answerText) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.questionMarks = questionMarks;
            this.answerText = answerText;
            this.presentationTime = System.currentTimeMillis(); // Initialize with current time
        }

        public int getQuestionId() {
            return questionId;
        }

        public String getQuestionText() {
            return questionText;
        }

        public int getQuestionMarks() {
            return questionMarks;
        }

        public String getAnswerText() {
            return answerText;
        }

        public long getPresentationTime() {
            return presentationTime;
        }

        public long getQuestionTimeTaken() {
            return questionTimeTaken;
        }

        public void setQuestionTimeTaken(long questionTimeTaken) {
            this.questionTimeTaken = questionTimeTaken;
        }

        @Override
        public String toString() {
            return "Question ID: " + questionId + ", Text: " + questionText + ", Marks: " + questionMarks + ", Answer: "
                    + answerText;
        }
    }
}
