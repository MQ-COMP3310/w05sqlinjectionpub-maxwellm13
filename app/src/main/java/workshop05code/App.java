package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO,"Wordle created and connected.");
          //  System.out.println("Wordle created and connected.");
        } else {
            logger.log(Level.INFO,"Not able to connect. Sorry!");
        //    System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Wordle structures in place.");
      //      System.out.println("Wordle structures in place.");
        } else {
            logger.log(Level.INFO,"Not able to launch. Sorry!");
            // System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (!line.matches("[a-z]{4}")) {  // word is invalid
                    logger.log(Level.SEVERE, "Invalid word in data file: " + line);
                    continue;
                }
                logger.log(Level.INFO, "Valid word added: " + line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE,"Not able to load . Sorry!");
            // System.out.println("Not able to load . Sorry!");
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            logger.log(Level.INFO,"Enter a 4 letter word for a guess or q to quit: ");
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            if (guess.length() != 4) {
                logger.log(Level.WARNING, "User input was not 4 letters in length");
                System.out.println("WARNING: MUST BE A 4-LETTER WORD");
            }
                if (!guess.matches("[a-z]{4}")) {
                    logger.log(Level.WARNING, "Invalid input: " + guess);
                    System.out.println("Invalid input. Please enter a 4-letter word.");
                }

                      
    

            while (!guess.equals("q")) {
                logger.log(Level.INFO,"You've guessed '" + guess+"'.");
                System.out.println("You've guessed '" + guess+"'.");

                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    logger.log(Level.INFO,"Success! It is in the the list.");
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    logger.log(Level.INFO,"Sorry. This word is NOT in the the list");
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "Unexpected error occurred.", e);
        }

    }
}