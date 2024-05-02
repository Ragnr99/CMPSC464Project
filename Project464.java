// Group Partners: Nicholas Lubold, Charles Lombardo, Eugene Sosa

// args[0] assumes a valid dfa or nfa input file, it will automatically determine which one it is
// args[1] can be either a string or text file where each row corresponds to a string

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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

            // Check if contains the right number of transitions for a DFA
            boolean notEnoughStates = false;
            if(numStates * alphabet.length() == numTransition) {
                notEnoughStates = true;
                isDFA = false;
                System.out.println("The automata does not contain enough transitions. \nIt is an NFA");
            }

            if (isDFA) {
                System.out.println("The automata does not have epsilon transitions or multiple transitions from a state for the same symbol. \nIt is a DFA.");
            } else {
                // Convert from NFA to DFA
                //      need new: startState, acceptStates, transitions

                // Initialize new data structures for DFA
                List<List<String>> newStartStates = new ArrayList<>();
                List<List<String>> newAcceptStates = new ArrayList<>();
                List<String[]> newTransitions = new ArrayList<>();

                // Generate subsets
                List<List<String>> subsets = generateSubsets(States);

                // Calculate new start states
                List<String> initialStartState = Arrays.asList(startState);
                for (List<String> subset : subsets) {
                    if (!Collections.disjoint(subset, initialStartState)) {
                        newStartStates.add(subset);
                    }
                }

                // Calculate new accept states
                for (List<String> subset : subsets) {
                    for (String acceptState : acceptStates) {
                        if (subset.contains(acceptState)) {
                            newAcceptStates.add(subset);
                            break;
                        }
                    }
                }

                // Calculate new transitions
                for (List<String> subset : subsets) {
                    for (String symbol : alphabetSet) {
                        List<String> nextState = calculateNextState(subset, symbol, transitions);
                        if (!nextState.isEmpty()) {
                            newTransitions.add(new String[]{subset.toString(), symbol, nextState.toString()});
                        }
                    }
                }

                // Output new start states, accept states, and transitions
                System.out.println("New Start States:");
                for (List<String> state : newStartStates) {
                    System.out.println(state);
                }
                System.out.println("New Accept States:");
                for (List<String> state : newAcceptStates) {
                    System.out.println(state);
                }
                System.out.println("New Transitions:");
                for (String[] transition : newTransitions) {
                    System.out.println(Arrays.toString(transition));
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
    

    public static boolean DFAAccept(String[] alphabet, String startState, String[] acceptStates, String[][] transitions, String w) {
        String currentState = startState;

        for (int i = 0; i < w.length(); i++) {
            String symbol = String.valueOf(w.charAt(i));
            boolean transitionFound = false;
            // Check if the current state has a transition on the current input symbol
            for (int j = 0; j < transitions.length; j++) {
                if (currentState.equals(transitions[j][0]) && symbol.equals(transitions[j][2])) {
                    // Once found, set the current state to the appropriate state
                    currentState = transitions[j][1];
                    transitionFound = true;
                    break;
                }
            }
            // If no transition is found, reject the input
            if (!transitionFound) {
                return false;
            }
        }

        // When all characters in the string have been processed, check if the current state is an accept state
        for (String acceptState : acceptStates) {
            if (currentState.equals(acceptState)) {
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

    private static List<String> calculateNextState(List<String> currentState, String symbol, String[][] transitions) {
        List<String> nextState = new ArrayList<>();
        for (String state : currentState) {
            for (String[] transition : transitions) {
                if (transition[0].equals(state) && transition[2].equals(symbol)) {
                    // Add the destination state of the transition to the next state
                    nextState.add(transition[1]);
                }
            }
        }
        // Perform ε-closure on the next state
        nextState.addAll(epsilonClosure(nextState, transitions));
        return nextState;
    }


    private static List<String> epsilonClosure(List<String> states, String[][] transitions) {
        List<String> epsilonClosure = new ArrayList<>(states);
        for (String state : states) {
            for (String[] transition : transitions) {
                if (transition[0].equals(state) && transition[2].equals("EPSILON")) {
                    // Add the destination state of the ε-transition to the epsilon closure
                    epsilonClosure.add(transition[1]);
                }
            }
        }
        return epsilonClosure;
    }


}
