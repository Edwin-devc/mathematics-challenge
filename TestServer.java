
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestServer{
     /* database variables */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/school_competition";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    

    /* socket variables */
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in = null;
    private static PrintWriter out;

    /* class level variable */
    private static Integer participantId = null;
       

  

    public static void main(String[] args) throws ClassNotFoundException, IOException{

        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        serverSocket = new ServerSocket(1234);
        System.out.println("Server is running. Waiting for a client to connect...");

        clientSocket = serverSocket.accept();
        System.out.println("Client connected.");

        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;

        try(
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement()
        ) {
            while ((inputLine = in.readLine()) != null){
                String[] command = inputLine.split(" ");
                    switch (command[0]) {
                        
                        case "login_as":
                        // login_as logic
                        if(command.length == 2) {
                            if (command[1].equals("participant")){
                                out.println("acknowledged");
                                out.println("participant");
                            }else{
                                out.println("Invalid Command");
                            }
                        }else{
                            out.println("Invalid Command Length for login_as");
                        }
                        break;
    
                        case "login":
                        if (command.length == 3){
                           participantId = participantLogin(command[1], command[2], out);
                        }else{
                            out.println("Invalid command length for login");
                        }
                        break;

                        case "Register":
                        // register command
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
                            System.out.println("Invalid command format. Please use 'attemptChallenge challengeNumber'.");
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
       

// Participant Login
public static Integer participantLogin(String username, String password, PrintWriter out){

    username = username.trim();
    password = password.trim();

    String query = String.format("SELECT participant_id, user_name, password FROM participant WHERE user_name = '%s' AND password = '%s'", username, password);

    try(
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement()
    ) {
        
        ResultSet resultSet = statement.executeQuery(query);

        if(resultSet.next()){
            int participantId = resultSet.getInt("participant_id");
            out.println("To view challenges type <ViewChallenges>");
            return participantId;
        }else{
            
            out.println("Invalid login credentials");
        }
        statement.close();
        
    } catch (SQLException e) {
        e.printStackTrace();
            out.println("Error during login");
    }
    return null;
    
}

// Participant View Challenges
public static void viewChallenges(PrintWriter out){
    String query = "SELECT challenge_name, challenge_id FROM challenge where is_valid ='true'";

    try(
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement();
    ) {
        ResultSet resultSet = statement.executeQuery(query);
        StringBuilder response = new StringBuilder("Challenges:\n");

        while(resultSet.next()){
            int challengeId = resultSet.getInt(("challenge_id"));
            String challengeName = resultSet.getString("challenge_name");
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
    String query = "SELECT challenge_id, challenge_name, duration, number_of_qn FROM challenge WHERE challenge_id = " + challengeNumber;
    ResultSet resultSet = statement.executeQuery(query);

    Challenge challenge = null;
    if (resultSet.next()) {
        int challengeId = resultSet.getInt("challenge_id");
        String challengeName = resultSet.getString("challenge_name");
        int duration = resultSet.getInt("duration");
        int numberOfQns = resultSet.getInt("number_of_qn");
        challenge = new Challenge(challengeId, challengeName, duration, numberOfQns);
    }

    resultSet.close();
    return challenge;
}


private static List<Question> fetchRandomQuestions(int challengeNumber, Statement statement) throws SQLException {
    // SQL query to fetch random questions for the selected challenge
    String query = "SELECT q.question_id, q.question_text, q.question_mark, a.answer_text " +
                   "FROM question q " +
                   "JOIN answer a ON q.answer_id = a.answer_id " +
                   "WHERE q.challenge_id = " + challengeNumber +
                   " ORDER BY RAND()"; // Fetch random questions

    ResultSet resultSet = statement.executeQuery(query);
    List<Question> questions = new ArrayList<>();

    while (resultSet.next()) {
        int questionId = resultSet.getInt("q.question_id");
        String questionText = resultSet.getString("q.question_text");
        int questionMarks = resultSet.getInt("q.question_mark");
        String answerText = resultSet.getString("a.answer_text");
        questions.add(new Question(questionId, questionText, questionMarks, answerText));
    }

    resultSet.close();
    return questions;
}

private static void handleChallengeAttempt(Challenge challenge, List<Question> questions, PrintWriter out, BufferedReader in, int participantId) throws IOException {
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

        long questionTimeTaken = (System.currentTimeMillis() - questionTimeStart) / 1000; // Time taken for the question in seconds

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

    String insertQuery = String.format("INSERT INTO attempt (participant_id, challenge_id, start_time, end_time, total_time_taken, total_score) " +
                                           "VALUES (%d, %d, '%s', '%s', %d, %d)",participantId,challenge.getChallengeId(),new Timestamp(startTime),
                                           new Timestamp(System.currentTimeMillis()), 
                                           totalTimeTaken,
                                           totalScore);
    int rowsInserted = statement.executeUpdate(insertQuery);
    if (rowsInserted > 0) {
        out.println("Attempt details saved successfully.");
        String updateQuery = String.format(
                "UPDATE participant " +
                "SET total_attempts = total_attempts + 1, total_challenges = total_challenges + 1 " +
                "WHERE participant_id = %d",
                participantId
            );
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
    //report generation logic here
    
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
    String query = String.format("SELECT COUNT(*) FROM attempt WHERE participant_id = %d AND challenge_id = %d", participantId, challengeId);
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
        return "Challenge ID: " + challengeId + ", Name: " + challengeName + ", Duration: " + duration + " minutes, Number of Questions: " + numberOfQns;
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
        return "Question ID: " + questionId + ", Text: " + questionText + ", Marks: " + questionMarks + ", Answer: " + answerText;
    }
}
    

}