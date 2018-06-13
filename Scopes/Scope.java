package ex6.Scopes;

import java.util.ArrayList;

/**
 * Scope class represents a scope block in the code.
 * Keeps track of elements in the scope and its parent.
 */
public class Scope {
    /** List of the data elements in the scope. */
    ArrayList<DataElement> dataElements;

    /** List of the sub-scopes of the scope. */
    ArrayList<Scope> subScopes;

    /** The parent scope of the scope. */
    Scope parentScope;
}
