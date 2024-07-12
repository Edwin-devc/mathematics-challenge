import java.io.*;
import java.net.*;

public class TestClient {

    
    public static String participantFirstMessage() {
        String message = "\n\t\tConfirm\n-----------------------------------------------\n- To login Enter <login> <username> <password> ";
        return message;
    }

    

    public static String participant_login(){
        String login = " \n\t\tLOGIN\n-----------------------------------------------\n- <username> <email>"; 
        return login;
    }

    public static void main(String[] args){

       try {

        Socket clientSocket = new Socket("localhost", 1234);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String userInput;
    
        System.out.println("Use 'login_as <representative> or <participant>' to proceed");


        while ((userInput = in.readLine()) != null){
            out.println(userInput);
            String serverResponse = serverIn.readLine();

            if (serverResponse.equals("acknowledged")) {
                serverResponse = serverIn.readLine();

                // participant
            if(serverResponse.equals("participant")){
                System.out.println("Server response:" + serverResponse);
                System.out.println(TestClient.participantFirstMessage());

                
                userInput = in.readLine();
                out.println(userInput);
                serverResponse = serverIn.readLine();
                System.out.println("Server response : " + serverResponse);

                if (serverResponse.contains("ViewChallenges")){
                    userInput = in.readLine();
                    out.println(userInput);

                    // Read and print all lines of server response for ViewChallenges
                    StringBuilder challengeResponse = new StringBuilder();

                    while((serverResponse = serverIn.readLine()) != null && !serverResponse.isEmpty()){
                        challengeResponse.append(serverResponse).append("\n");
                    }
                    System.out.println("Server Response : "+ challengeResponse.toString());
                    
                    System.out.println("To attempt a challenge, type <attemptChallenge> <challengeNumber>");
                }
            
            }
        } else if (serverResponse.startsWith("You chose to attempt challenge:")) {
            // Handle challenge questions and responses
            System.out.println(serverResponse);

            while (true) {
                serverResponse = serverIn.readLine();

                // Check if the challenge is completed
                if (serverResponse.equals("Challenge completed!")) {
                    System.out.println(serverResponse);
                    break;
                }

                // Print other responses (question details, etc.)
                System.out.println(serverResponse);

                // Look for the "Your answer: " prompt
                if (serverResponse.startsWith("Your answer: ")) {
                    userInput = in.readLine();  // Read user input
                    out.println(userInput);     // Send user input to server
                }
            }

            // Print final responses after challenge completion
            System.out.println(serverResponse);
            while ((serverResponse = serverIn.readLine()) != null && !serverResponse.isEmpty()) {
                System.out.println(serverResponse);
            }
        } 

        else {
            System.out.println("Server response: " + serverResponse);
        }
    }

        in.close();
        serverIn.close();
        out.close();
        clientSocket.close();
           
 } catch (Exception e) {
        e.printStackTrace();

       }
    }

}
