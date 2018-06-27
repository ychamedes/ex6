package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Method;
import oop.ex6.Scopes.Scope;
import oop.ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;

import static oop.ex6.main.Sjavac.*;

/**
 * Verifies that variables and methods are not used outside of their scope or declared in the wrong place,
 * and that no scope-related issues exist in a given scope.
 */
class ScopeChecker {


    final static String OPENING_BRACKET_REGEX = ".*\\{\\s*";
    final static String CLOSING_BRACKET_REGEX = "\\s*}\\s*";


    final static int STARTING_BRACKET_BALANCE = 0;
    private final static int FINAL_CAPTURING_GROUP = 2;
    private final static int FIRST_LINE_INDEX = 0;
    private final static int LAST_LINE_INDEX = -1;
    private final static int RETURN_LINE_INDEX = -2;
    private final static int CONDITION_CAPTURING_GROUP = 2;
    private final static int TYPE_CAPTURING_GROUP = 3;
    final static int NAME_CAPTURING_GROUP = 2;
    private final static int VALUE_CAPTURING_GROUP = 4;
    private final static int MINIMUM_METHOD_LINES = 3;


    /**
     * A Stack object that stores Scopes that are yet to be checked
     */
    static Stack<Scope> scopeStack = new Stack<>();

    /**
     * An ArrayList of the methods declared in the main scope
     */
    //protected static ArrayList<Method> methods = new ArrayList<>();
    static ArrayList<String> methods = new ArrayList<>();


