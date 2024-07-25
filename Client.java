import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void loginInfo() {
        System.out.print("Login using <login> <email> <password>: ");
    }

    public static String menuInfo() {
        return "\n\t\tMenu\n-----------------------------------------------\n- 1. View Applicants \n- 2. Confirm Applicant (confirm yes/no username) \n- Enter choice: ";
    }

    public static String participantFirstMessage() {
        return "\n\t\tConfirm\n-----------------------------------------------\n- To login Enter <login> <username> <password> \n- To Register Enter <Register> <username> <firstname> <lastname> <emailAddress> <date_of_birth> <school_registration_number> <image_file.png>";
    }

    public static String participantLogin() {
        return " \n\t\tLOGIN\n-----------------------------------------------\n- <username> <email>";
    }

    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 1234);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String userInput;
            boolean loggedIn = false;
            String loginType = "";

            System.out.println("Use 'login_as <representative> or <participant>' to proceed");

            while ((userInput = in.readLine()) != null) {
                out.println(userInput);
                String serverResponse = serverIn.readLine();

                if (serverResponse.equals("acknowledged")) {
                    serverResponse = serverIn.readLine();

                    if (!loggedIn) {
                        if (serverResponse.equals("participant")) {
                            loginType = "participant";
                            System.out.println("Server response: " + serverResponse);
                            System.out.println(Client.participantFirstMessage());

                            userInput = in.readLine();
                            out.println(userInput);
                            serverResponse = serverIn.readLine();
                            System.out.println("Server response: " + serverResponse);

                            if (serverResponse.equals("Login successful!")) {
                                loggedIn = true;
                                System.out.println("You are now logged in as a participant.");
                                System.out.println("-Type <ViewChallenges> to view Challenges");
                                 // Added participant handling loop
                            while (loggedIn) {
                                userInput = in.readLine();
                                out.println(userInput);
            
                                // Handle "ViewChallenges" command
                                
                                if (userInput.equalsIgnoreCase("ViewChallenges")) {
                                    StringBuilder challengeResponse = new StringBuilder();
                                    while ((serverResponse = serverIn.readLine()) != null && !serverResponse.isEmpty()) {
                                        challengeResponse.append(serverResponse).append("\n");
                                    }
                                    System.out.println("Server Response: " + challengeResponse.toString());
                                    System.out.println("To attempt a challenge, type <attemptChallenge> <challengeNumber>");
                                }

                                // Handle "attemptChallenge" command
                                 
                                if (userInput.startsWith("attemptChallenge")) {
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
                                            userInput = in.readLine();  
                                            out.println(userInput);     
                                        }
                                    }

                                    // Print final responses after challenge completion
                                    while ((serverResponse = serverIn.readLine()) != null && !serverResponse.isEmpty()) {
                                        System.out.println(serverResponse);
                                    }
                                }
                             } 
                            }

                        } else if (serverResponse.equals("representative")) {
                            loginType = "representative";
                            System.out.println("Server response: " + serverResponse);
                            loginInfo();

                            userInput = in.readLine();
                            out.println(userInput);
                            serverResponse = serverIn.readLine();
                            System.out.println("Server response: " + serverResponse);

                            if (serverResponse.equals("Login successful!")) {
                                loggedIn = true;
                                System.out.println("You are now logged in as a representative.");
                                // Display representative menu
                                System.out.println(Client.menuInfo());

                                while (true) {
                                    userInput = in.readLine();
                                    out.println(userInput);

                                    if (userInput.equals("2")) {
                                        String serverPrompt = serverIn.readLine();
                                        System.out.print(serverPrompt);

                                        String confirmCommand = in.readLine();
                                        out.println(confirmCommand);
                                    }

                                    String serverResponseRep = serverIn.readLine();
                                    while (serverIn.ready()) {
                                        serverResponseRep += "\n" + serverIn.readLine();
                                    }

                                    System.out.println("Server response: " + serverResponseRep);
                                    System.out.println(Client.menuInfo());
                                }
                            }
                        } else {
                            System.out.println("Invalid login type. Please enter 'login_as <representative> or <participant>'");
                        }
                    }
                }  else {
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
