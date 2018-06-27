package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

import static oop.ex6.main.Sjavac.*;

/**Checks that there are no scope-related issues in a given scope. Also checks validity of variables and
 * methods in a scope*/
public class MainScopeChecker extends ScopeChecker {


    /**
     * Builds a main scope, containing all the lines in the program
     * @param lines the lines in the program file
     * @return a Scope object representing the main scope
     */
    static Scope buildMainScope(String[] lines){
        return new Scope(new ArrayList<>(Arrays.asList(lines)));
    }

    /**
     * Checks the legality of the main scope, throwing an exception if any scope, variable, or method
     * related errors exist
     * @param scope the main scope of the program
     * @throws IllegalCodeException if any illegal code is detected
     */
    static void checkMainScope(Scope scope) throws IllegalCodeException{
        int bracketBalance = STARTING_BRACKET_BALANCE;
        ArrayList<String> lines = scope.getLines();
        ArrayList<String> tempSubscope = new ArrayList<>();

        for(String line : lines){
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
                    if (firstWord.matches(VARIABLE_DECLARATION_WORDS_REGEX)) {
                        checkVariableDeclaration(line, scope);
                    }

                    //Nested scope (method declaration)
                    else if (line.matches(OPENING_BRACKET_REGEX)) {
                        if (!isScopeMethod(line))
                            throw new IllegalCodeException();
                        else {
                            bracketBalance++;
                            tempSubscope.add(line);
                            String methodName = null;
                            Matcher methodDeclarationMatcher = METHOD_PATTERN.matcher(line);
                            if (methodDeclarationMatcher.find()) {
                                methodName = methodDeclarationMatcher.group(NAME_CAPTURING_GROUP);

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
                        checkVariableAssignment(line, scope);
                    }
                }
                    else {
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
