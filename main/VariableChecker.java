package ex6.main;

import ex6.Exceptions.IllegalCodeException;
import ex6.Scopes.Variable;
import ex6.main.*;

public class VariableChecker {

    private static final String BOOLEAN = "boolean";
    private static final String STRING = "String";
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";


    private static final String BOOLEAN_VALUE_REGEX = "true|false";
    private static final String STRING_VALUE_REGEX = "^\"[\\w\\s]*\"$";
    private static final String CHAR_VALUE_REGEX = "^\'[\\w]'$";



    public static void checkVariable(Variable variable) throws IllegalCodeException{
        String value = variable.getValue();
        String type = variable.getType();

        switch(type){
            case(BOOLEAN): {
                if (!value.matches(BOOLEAN_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }
            }
            case(STRING):{
                if (!value.matches(STRING_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }
            }
            case(DOUBLE):{
                try{
                    double parsedValue = Double.parseDouble(value);
                }
                catch (NumberFormatException numE){
                    throw new IllegalCodeException();
                }

            }
            case(CHAR):{
                if (!value.matches(CHAR_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }

            }
            case(INT):{
                try{
                    int parsedValue = Integer.parseInt(value);
                }
                catch (NumberFormatException numE){
                    throw new IllegalCodeException();
                }
            }
            default:{
                throw new IllegalCodeException();
            }
        }

    }
}
