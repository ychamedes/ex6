package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Scope;
import oop.ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;

import static oop.ex6.main.Sjavac.*;

/**
 * ScopeChecker checks that there are no violations of scope in the code.
 * Verifies that variables and methods are not used outside of their scope or declared in the wrong place.
 */
public class ScopeChecker {

    protected final static String VOID = "void";
    protected final static String FINAL = "final";

    protected final static String VARIABLE_RESERVED_REGEX = "int|double|String|boolean|char";
    protected final static String OPENING_BRACKET_REGEX = ".*\\{\\s*";
    protected final static String CLOSING_BRACKET_REGEX = "\\s*}\\s*";


    protected final static int STARTING_BRACKET_BALANCE = 0;
    protected final static int FINAL_CAPTURING_GROUP = 2;
    private final static int FIRST_LINE_INDEX = 0;
    private final static int LAST_LINE_INDEX = -2;
    private final static int RETURN_LINE_INDEX = -3;
    private final static int CONDITION_CAPTURING_GROUP = 2;
    private final static int TYPE_CAPTURING_GROUP = 3;
    protected final static int NAME_CAPTURING_GROUP = 2;
    protected final static int VALUE_CAPTURING_GROUP = 3;
    private final static int MINIMUM_METHOD_LINES = 3;



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
            if(lines.size() <= MINIMUM_METHOD_LINES){
                throw new IllegalCodeException();
            }
            else {
                methodEndingChecker(lines.get(lines.size() + RETURN_LINE_INDEX), lines.get(lines.size() + LAST_LINE_INDEX));
            }
        }

        for (String line : scope.getLines()) {
            if (bracketBalance == 0) {
                String firstWord = null;
                Matcher firstWordMatcher = FIRST_WORD_PATTERN.matcher(line);
                if(firstWordMatcher.find()) {
                    firstWord = firstWordMatcher.group();
                }

                //Variable declaration
                if (firstWord.matches(VARIABLE_RESERVED_REGEX)) {
                    Matcher variableBeginningMatcher = VARIABLE_START_PATTERN.matcher(line);
                    String type = variableBeginningMatcher.group(TYPE_CAPTURING_GROUP);
                    boolean isFinal = variableBeginningMatcher.group(FINAL_CAPTURING_GROUP).equals(FINAL);

                    Matcher variableNameMatcher = VARIABLE_PATTERN.matcher(line);

                    while(variableNameMatcher.find()){
                        Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(variableNameMatcher.group());
                        String value = variableAssignmentMatcher.group(VALUE_CAPTURING_GROUP);
                        scope.addVariable(new Variable(type, variableNameMatcher.group(), value, isFinal));
                    }
                }


                //Nested scope
                else if (line.matches(OPENING_BRACKET_REGEX)) {
                    if(isScopeMethod(line))
                        throw new IllegalCodeException();
                        // if/while block
                    else if (firstWord.matches(CONTROL_FLOW_REGEX)){
                        Matcher controlFlowMatcher = CONTROL_FLOW_PATTERN.matcher(line);
                        if(controlFlowMatcher.matches()){
                            conditionChecker(controlFlowMatcher.group(CONDITION_CAPTURING_GROUP), scope);
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

//                //Method call
//                else if (line.matches(METHOD_CALL_REGEX)) {
//                    Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(line);
//                }

                //Variable reassignment
                else{
                    Matcher variableReassignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(line);
                    if(variableReassignmentMatcher.find()){
                        String varName = variableReassignmentMatcher.group(NAME_CAPTURING_GROUP); //Check
                        // capturing group numbers!
                        String varValue = variableReassignmentMatcher.group(VALUE_CAPTURING_GROUP);
                        if(varValue.matches(VARIABLE_NAME_REGEX)){
                            Variable assigningVar = scope.isExistingVariable(varValue);
                            if(assigningVar == null || assigningVar.getValue() == null){
                                throw new IllegalCodeException();
                            }
                            else{
                                varValue = assigningVar.getValue();
                            }
                        }
                        Variable oldVar = scope.isExistingVariable(varName);
                        if(oldVar == null || oldVar.isVariableFinal()){
                            throw new IllegalCodeException();
                        }
                        else{
                            oldVar.setValue(varValue);
                            VariableChecker.checkVariable(oldVar);
                        }
                    }
                    else{
                        throw new IllegalCodeException();
                    }
                }

            } else {
                tempSubscope.add(line);
                if (line.matches(OPENING_BRACKET_REGEX)) {
                    bracketBalance++;
                }
                if (line.matches(CLOSING_BRACKET_REGEX)) {
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

    private static void conditionChecker(String condition, Scope currentScope) throws IllegalCodeException{
        Matcher conditionMatcher = CONDITION_PATTERN.matcher(condition);
        while(conditionMatcher.find()){
            String currentCondition = conditionMatcher.group();
            if(!currentCondition.matches(BOOLEAN_REGEX)){
                try{
                    Double.parseDouble(currentCondition);
                }
                catch (NumberFormatException numE){
                    throw new IllegalCodeException();
                }
                Variable existingVariable = currentScope.isExistingVariable(currentCondition);
                if(existingVariable == null || existingVariable.getValue() == null){
                    throw new IllegalCodeException();
                }
                else{
                    if(!existingVariable.getType().matches(BOOLEAN_TYPE_REGEX)){
                        throw new IllegalCodeException();
                    }
                }
            }
        }
    }

    protected static boolean isScopeMethod(String firstLine){
        Matcher methodMatcher = METHOD_PATTERN.matcher(firstLine);
        return methodMatcher.matches();
    }

//    private static void methodEndingChecker(String returnLine, String closingLine) {
//        if (!(returnLine.matches(RETURN_REGEX) && closingLine.matches(Sjavac.CLOSING_BRACKET_REGEX)));
//    }

    private static void methodEndingChecker(String returnLine, String closingLine) throws IllegalCodeException{
        Matcher returnMatcher = RETURN_PATTERN.matcher(returnLine);
        Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(closingLine);
        if(!(returnMatcher.matches() && closingMatcher.matches())){
            throw new IllegalCodeException();
        }
    }

    protected static boolean isExistingMethod(String methodName){
        return methods.contains(methodName);
    }

}
