// Group Partners: Nicholas Lubold, Charles Lombardo, Eugene Sosa

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Project464 {

    public static void main(String[] args) {
        Boolean result;
        //int count = 0;
        boolean isDFA = true;
        try {
            // First Step is to Parse the DFA Description
            //Gets and opens File
            File DFADesc = new File(args[0]);

            Scanner Reader = new Scanner(DFADesc);

            //Gets and stores Alphabet
            String alphabet = Reader.nextLine();
            String[] alphabetSet = alphabet.split("");

            int numStates =  Integer.parseInt(Reader.nextLine());

            String[] States = new String[numStates];

            for(int i = 0; i < numStates; i ++){
                States[i] = Reader.nextLine();
            }

            String startState = Reader.nextLine();

            int numAcceptStates =  Integer.parseInt(Reader.nextLine());

            String[] acceptStates = new String[numAcceptStates];

            for (int i = 0; i < numAcceptStates; i ++){
                acceptStates[i] = Reader.nextLine();

            }

            int numTransition = Integer.parseInt(Reader.nextLine());

            String[][] transitions = new String[numTransition][3];
            for (int i = 0; i < numTransition; i ++){
                transitions[i] = Reader.nextLine().split(",");
            }
//``````````````````````````````````````````````````````````````````
            // Check for Epsilon Transitions
            boolean hasEpsilonTransition = false;
            for (String[] transition : transitions) {
                if (transition[2].equals("EPSILON")) {
                    hasEpsilonTransition = true;
                    break;
                }
            }

            if (hasEpsilonTransition) {
                isDFA = false;
                System.out.println("The automaton contains epsilon transitions. It is an NFA.");
            } else {
                System.out.println("The automaton does not contain epsilon transitions.");
            }

            // Check for Multiple Transitions from a State for the Same Symbol
            boolean hasMultipleTransitions = false;
            for (String state : States) {
                for (String symbol : alphabetSet) {
                    int count = 0;
                    for (String[] transition : transitions) {
                        if (transition[0].equals(state) && transition[2].equals(symbol)) {
                            count++;
                        }
                    }
                    if (count > 1) {
                        hasMultipleTransitions = true;
                        isDFA = false;
                        System.out.println("The automaton contains multiple transitions from a state for the same symbol. It is an NFA.");
                        break;
                    }
                }
                if (hasMultipleTransitions) {
                    break;
                }
            }

            if (isDFA) {
                System.out.println("The automaton does not have epsilon transitions or multiple transitions from a state for the same symbol. It is a DFA.");
            }

//``````````````````````````````````````````````````````````````````
            String pointer = startState;

            try {
                File wFile = new File(args[1]);

                Scanner Reader2 = new Scanner(wFile);
                String w;
                while(Reader2.hasNextLine()) {
                    w = Reader2.nextLine();
                    result = DFAAccept(alphabetSet, pointer, acceptStates, transitions, w);

                    if (result) {
                        System.out.println("ACCEPT");
                    } else {
                        System.out.println("REJECT");

                    }
                }
                Reader2.close();
            } catch(FileNotFoundException e2) {
                String w = args[1];
                result = DFAAccept(alphabetSet, pointer, acceptStates, transitions, w);
                if (result) {
                    System.out.println("ACCEPT");
                } else {
                    System.out.println("REJECT");

                }
            }
            Reader.close();

        } catch (FileNotFoundException e){
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    public static boolean DFAAccept(String[] alphabet, String pointer, String[] AcceptStates, String[][] transitions, String w){

        for (int i = 0; i < w.length(); i++){
            //Checks if character is part of the alphabet, reject if not
            if (!Arrays.asList(alphabet).contains(String.valueOf(w.charAt(i)))){
                return false;
            }
            for (int j = 0; j < transitions.length; j++){
                // go through each transition until the pointer state and input character match
                if (pointer.equals(transitions[j][0]) && String.valueOf(w.charAt(i)).equals(transitions[j][2])) {
                    pointer = transitions[j][1]; //once found set pointer state to the appropriate state
                    break;  //continues to the next character
                }
            }
        }

        //when all characters in string have been processed check if any pointer states are an Accept State.
        for (String accept : AcceptStates){
            if (pointer.equals(accept)) {
                return true;
            }
        }

        return false;
    }

}