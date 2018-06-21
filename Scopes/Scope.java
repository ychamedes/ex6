package oop.ex6.Scopes;

import java.util.HashMap;
import java.util.Queue;

/**
 * Scope class represents a scope block in the code.
 * Keeps track of elements in the scope and its parent.
 */
public class Scope {
    /** List of the data elements in the scope. */
    private HashMap<String, String> variables;

    /** List of global variables shared by all scopes*/
    private static HashMap<String, String> globals;

    /** List of the sub-scopes of the scope. */
    private Queue<Scope> subScopes;

    /** The parent scope of the scope. */
    private Scope parentScope;


    public Scope(HashMap<String, String> mainScopeVariables, Queue<Scope> subScopes){
        globals = mainScopeVariables;
        this.subScopes = subScopes;
    }

    public Scope(HashMap<String, String> localVariables, Queue<Scope> subScopes, Scope parent){
        variables = localVariables;
        this.subScopes = subScopes;
        parentScope = parent;
    }
}
