package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Variable;

import oop.ex6.Exceptions.IllegalCodeException;
import oop.ex6.Scopes.Variable;

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
                    try{
                        Double.parseDouble(value);
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalCodeException();
                    }
                }
            }
            case(STRING):{
                if (!value.matches(STRING_VALUE_REGEX)) {
                    throw new IllegalCodeException();
                }
            }
            case(DOUBLE):{
                try{
                    Double.parseDouble(value);
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
                    Integer.parseInt(value);
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
