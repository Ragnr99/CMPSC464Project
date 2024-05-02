// Group Partners: Nicholas Lubold, Charles Lombardo, Eugene Sosa

// args[0] assumes a valid dfa or nfa input file, it will automatically determine which one it is
// args[1] can be either a string or text file where each row corresponds to a string

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Project464 {

    public static void main(String[] args) {
        Boolean result;
        int count;
        boolean isDFA = true;
        try {
            File DFADesc = new File(args[0]);

            Scanner Reader = new Scanner(DFADesc);

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
            // Output all transitions to console
            System.out.println("Transitions:");
            for (String[] transition : transitions) {
                System.out.println(Arrays.toString(transition));
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
                System.out.println("The automata contains epsilon transitions. \nIt is an NFA.");
            } else {
                System.out.println("The automata does not contain epsilon transitions.");
            }

            // Check for Multiple Transitions from a State for the same symbol
            boolean hasMultipleTransitions = false;
            for (String state : States) {
                for (String symbol : alphabetSet) {
                    count = 0;
                    for (String[] transition : transitions) {
                        if (transition[0].equals(state) && transition[2].equals(symbol)) {
                            count++;
                        }
                    }
                    if (count > 1) {
                        hasMultipleTransitions = true;
                        isDFA = false;
                        System.out.println("The automata contains multiple transitions from a state for the same symbol. \nIt is an NFA.");
                        break;
                    }
                }
                if (hasMultipleTransitions) {
                    break;
                }
            }

            // Check if enough transitions for NFA
            boolean notEnoughStates = false;
            if(numStates * alphabet.length() == numTransition) {
                notEnoughStates = true;
                isDFA = false;
                System.out.println("The automata contains less than transitions. \nIt is an NFA");
            }

            if (isDFA) {
                System.out.println("The automata does not have epsilon transitions or multiple transitions from a state for the same symbol. \nIt is a DFA.");
            } else {
                // Convert from NFA to DFA
                //      need new: startState, acceptStates, transitions

                // Generate subsets
                List<List<String>> subsets = generateSubsets(States);

                // Output subsets
                System.out.println("Subsets:");
                for (List<String> subset : subsets) {
                    System.out.println(subset);
                }

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

    // Generate all subsets of an array of strings
    private static List<List<String>> generateSubsets(String[] strings) {
        List<List<String>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), strings, 0);
        return result;
    }

    private static void backtrack(List<List<String>> result, List<String> tempList, String[] strings, int start) {
        result.add(new ArrayList<>(tempList));
        for (int i = start; i < strings.length; i++) {
            tempList.add(strings[i]);
            backtrack(result, tempList, strings, i + 1);
            tempList.remove(tempList.size() - 1);
        }
    }
}
