package ex6.Scopes;

import ex6.Exceptions.IllegalCodeException;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Scope class represents a scope block in the code.
 * Keeps track of elements in the scope and its parent.
 */
public class Scope {
    private ArrayList<String> scopeLines;

    /** List of the data elements in the scope. */
    private ArrayList<Variable> variables;

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

    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public void addVariable(Variable variable) throws IllegalCodeException{

    }

    public void addGlobalVariable(Variable variable) throws IllegalCodeException{

    }

    public void addSubscope(ArrayList<String>)


}
