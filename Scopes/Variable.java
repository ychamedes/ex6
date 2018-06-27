package oop.ex6.Scopes;

/**
 * Variable class represents a variable
 */
public class Variable{

    /** Represents the type of the variable*/
    private String type;

    /** Represents the name of the variable*/
    private String name;

    /** Represents a value assigned to the variable */
    private String value;

    /** Represents if the variable is declared final or not. */
    private boolean isFinal;


    /**
     * A constructor for the Variable class
     * @param type the variable type
     * @param name the name of the variable
     * @param value the value assigned to the variable
     * @param isFinal true if the boolean is declared final
     */
    public Variable(String type, String name, String value, boolean isFinal){
        this.type = type;
        this.name = name;
        this.value = value;
        this.isFinal = isFinal;
    }

    /**
     *
     * @return the variable's type
     */
    public String getType(){
        return type;
    }

    /**
     *
     * @return the variable's name
     */
    String getName(){
        return name;
    }

    /**
     *
     * @return the variable's value
     */
    public String getValue(){
        return value;
    }

    /**
     * Change the value assigned to the variable
     * @param newValue the new value
     */
    public void setValue(String newValue){
        value = newValue;
    }

    /**
     *
     * @return true if the variable is final, false otherwise
     */
    public boolean isVariableFinal(){
        return isFinal;
    }

}
