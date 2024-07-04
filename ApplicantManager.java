import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class ApplicantManager {
    private Connection connection;
    private static final String APPLICANTS_FILE = "applicants.txt";
    private static final String SENDER_EMAIL = "essaotys5@gmail.com";
    private static final String SENDER_PASSWORD = "xqaz jyix vzsl qdgp";
    private static final String SMTP_HOST = "smtp.gmail.com"; 

    public ApplicantManager(Connection connection) {
        this.connection = connection;
    }

    public String login(String username, int regno) {
        return "Login successful for " + username;
    }

    public String viewApplicants() {
        StringBuilder applicantsList = new StringBuilder("Applicants:\n");

        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICANTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                applicantsList.append(line).append("\n"); 
            }
        } catch (IOException e) {
            return "Error reading applicants file: " + e.getMessage();
        }

        return applicantsList.toString();
    }

    public static void sendEmail(String recipient, String subject, String content) {
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
            message.setSubject(subject);
            message.setText(content);
            
            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipient);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + recipient, e);
        }
    }

    public String confirmApplicant(String decision, String username, int regno) {
        List<String> applicants = new ArrayList<>();
        boolean applicantFound = false;
        String email = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICANTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    if (parts.length >= 6 && Integer.parseInt(parts[5].trim()) == regno) {
                        applicantFound = true;
                        email = parts[3];

                        if (decision.equalsIgnoreCase("yes")) {
                            addParticipant(parts);
                            sendEmail(email, "Application Accepted", "Dear " + parts[1] + ",\n\nYour application has been accepted.\n\nRegards,\nTeam");
                        } else if (decision.equalsIgnoreCase("no")) {
                            addRejected(parts);
                            sendEmail(email, "Application Rejected", "Dear " + parts[1] + ",\n\nYour application has been rejected.\n\nRegards,\nTeam");
                        } else {
                            return "Invalid decision. Use 'yes' to confirm or 'no' to reject.";
                        }
                    } else {
                        return "You can only accept/reject applicants that belong to your school.";
                    }
                } else {
                    applicants.add(line);
                }
            }
        } catch (IOException e) {
            return "Error reading applicants file: " + e.getMessage();
        }

        if (!applicantFound) {
            return "Applicant not found: " + username;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICANTS_FILE))) {
            for (String applicant : applicants) {
                writer.write(applicant);
                writer.newLine();
            }
        } catch (IOException e) {
            return "Error updating applicants file: " + e.getMessage();
        }

        return decision.equalsIgnoreCase("yes") ? "Applicant accepted: " + username : "Applicant rejected: " + username;
    }

    private void addParticipant(String[] parts) {
        String query = "INSERT INTO participants (username, firstname, lastname, emailAddress, date_of_birth, registration_number, image_file) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, parts[0]);
            stmt.setString(2, parts[1]);
            stmt.setString(3, parts[2]);
            stmt.setString(4, parts[3]);
            stmt.setString(5, parts[4]);
            stmt.setInt(6, Integer.parseInt(parts[5].trim()));
            stmt.setString(7, parts[6]);
            stmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void addRejected(String[] parts) {
        String query = "INSERT INTO rejected (username, firstname, lastname, emailAddress, date_of_birth, registration_number, image_file) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, parts[0]);
            stmt.setString(2, parts[1]);
            stmt.setString(3, parts[2]);
            stmt.setString(4, parts[3]);
            stmt.setString(5, parts[4]);
            stmt.setInt(6, Integer.parseInt(parts[5].trim()));
            stmt.setString(7, parts[6]);
            stmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
