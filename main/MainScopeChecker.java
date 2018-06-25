package ex6.main;

import ex6.Exceptions.IllegalCodeException;
import ex6.Scopes.Scope;
import ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import static ex6.main.Sjavac.*;

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
            if(bracketBalance == 0) {

                String firstWord = null;
                Matcher firstWordMatcher = FIRST_WORD_PATTERN.matcher(line);
                if(firstWordMatcher.find()) {
                    firstWord = firstWordMatcher.group(FIRST_WORD_INDEX);
                }

                //Variable declaration
                if (firstWord.matches(variableReservedRegex)) {
                    Matcher variableBeginningMatcher = VARIABLE_PATTERN.matcher(line);
                    String type = variableBeginningMatcher.group(TYPE_CAPTURING_GROUP);
                    boolean isFinal = variableBeginningMatcher.group(FIRST_WORD_INDEX).equals(FINAL);

                    Matcher variableNameMatcher = variableNamePattern.matcher(line);

                    while(variableNameMatcher.find()){
                        Matcher variableAssignmentMatcher = variableAssignmentPattern.matcher
                                (variableNameMatcher.group());
                        String value = variableAssignmentMatcher.group();
                        scope.addVariable(new Variable(type, variableNameMatcher.group(), value,
                                isFinal));
                    }
                }

                //Nested scope
                else if (line.matches(openingBracketRegex)) {
                    if(!isScopeMethod(line))
                        throw new IllegalCodeException();
                    else {
                        bracketBalance++;
                        tempSubscope.add(line);
                        String methodName = null;
                        Matcher methodDeclarationMatcher = METHOD_PATTERN.matcher(line);
                        if(methodDeclarationMatcher.matches()){
                            methodName = methodDeclarationMatcher.group(1);
                        }
                        if(isExistingMethod(methodName)){
                            throw new IllegalCodeException();
                        }
                        else{
                            methods.add(methodName);
                        }
                    }
                }

                //Variable reassignment
                else{
                    Matcher variableReassignmentMatcher = VARIABLE_REASSIGNMENT_PATTERN.matcher(line);
                    if(variableReassignmentMatcher.find()){
                        String varName = variableReassignmentMatcher.group(NAME_CAPTURING_GROUP); //Check
                        // capturing group numbers!
                        String varValue = variableReassignmentMatcher.group(VALUE_CAPTURING_GROUP);
                        if(varValue.matches(variableNamePattern)){ //Pattern should be string?
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
                }
            }
            else{
                tempSubscope.add(line);
                if(line.matches(openingBracketRegex)){
                    bracketBalance++;
                }
                if(line.matches(closingBracketRegex)){
                    bracketBalance--;
                    if(bracketBalance == 0){
                        scopeStack.push(new Scope(tempSubscope, scope));
                        tempSubscope.clear();
                    }
                }
            }

            while(!scopeStack.empty()){
                checkScope(scopeStack.pop());
            }
        }
        if(bracketBalance != STARTING_BRACKET_BALANCE){
            throw new IllegalCodeException();
        }
    }

}
