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

    /** List of global variables shared by all scopes*/
    private static ArrayList<Variable> globals;

    /** The parent scope of the scope. */
    private Scope parentScope;

    /** The subscopes nested in this scope. */
    private ArrayList<String>[] subScopes;

    public Scope(ArrayList<String> lines){
        scopeLines = lines;
    }

    public Scope(ArrayList<String> lines, Scope parent){
        scopeLines = lines;
        parentScope = parent;
    }

    public ArrayList<String> getLines(){
        return scopeLines;
    }

    public Scope getParentScope(){
        return parentScope;
    }

    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public void addVariable(Variable variable) throws IllegalCodeException {
        if(isExistingVariable(variable.getName()) != null){
            throw new IllegalCodeException();
        }
        else {
            VariableChecker.checkVariable(variable);
            variables.add(variable);
        }
    }

    public Variable isExistingVariable(String name){
        Scope currentScope = this;
        while(currentScope != null) {
            System.out.println();
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

    public void addSubscope(ArrayList<String> scopeLines){

    }


}
