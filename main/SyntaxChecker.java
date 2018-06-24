package ex6.main;

import ex6.Exceptions.IllegalCodeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex6.main.Sjavac.*;

/**
 * SyntaxChecker class checks that there are no violations of syntax in the code.
 * Verifies that there is no "stray text", that reserved words are correctly spelled, and more.
 */
public class SyntaxChecker {

    /** Regex options of reserved words that can begin variable declaration. */
    static final String VARIABLE_DECLARATION_WORDS_REGEX = "int|double|String|boolean|char|final";

    /** Regex options of reserved words the can begin method declaration. */
    static final String METHOD_DECLARATION_REGEX = "void";

    /** Reserved word for return statement in method. */
    static final String METHOD_RETURN_KEYWORD = "return";

    /** Pattern for a blank line in the code. */
    static final Pattern BLANK_LINE_PATTERN = Pattern.compile("\\s*");

    /** Pattern for a comment line in the code. */
    static final Pattern COMMENT_PATTERN = Pattern.compile("\\/\\/");

    /** Pattern for a reserved word. */
    static final Pattern RESERVED_WORD_PATTERN = Pattern.compile("^\\s*(int|double|String|boolean|char|final|void|if|while|true|false|return)");

    /** Pattern for valid non-reserved word sequences: closing curly brace, variable initialization, and method calling. */
    static final Pattern NON_RESERVED_PATTERN = Pattern.compile("\\s*(}|(_\\w+|[a-zA-Z]\\w*)\\s*=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*)\\s*;|([a-zA-Z]\\w*)\\s*\\(\\s*((_\\w+|[a-zA-Z]\\w*)\\s*)?(,\\s*(_\\w+|[a-zA-Z]\\w*)\\s*)*\\)\\s*;)\\s*");

    /** Pattern for the end of each line, which either be a semicolon, or open or closed curly brace. */
    static final Pattern END_OF_LINE_PATTERN = Pattern.compile("[;\\{\\}]\\s*$");

    /** Pattern for illegal tokens or sequence in the code: operators and alternate comment patterns. */
    static final Pattern ILLEGAL_TOKENS_PATTERN = Pattern.compile("\\/\\*{1,2}.*\\*\\/|[-\\+\\*]");

    static void checkSyntax(String[] lines) throws IllegalCodeException{
        for (String line : lines){
            if (!blankLineCheck(line) && !commentLineCheck(line)){
                /* Check every line for a valid end and invalid tokens. */
                if (!endOfLineCheck(line) || invalidTokenCheck(line)){
                    System.out.println("END?INVALID");
                    throw new IllegalCodeException();
                }
                Matcher m = FIRST_WORD_PATTERN.matcher(line);
                if(m.find()) {
                    String firstWord = m.group();

                    if (reservedWordAtStart(line)) {
                        /* Check every line according to a specific format, that it follows that format. */
                        // Variable declaration
                        if (firstWord.matches(VARIABLE_DECLARATION_WORDS_REGEX) && !variableSyntaxCheck(line)) {
                            System.out.println("VARIABLE");
                            throw new IllegalCodeException();
                        }
                        // Control Flow block
                        if (firstWord.matches(CONTROL_FLOW_REGEX) && !controlFlowSyntaxCheck(line)) {
                            System.out.println("FLOW");
                            throw new IllegalCodeException();
                        }
                        // Method declaration
                        if (firstWord.matches(METHOD_DECLARATION_REGEX) && !methodSyntaxCheck(line)) {
                            System.out.println("METHOD");
                            throw new IllegalCodeException();
                        }
                        // Return case
                        if (firstWord.matches(METHOD_RETURN_KEYWORD) && !returnLineCheck(line)) {
                            System.out.println("RETURN");
                            throw new IllegalCodeException();
                        }
                    }
                    // Check syntax of cases without reserved words
                    else if (!nonReservedWordCheck(line)) {
                        System.out.println("NON-RESERVED");
                        throw new IllegalCodeException();
                    }
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
        Matcher m = BLANK_LINE_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * Checks if the line is a valid comment (Beginning with //)
     * @param line the line to be checked
     * @return true if the line is a comment
     */
    private static boolean commentLineCheck(String line){
        Matcher m = COMMENT_PATTERN.matcher(line);
        return m.lookingAt();
    }

    /**
     * Checks if line begins with a reserved word.
     * @param line the line to be checked
     * @return true if the line begins with a reserved word
     */
    private static boolean reservedWordAtStart(String line){
        Matcher m = RESERVED_WORD_PATTERN.matcher(line);
        return m.find();
    }

    /**
     * Checks if line is a valid "non-reserved word" line, i.e. uses a method, initializes a variable,
     * or is a closing curly brace.
     * @param line the line to be checked.
     * @return true if the line is valid.
     */
    private static boolean nonReservedWordCheck(String line){
        Matcher m = NON_RESERVED_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * Checks if line follows the correct format of variable declaration.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean variableSyntaxCheck(String line){
        Matcher m = VARIABLE_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * Checks if line follows the correct format of an if/while statement.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean controlFlowSyntaxCheck(String line){
        Matcher m = CONTROL_FLOW_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * Checks if line follows the correct format of method declaration.
     * @param line the line to be checked.
     * @return true if the line follows the format.
     */
    private static boolean methodSyntaxCheck(String line){
        Matcher m = METHOD_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * Checks if a line has the correct format for a line of code (ends in ;, {, or } )
     * @param line the line to be checked
     * @return true if line ends correctly
     */
    private static boolean endOfLineCheck(String line){
        Matcher m = END_OF_LINE_PATTERN.matcher(line);
        return m.find();
    }

    /**
     * Checks that the line has no invalid tokens, such as operators or alternate comment structures.
     * @param line the line to be checked.
     * @return true if the line has invalid tokens
     */
    private static boolean invalidTokenCheck(String line){
        Matcher m = ILLEGAL_TOKENS_PATTERN.matcher(line);
        return m.find();
    }

    /**
     * Checks that line is a valid return line, meaning there are no other words on the line.
     * @param line the line to be checked.
     * @return true if the line is a valid return line.
     */
    private static boolean returnLineCheck(String line){
        Matcher m = RETURN_PATTERN.matcher(line);
        return m.matches();
    }
}
