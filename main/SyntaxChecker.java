package oop.ex6.main;

import oop.ex6.Exceptions.IllegalCodeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Sjavac.*;

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

    /** Pattern for a reserved word. */
    static final Pattern RESERVED_WORD_PATTERN = Pattern.compile("^\\s*(int|double|String|boolean|char|final|void|if|while|true|false|return)");

    /** Pattern for valid non-reserved word sequences: closing curly brace, variable initialization, and method calling. */
    static final Pattern NON_RESERVED_PATTERN = Pattern.compile("\\s*(}|(_\\w+|[a-zA-Z]\\w*)\\s*=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*)\\s*;|([a-zA-Z]\\w*)\\s*\\(\\s*((_\\w+|[a-zA-Z]\\w*)\\s*)?(,\\s*(_\\w+|[a-zA-Z]\\w*)\\s*)*\\)\\s*;)\\s*");

    /** Pattern for the end of each line, which either be a semicolon, or open or closed curly brace. */
    static final Pattern END_OF_LINE_PATTERN = Pattern.compile("[;\\{\\}]\\s*$");

    /** Pattern for a return line, which should be by itself. */
    static final Pattern RETURN_PATTERN = Pattern.compile("\\s*return;\\s*");

    /** Pattern for illegal tokens or sequence in the code: operators and alternate comment patterns. */
    static final Pattern ILLEGAL_TOKENS_PATTERN = Pattern.compile("\\/\\*{1,2}.*\\*\\/|[-\\+\\*]");

    static void checkSyntax(String[] lines) throws IllegalCodeException{
        for (String line : lines){
            if (!linePatternMatcher(line, BLANK_LINE_PATTERN) && !linePatternMatcher(line, COMMENT_PATTERN)){
                /* Check every line for a valid end and invalid tokens. */
                if (!linePatternFinder(line, END_OF_LINE_PATTERN) || linePatternFinder(line, ILLEGAL_TOKENS_PATTERN)){
                    throw new IllegalCodeException();
                }
                Matcher m = FIRST_WORD_PATTERN.matcher(line);
                if(m.find()) {
                    String firstWord = m.group();

                    if (linePatternFinder(line, RESERVED_WORD_PATTERN)) {
                        /* Check every line according to a specific format, that it follows that format. */
                        // Variable declaration
                        if (firstWord.matches(VARIABLE_DECLARATION_WORDS_REGEX) && !linePatternMatcher(line, VARIABLE_PATTERN)) {
                            throw new IllegalCodeException();
                        }
                        // Control Flow block
                        if (firstWord.matches(CONTROL_FLOW_REGEX) && !linePatternMatcher(line, CONTROL_FLOW_PATTERN)) {
                            throw new IllegalCodeException();
                        }
                        // Method declaration
                        if (firstWord.matches(METHOD_DECLARATION_REGEX) && !linePatternMatcher(line, METHOD_PATTERN)) {
                            throw new IllegalCodeException();
                        }
                        // Return case
                        if (firstWord.matches(METHOD_RETURN_KEYWORD) && !linePatternMatcher(line, RETURN_PATTERN)) {
                            throw new IllegalCodeException();
                        }
                    }
                    // Check syntax of cases without reserved words
                    else if (!linePatternMatcher(line, NON_RESERVED_PATTERN)) {
                        throw new IllegalCodeException();
                    }
                }
            }
        }
    }

    /**
     * A generic helper function for seeing if a line matches a pattern.
     * @param line the line to be checked.
     * @param pattern the pattern to be compared.
     * @return true if the line matches.
     */
    private static boolean linePatternMatcher(String line, Pattern pattern){
        return pattern.matcher(line).matches();
    }

    /**
     * A generic helper function for seeing if a line contains a match to a pattern.
     * @param line the line to be checked.
     * @param pattern the pattern to be compared.
     * @return true if the line contains the pattern.
     */
    private static boolean linePatternFinder(String line, Pattern pattern){
        return pattern.matcher(line).find();
    }

}
