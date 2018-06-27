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

    public Scope(ArrayList<String> lines){
        scopeLines = lines;
    }

    public Scope(ArrayList<String> lines, Scope parent){
        scopeLines = lines;
        parentScope = parent;
    }

    public Scope(ArrayList<String> lines, Scope parent, ArrayList<Variable> parameters) throws IllegalCodeException{
        scopeLines = lines;
        parentScope = parent;
        for(Variable parameter : parameters){
            addVariable(parameter);
        }
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
