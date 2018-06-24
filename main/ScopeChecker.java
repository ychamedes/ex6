package ex6.main;

import ex6.Exceptions.IllegalCodeException;
import ex6.Scopes.Scope;
import ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;

import static ex6.main.Sjavac.*;

/**
 * ScopeChecker checks that there are no violations of scope in the code.
 * Verifies that variables and methods are not used outside of their scope or declared in the wrong place.
 */
public class ScopeChecker {

    protected final static String VOID = "void";
    protected final static String FINAL = "final";

    protected final static String variableReservedRegex = "int|double|String|boolean|char";
    protected final static String openingBracketRegex = ".*\\{\\s*";
    protected final static String closingBracketRegex = "\\s*}\\s*";


    protected final static int STARTING_BRACKET_BALANCE = 0;
    protected final static int FIRST_WORD_INDEX = 1;
    private final static int FIRST_LINE_INDEX = 0;
    private final static int LAST_LINE_INDEX = -2;
    private final static int RETURN_LINE_INDEX = -3;
    private final static int CONDITION_CAPTURING_GROUP = 2;
    private final static int TYPE_CAPTURING_GROUP = 3;


    protected static Stack<Scope> scopeStack= new Stack<>();
    protected static ArrayList<String> methods = new ArrayList<>();

    Scope buildScope(String[] lines, Scope parent){
        return new Scope(new ArrayList<String>(Arrays.asList(lines)), parent);
    }


    static void checkScope(Scope scope) throws IllegalCodeException {
        int bracketBalance = STARTING_BRACKET_BALANCE;

        ArrayList<String> tempSubscope = new ArrayList<>();
        ArrayList<String> lines = scope.getLines();

        if(isScopeMethod(scope.getLines().get(FIRST_LINE_INDEX))){
            methodEndingChecker(lines.get(lines.size() + RETURN_LINE_INDEX), lines.get(lines.size() +
                    LAST_LINE_INDEX));
        }

        for (String line : scope.getLines()) {
            if (bracketBalance == 0) {
                String firstWord = null;
                Matcher firstWordMatcher = FIRST_WORD_PATTERN.matcher(line);
                if(firstWordMatcher.find()) {
                    firstWord = firstWordMatcher.group(FIRST_WORD_INDEX);
                }

                //Variable declaration
                if (firstWord.matches(variableReservedRegex)) {
                    Matcher variableBeginningMatcher = variableStartPattern.matcher(line);
                    String type = variableBeginningMatcher.group(TYPE_CAPTURING_GROUP);
                    boolean isFinal = variableBeginningMatcher.group(FIRST_WORD_INDEX).equals(FINAL);

                    Matcher variableNameMatcher = VARIABLE_PATTERN.matcher(line);

                    while(variableNameMatcher.find()){
                        Matcher variableAssignmentMatcher = variableAssignmentPattern.matcher(variableNameMatcher.group());
                        String value = variableAssignmentMatcher.group();
                        scope.addVariable(new Variable(type, variableNameMatcher.group(), value, isFinal));
                    }
                }


                //Nested scope
                else if (line.matches(openingBracketRegex)) {
                    if(isScopeMethod(line))
                        throw new IllegalCodeException();
                        // if/while block
                    else if (firstWord.matches(CONTROL_FLOW_REGEX)){
                        Matcher controlFlowMatcher = CONTROL_FLOW_PATTERN.matcher(line);
                        if(controlFlowMatcher.find()){
                            conditionChecker(controlFlowMatcher.group(CONDITION_CAPTURING_GROUP));
                        }
                        else{
                            throw new IllegalCodeException();
                        }
                    }
                    else {
                        bracketBalance++;
                        tempSubscope.add(line);
                    }
                }

            } else {
                tempSubscope.add(line);
                if (line.matches(openingBracketRegex)) {
                    bracketBalance++;
                }
                if (line.matches(closingBracketRegex)) {
                    bracketBalance--;
                    if (bracketBalance == 0) {
                        scopeStack.push(new Scope(tempSubscope, scope));
                        tempSubscope.clear();
                    }
                }
            }

            while (!scopeStack.empty()) {
                checkScope(scopeStack.pop());
            }
        }
        if (bracketBalance != STARTING_BRACKET_BALANCE) {
            throw new IllegalCodeException();
        }
    }

    private static void conditionChecker(String condition){

    }

    protected static boolean isScopeMethod(String firstLine){
        Matcher methodMatcher = METHOD_PATTERN.matcher(firstLine);
        return methodMatcher.matches();
    }

    private static void methodEndingChecker(String returnLine, String closingLine){
        if(!(returnLine.matches(RETURN_REGEX) && closingLine.matches(CLOSING_BRACKET_REGEX)));
    }

    protected static boolean isExistingMethod(String methodName){
        return methods.contains(methodName);
    }

}
