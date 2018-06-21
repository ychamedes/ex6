package oop.ex6.main;

import ex6.Exceptions.IllegalCodeException;
import ex6.Scopes.Scope;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ScopeChecker checks that there are no violations of scope in the code.
 * Verifies that variables and methods are not used outside of their scope or declared in the wrong place.
 */
public class ScopeChecker {

    private final static String VOID = "void";
    private final static String variableReservedRegex = "int|double|String|boolean|char";
    private final static String variableNameRegex = "_\\w+|[a-zA-Z]\\w*";

    private Stack<Scope> scopeStack= new Stack<Scope>();

    Scope buildMainScope(String[] lines){
        return new Scope(lines);
    }

    Scope buildScope(String[] lines, Scope parent){
        return new Scope(lines, parent);
    }

    void checkScope(Scope scope) throws IllegalCodeException{
        int bracketBalance = 0;
        ArrayList<String> tempSubscope = new ArrayList<String>();

        for(String line : scope.getLines()){
            if(bracketBalance == 0) {
                String firstWord = line.substring(0, line.indexOf(" "));

                //Variable declaration
                if (firstWord.matches(variableReservedRegex)) {
                    String type = firstWord;
                    String name;// = Capture second word;
                    String value;// = Capture value word;
                    scope.addVariable(type, name, value);
                }

                //Method declaration
                else if (firstWord.equals(VOID)) {
                    //Make sure syntax checks opening bracket for method
                    bracketBalance++;
                    tempSubscope.add(line);
                }
            }
            else{
                tempSubscope.add(line);
                if(true/* line ends in opening bracket { */){
                    bracketBalance++;
                }
                if(true/* line is closing bracket } */){
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
    }


}