    /**
     * Check a given Scope for all variable, method, and scope-related issues.
     *
     * @param scope the scope to be checked
     * @throws IllegalCodeException if any illegal code is detected
     */
    static void checkScope(Scope scope) throws IllegalCodeException {
        //Tracks the number of opening and closing brackets
        int bracketBalance = STARTING_BRACKET_BALANCE;

        //Stores the lines of an inner scope
        ArrayList<String> tempSubscope = new ArrayList<>();

        ArrayList<String> lines = scope.getLines();

        //Ensure the scope has a valid ending
        if (lines.size() > 0) {
            if (isScopeMethod(lines.get(FIRST_LINE_INDEX))) {
                if (lines.size() <= MINIMUM_METHOD_LINES) {
                    throw new IllegalCodeException();
                } else {
                    methodEndingChecker(lines.get(lines.size() + RETURN_LINE_INDEX), lines.get(lines.size() + LAST_LINE_INDEX));
                }
            }
        }

        //Pass through the lines in the scope and analyze each one
        for (String line : lines) {

            Matcher blankLineMatcher = BLANK_LINE_PATTERN.matcher(line);
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line);

            if (!(blankLineMatcher.matches() || commentMatcher.matches())) {
                if (bracketBalance == 0) { //If we are in this scope
                    String firstWord = null;
                    Matcher firstWordMatcher = FIRST_WORD_PATTERN.matcher(line);
                    if (firstWordMatcher.find()) {
                        firstWord = firstWordMatcher.group();
                    }

                    //Variable declaration
                    if (firstWord.matches(VARIABLE_DECLARATION_WORDS_REGEX)) {
                        checkVariableDeclaration(line, scope);
                    }


                    //Nested scope (if/while)
                    else if (line.matches(OPENING_BRACKET_REGEX)) {
                        if (isScopeMethod(line)) {
                            if (scope.getParentScope().getParentScope() != null) {
                                throw new IllegalCodeException();
                            }
                        }

                        // if/while block
                        else if (firstWord.matches(CONTROL_FLOW_REGEX)) {
                            Matcher controlFlowMatcher = CONTROL_FLOW_PATTERN.matcher(line);
                            if (controlFlowMatcher.find()) {
                                conditionChecker(controlFlowMatcher.group(CONDITION_CAPTURING_GROUP), scope);
                            } else {
                                throw new IllegalCodeException();
                            }
                        } else {
                            System.out.println(firstWord);
                            throw new IllegalCodeException();
                        }
                        bracketBalance++;
                        if (line != lines.get(0)) {
                            tempSubscope.add(line);
                        }
                    }

                    //Method call
                    else if (line.matches(METHOD_CALL_REGEX)) {
                        Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(line);
                        if (methodCallMatcher.find()) {
                            String methodName = methodCallMatcher.group(NAME_CAPTURING_GROUP);
                            if (!isExistingMethod(methodName)) {
                                throw new IllegalCodeException();
                            }
                        }
                    }
                        //Variable reassignment
                        else {
                            checkVariableAssignment(line, scope);
                        }

                    } else { //If bracket balance is not 0, we are looking at a nested scope
                        if (line != lines.get(lines.size() - 1)) {
                            tempSubscope.add(line);
                        }
                        if (line.matches(OPENING_BRACKET_REGEX)) {
                            bracketBalance++;
                        }
                        if (line.matches(CLOSING_BRACKET_REGEX)) {
                            bracketBalance--;
                            if (bracketBalance == 0) { //End of scope
                                ArrayList<String> childScopeLines = new ArrayList<>(tempSubscope);
                                scopeStack.push(new Scope(childScopeLines, scope));
                                tempSubscope.clear();
                            }
                        }
                    }

                    while (!scopeStack.empty()) { //Check the next nested scope
                        checkScope(scopeStack.pop());
                    }
                }
            }
            if (bracketBalance != STARTING_BRACKET_BALANCE) {
                throw new IllegalCodeException();
            }
        }

        /**
         * Checks if a condition following an if/while statement is legal
         *
         * @param condition    the condition to be checked
         * @param currentScope the if/while scope containing the condition
         * @throws IllegalCodeException if the condition is illegal
         */
        private static void conditionChecker (String condition, Scope currentScope) throws IllegalCodeException {
            Matcher conditionMatcher = CONDITION_PATTERN.matcher(condition);

            boolean isNumber = true;
            boolean isExistingVariable = true;

            while (conditionMatcher.find()) {
                String currentCondition = conditionMatcher.group();
                if (!currentCondition.matches(BOOLEAN_REGEX)) {
                    try {
                        Double.parseDouble(currentCondition);
                    } catch (NumberFormatException numE) {
                        isNumber = false;
                    }
                    Variable existingVariable = currentScope.isExistingVariable(currentCondition);
                    if (existingVariable == null || existingVariable.getValue() == null) {
                        isExistingVariable = false;
                    } else {
                        if (!existingVariable.getType().matches(BOOLEAN_TYPE_REGEX) && !isNumber && !isExistingVariable) {
                            throw new IllegalCodeException();
                        }
                    }
                }
            }
        }

        /**
         * Identifies whether the current scope is a method declaration
         *
         * @param firstLine the first line of the scope
         * @return true if the scope is a method declaration, false otherwise
         */
        protected static boolean isScopeMethod (String firstLine){
            Matcher methodMatcher = METHOD_PATTERN.matcher(firstLine);
            return methodMatcher.matches();
        }

        /**
         * Checks whether a method declaration ends legally (with a return line followed by a closing bracket
         * line)
         *
         * @param returnLine  the second to last line of the scope
         * @param closingLine the last line of the scope
         * @throws IllegalCodeException if the method declaration ends illegally.
         */
        private static void methodEndingChecker (String returnLine, String closingLine) throws
        IllegalCodeException {
            Matcher returnMatcher = RETURN_PATTERN.matcher(returnLine);
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(closingLine);
            if (!(returnMatcher.matches() && closingMatcher.matches())) {
                throw new IllegalCodeException();
            }
        }

        /**
         * Identifies whether a given method is declared in the main scope
         *
         * @param methodName the name of the method to be checked
         * @return true if the method exists, false otherwise
         */
        protected static boolean isExistingMethod (String methodName){
            return methods.contains(methodName);
        }

        /**
         * Checks if a variable declaration statement is valid
         *
         * @param line  the line containing the declaration
         * @param scope the scope containing the line
         * @throws IllegalCodeException if the declaration contains illegal code
         */
        protected static void checkVariableDeclaration (String line, Scope scope) throws IllegalCodeException
        {
            Matcher variableBeginningMatcher = VARIABLE_PATTERN.matcher(line);
            String type = null;
            boolean isFinal = false;
            if (variableBeginningMatcher.find()) {
                type = variableBeginningMatcher.group(TYPE_CAPTURING_GROUP);
                isFinal = variableBeginningMatcher.group(FINAL_CAPTURING_GROUP) != null;
            }

            Matcher variableNameMatcher = VARIABLE_PATTERN.matcher(line);

            while (variableNameMatcher.find()) {
                Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(variableNameMatcher.group());
                if (variableAssignmentMatcher.find()) {
                    String value = variableAssignmentMatcher.group(VALUE_CAPTURING_GROUP);
                    scope.addVariable(new Variable(type, variableNameMatcher.group(), value,
                            isFinal));
                }
            }
        }

        /**
         * Checks if a variable reassignment statement is valid
         * @param line  the line containing the statement
         * @param scope the scope containing the line
         * @throws IllegalCodeException if the statement contains illegal code
         */
        protected static void checkVariableAssignment (String line, Scope scope) throws IllegalCodeException {
            Matcher variableReassignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(line);
            if (variableReassignmentMatcher.find()) {
                String varName = variableReassignmentMatcher.group(NAME_CAPTURING_GROUP);
                String varValue = variableReassignmentMatcher.group(VALUE_CAPTURING_GROUP);
                if (varValue.matches(VARIABLE_NAME_REGEX)) {
                    Variable assigningVar = scope.isExistingVariable(varValue);
                    if (assigningVar == null || assigningVar.getValue() == null) {
                        throw new IllegalCodeException();
                    } else {
                        varValue = assigningVar.getValue();
                    }
                }
                Variable oldVar = scope.isExistingVariable(varName);
                if (oldVar == null || oldVar.isVariableFinal()) {
                    throw new IllegalCodeException();
                } else {
                    oldVar.setValue(varValue);
                    VariableChecker.checkVariable(oldVar);
                }
            } else {
                if (!(line.matches(RETURN_REGEX) || line.matches(CLOSING_BRACKET_REGEX))) {
                    throw new IllegalCodeException();
                }
            }
        }

}

