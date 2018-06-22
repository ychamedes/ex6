package ex6.main;

import ex6.Exceptions.IllegalCodeException;
import ex6.Scopes.Scope;
import ex6.Scopes.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import static ex6.main.Sjavac.*;

public class MainScopeChecker extends ScopeChecker {

    Scope buildMainScope(String[] lines){
        return new Scope(new ArrayList<String>(Arrays.asList(lines)));
    }


    void checkMainScope(Scope scope) throws IllegalCodeException{
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
                    Matcher variableBeginningMatcher = variableStartPattern.matcher(line);
                    String type = variableBeginningMatcher.group(x);
                    boolean isFinal = variableBeginningMatcher.group(FIRST_WORD_INDEX).equals(FINAL);

                    Matcher variableNameMatcher = variableNamePattern.matcher(line);

                    while(variableNameMatcher.find()){
                        Matcher variableAssignmentMatcher = variableAssignmentPattern.matcher
                                (variableNameMatcher.group());
                        String value = variableAssignmentMatcher.group();
                        scope.addGlobalVariable(new Variable(type, variableNameMatcher.group(), value,
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
