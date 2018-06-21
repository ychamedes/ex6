package oop.ex6.Scopes;

import ex6.Exceptions.IllegalCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * Scope class represents a scope block in the code.
 * Keeps track of elements in the scope and its parent.
 */
public class Scope {
    private ArrayList<String> scopeLines;

    /** List of the data elements in the scope. */
    private HashMap<String, String> variables;

    /** List of global variables shared by all scopes*/
    private static HashMap<String, String> globals;

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

    public HashMap<String, String> getVariables(){
        return variables;
    }

    public void addVariable(String type, String name, String value) throws IllegalCodeException{

    }

    public void addSubscope(ArrayList<String>)


}
