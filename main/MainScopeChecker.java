package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Scope;
import oop.ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import static oop.ex6.main.Sjavac.*;

/**
 * MainScopeChecker checks that there are no violations of scope in the main scope of the code.
 * Keeps track of global variables, and ensures that there no method calls or "stray" if/while statements.
 */

public class MainScopeChecker extends ScopeChecker {

    private final static int TYPE_CAPTURING_GROUP = 3;

    static Scope buildMainScope(String[] lines){
        return new Scope(new ArrayList<>(Arrays.asList(lines)));
    }

    static void checkMainScope(Scope scope) throws IllegalCodeException{
        int bracketBalance = STARTING_BRACKET_BALANCE;
        ArrayList<String> lines = scope.getLines();
        ArrayList<String> tempSubscope = new ArrayList<>();

        for(String line : scope.getLines()){
            Matcher blankLineMatcher = BLANK_LINE_PATTERN.matcher(line);
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
            if(!(blankLineMatcher.matches() || commentMatcher.matches())) {
                if (bracketBalance == 0) {

                    String firstWord = null;
                    Matcher firstWordMatcher = FIRST_WORD_PATTERN.matcher(line);
                    if (firstWordMatcher.find()) {
                        firstWord = firstWordMatcher.group();
                    }

                    //Variable declaration
                    if (firstWord.matches(VARIABLE_RESERVED_REGEX)) {
                        Matcher variableBeginningMatcher = VARIABLE_PATTERN.matcher(line);
                        String type = null;
                        boolean isFinal = false;
                        if (variableBeginningMatcher.matches()) {
                            type = variableBeginningMatcher.group(TYPE_CAPTURING_GROUP);
                            isFinal = variableBeginningMatcher.group(FINAL_CAPTURING_GROUP) != null;
                        }

                        Matcher variableNameMatcher = VARIABLE_START_PATTERN.matcher(line);

                        while (variableNameMatcher.find()) {
                            Matcher variableAssignmentMatcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(variableNameMatcher.group());
                            String value = variableAssignmentMatcher.group(VALUE_CAPTURING_GROUP);
                            scope.addVariable(new Variable(type, variableNameMatcher.group(), value, isFinal));
                        }
                    }

                    //Nested scope
                    else if (line.matches(OPENING_BRACKET_REGEX)) {
                        if (!isScopeMethod(line))
                            throw new IllegalCodeException();
                        else {
                            bracketBalance++;
                            tempSubscope.add(line);
                            String methodName = null;
                            Matcher methodDeclarationMatcher = METHOD_PATTERN.matcher(line);
                            if (methodDeclarationMatcher.matches()) {
                                methodName = methodDeclarationMatcher.group(1);
                            }
                            if (isExistingMethod(methodName)) {
                                throw new IllegalCodeException();
                            } else {
                                methods.add(methodName);
                            }
                        }
                    }

                    //Variable reassignment
                    else {
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
                            ArrayList<String> childScopeLines = new ArrayList<>(tempSubscope);
                            scopeStack.push(new Scope(childScopeLines, scope));
                            tempSubscope.clear();
                        }
                    }
                }

                while (!scopeStack.empty()) {
                    checkScope(scopeStack.pop());
                }
            }
        }
        if(bracketBalance != STARTING_BRACKET_BALANCE){
            throw new IllegalCodeException();
        }
    }

}
