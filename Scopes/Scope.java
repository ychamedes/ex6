package oop.ex6.Scopes;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.main.VariableChecker;

import java.util.ArrayList;

/**
 * Scope class represents a scope block in the code.
 * Keeps track of elements in the scope and its parent.
 */
public class Scope {
    private ArrayList<String> scopeLines;

    /** List of the data elements in the scope. */
    private ArrayList<Variable> variables = new ArrayList<>();

    /** The parent scope of the scope. */
    private Scope parentScope;

    /**
     * A constructor for the Scope class
     * @param lines the lines in the scope
     */
    public Scope(ArrayList<String> lines){
        scopeLines = lines;
    }

    /**
     * A constructor for the Scope class
     * @param lines the lines in the scope
     * @param parent the parent scope
     */
    public Scope(ArrayList<String> lines, Scope parent){
        scopeLines = lines;
        parentScope = parent;
    }


    /**
     *
     * @return the lines in the scope
     */
    public ArrayList<String> getLines(){
        return scopeLines;
    }

    /**
     *
     * @return the parent of this scope
     */
    public Scope getParentScope(){
        return parentScope;
    }

    /**
     *
     * @return the variables in the scope
     */
    public ArrayList<Variable> getVariables(){
        return variables;
    }

    /**
     * Checks if a variable is legal, and if so adds it to the scope's variables
     * @param variable the Variable to be added
     * @throws IllegalCodeException if the variable is illegal
     */
    public void addVariable(Variable variable) throws IllegalCodeException {
        if(isExistingVariable(variable.getName()) != null){
            throw new IllegalCodeException();
        }
        else {
            VariableChecker.checkVariable(variable);
            variables.add(variable);
        }
    }

    /**
     * Checks if a Variable exists in the scope, and returns the existing Variable
     * @param name the name of the variable to check
     * @return a Variable object if one of the same name exists, and false otherwise
     */
    public Variable isExistingVariable(String name){
        Scope currentScope = this;
        while(currentScope != null) {
            ArrayList<Variable> currentVariables = currentScope.getVariables();
            if (currentVariables != null) {
                for (Variable existingVar : currentVariables) {
                    if (existingVar.getName().equals(name)) {
                        return existingVar;
                    }
                }
            }
            currentScope = currentScope.getParentScope();
        }
        return null;
    }

}
