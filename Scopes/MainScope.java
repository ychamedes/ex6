package ex6.Scopes;

import java.util.ArrayList;

/**
 * MainScope class represents the main (most general) scope of the code.
 * Maintains all the global variables of the code.
 */
public class MainScope extends Scope {
    /** A list of the global variables of the code.*/
    ArrayList<Variable> globalVariables;
}
