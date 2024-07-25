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



    