package ex6.Scopes;

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


    public Variable(String type, String name, String value, boolean isFinal){
        this.type = type;
        this.name = name;
        this.value = value;
        this.isFinal = isFinal;
    }

    public String getType(){
        return type;
    }

    String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }

}
