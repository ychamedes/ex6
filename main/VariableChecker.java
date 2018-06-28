package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Variable;

public class VariableChecker {

    /** Reserved word for a boolean variable*/
    private static final String BOOLEAN = "boolean";

    /** Reserved word for a string variable*/
    private static final String STRING = "String";

    /** Reserved word for an int variable*/
    private static final String INT = "int";

    /** Reserved word for a double variable*/
    private static final String DOUBLE = "double";

    /** Reserved word for a char variable*/
    private static final String CHAR = "char";

    private static final String BOOLEAN_VALUE_REGEX = "true|false";
    private static final String STRING_VALUE_REGEX = "^\"[\\w\\s]*\"$";
    private static final String CHAR_VALUE_REGEX = "^\'[\\w]'$";

    /**
     * Checks the legality of a variable
     * @param variable the Variable object to be checked
     * @throws IllegalCodeException if the Variable is illegal
     */
    public static void checkVariable(Variable variable) throws IllegalCodeException{
        String value = variable.getValue();
        String type = variable.getType();

        // Compare the type of the Variable with the value
        switch(type){
            case(BOOLEAN): {
                if (!value.matches(BOOLEAN_VALUE_REGEX)) {
                    try{
                        Double.parseDouble(value);
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalCodeException();
                    }
                }
                break;
            }
            case(STRING):{
                if (!value.matches(STRING_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }
                break;
            }
            case(DOUBLE):{
                try{
                    Double.parseDouble(value);
                }
                catch (NumberFormatException numE){
                    throw new IllegalCodeException();
                }
                break;
            }
            case(CHAR):{
                if (!value.matches(CHAR_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }
                break;
            }
            case(INT):{
                try{
                    Integer.parseInt(value);
                }
                catch (NumberFormatException numE){
                    throw new IllegalCodeException();
                }
                break;
            }
            default:{
                throw new IllegalCodeException();
            }
        }

    }
}
