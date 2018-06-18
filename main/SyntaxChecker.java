package ex6.main;

import com.sun.deploy.util.ArrayUtil;
import ex6.Exceptions.IllegalCodeException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SyntaxChecker class checks that there are no violations of syntax in the code.
 * Verifies that there is no "stray text", that reserved words are correctly spelled, and more.
 */
public class SyntaxChecker {

    static void checkSyntax(String[][] lines) throws IllegalCodeException{
        for (String[] line : lines){
            if (!blankLineCheck(line) && !commentLineCheck(line)){
                // If line ender at end of line
                // If reserved word at beginning of line
                if (isReservedWord(line[0])) {
                            String variableNameRegex = "_\\w+|[a-zA-Z]\\w*";
                            String variableTypeRegex = "int|double|String|boolean|char";
                            String variableInitializationRegex = "\\=\\s*(\\w+)";
                            String variableDeclarationRegex = "/^\\s*((final)\\s+)?("+variableTypeRegex+")\\s+("+
                                    variableNameRegex+"\\s*("+variableInitializationRegex+")?\\,?\\s*)+\\;\\s*$/";
                             /**
                             * Variable declaration syntax:
                             * 1. reserved variable type name
                             * 2. a. valid variable name (contains: a-zA-Z0-9_, starts with a-z, A-Z, _, if starts with _ contains at least one more character
                             *    b. followed by = or , or nothing
                             *         =. followed by any valid value
                             *         ,. followed by another valid variable name, etc
                             *    c. Ending with ;
                             */
                            final Pattern pattern = Pattern.compile(variableNameRegex+declarationOperator);
                            final Matcher matcher = pattern.matcher(line);


                    }
                }
            }
        }
    }


    /**
     * Checks if a line is empty (containing only white space)
     * @param line the line to be checked
     */
    private static boolean blankLineCheck(String[] line){

    }

    /**
     * Checks if the line is a valid comment (Beginning with //)
     * @param line the line to be checked
     */
    private static boolean commentLineCheck(String[] line){

    }

    /**
     * Checks if a line has the correct format for a line of code (ends in ;, {, or } )
     * @param line the line to be checked
     */
    private static void codeLineCheck(String[] line) throws IllegalCodeException{

    }

    private static boolean isReservedWord(String word){
        return Arrays.asList(Sjavac.RESERVED_WORDS).contains(word);
    }




}

