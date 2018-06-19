package ex6.main;

import ex6.Exceptions.IllegalCodeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SyntaxChecker class checks that there are no violations of syntax in the code.
 * Verifies that there is no "stray text", that reserved words are correctly spelled, and more.
 */
public class SyntaxChecker {

    private final static String variableReservedRegex = "int|double|String|boolean|char";
    private final static String otherReservedRegex = "void|final|if|while|true|false|return";
    private final static String variableNameRegex = "_\\w+|[a-zA-Z]\\w*";

    static void checkSyntax(String[] lines) throws IllegalCodeException{
        for (String line : lines){
            if (!blankLineCheck(line) && !commentLineCheck(line)){
                if (!endOfLineCheck(line)){
                    throw new IllegalCodeException();
                }
                if (reservedWordAtStart(line)){
                    String firstWord = line.substring(0, line.indexOf(" "));
                    // Variable declaration
                    if (firstWord.matches(variableReservedRegex+"|final") && !variableSyntaxCheck(line)){
                        throw new IllegalCodeException();
                    }
                    // If/While block
                    if (firstWord.matches("if|while") && !ifWhileSyntaxCheck(line)){
                        throw new IllegalCodeException();
                    }
                    // Method declaration
                    if (firstWord.equals("void") && !methodSyntaxCheck(line)){
                        throw new IllegalCodeException();
                    }
                    // Check that if,while,method are closed.
                    // Check for any other type of line (a = b, or illegal lines)
                }
            }
        }
    }


    /**
     * Checks if a line is empty (containing only white space)
     * @param line the line to be checked
     * @return true if the line is empty
     */
    private static boolean blankLineCheck(String line){
        Pattern p = Pattern.compile("\\s*");
        Matcher m = p.matcher(line);
        return m.matches();
    }

    /**
     * Checks if the line is a valid comment (Beginning with //)
     * @param line the line to be checked
     * @return true if the line is a comment
     */
    private static boolean commentLineCheck(String line){
        Pattern p = Pattern.compile("//");
        Matcher m = p.matcher(line);
        return m.lookingAt();
    }

    /**
     * Checks if line begins with a reserved word.
     * @param line the line to be checked
     * @return true if the line begins with a reserved word
     */
    private static boolean reservedWordAtStart(String line){
        Pattern p = Pattern.compile("\\s*("+variableReservedRegex+"|"+otherReservedRegex+")");
        Matcher m = p.matcher(line);
        return m.lookingAt();
    }

    /**
     * Checks if line follows the correct format of variable declaration.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean variableSyntaxCheck(String line){
        String variableInitializationRegex = "\\=\\s*(\\w+)";

        Pattern pattern = Pattern.compile("\\s*((final)\\s+)?("+variableReservedRegex+")\\s+("+
                variableNameRegex+"\\s*("+variableInitializationRegex+")?\\,?\\s*)+\\;\\s*");
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if line follows the correct format of an if/while statement.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean ifWhileSyntaxCheck(String line){
        Pattern pattern = Pattern.compile("\\s*(if|while)\\s*\\(\\s*(\\"+variableNameRegex+"\\s*(\\|\\||&&))*\\s*"+
                variableNameRegex+"\\s*\\)\\s*\\{\\s*");
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if line follows the correct format of method declaration.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean methodSyntaxCheck(String line){
        String methodNameRegex = "[a-zA-Z]\\w*";
        Pattern pattern = Pattern.compile("\\s*void\\s+("+methodNameRegex+")\\s*\\(\\s*(("+
                variableReservedRegex+")\\s+("+variableNameRegex+"),)*\\s*(("+variableReservedRegex+")\\s+("+
                variableNameRegex+"))\\s*\\)\\s*\\{\\s*");
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if a line has the correct format for a line of code (ends in ;, {, or } )
     * @param line the line to be checked
     * @return true if line ends correctly
     */
    private static boolean endOfLineCheck(String line){
        Pattern pattern = Pattern.compile("[;{}]$");
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }
}

