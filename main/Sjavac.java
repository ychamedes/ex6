package oop.ex6.main;

import oop.ex6.Exceptions.*;
import oop.ex6.Scopes.Scope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Sjavac class that functions as a code verifier for s-Java files.
 */
public class Sjavac {

    private static final int NUMBER_VALID_ARGUMENTS = 1;

    private static final int LEGAL_CODE_OUTPUT = 0;
    private static final int ILLEGAL_CODE_OUTPUT = 1;
    private static final int GENERAL_ERROR_OUTPUT = 2;

    private static final int INITIAL_LINE_COUNT = 0;

    private static final String ERROR = " Error: ";
    private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
    private static final String UNKNOWN_ERROR = "Unknown Error";

    /** Pattern for the first non-whitespace sequence. */
    static final Pattern FIRST_WORD_PATTERN = Pattern.compile("\\S+");

    /** Pattern for a blank line in the code. */
    static final Pattern BLANK_LINE_PATTERN = Pattern.compile("\\s*");

    /** Pattern for a comment line in the code. */
    static final Pattern COMMENT_PATTERN = Pattern.compile("^\\/\\/.*");

    /** Pattern for variable(s) declaration, including possible final keyword and initialization.*/
    static final Pattern VARIABLE_PATTERN = Pattern.compile("\\s*((final)\\s+)?(int|double|String|boolean|char)\\s+(?!final)((_\\w+|[a-zA-Z]\\w*)\\s*(=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*))?\\s*)(,\\s*(_\\w+|[a-zA-Z]\\w*)\\s*(=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*))?\\s*)*;\\s*");

    /** Pattern for method declaration, including the void keyword and valid parameter conditions. */
    static final Pattern METHOD_PATTERN = Pattern.compile("\\s*void\\s+([a-zA-Z]\\w*)\\s*\\((\\s*(final\\s+)?(int|double|String|boolean|char)\\s+(_\\w+|[a-zA-Z]\\w*)\\s*)?(,\\s*(final\\s+)?(int|double|String|boolean|char)\\s+(_\\w+|[a-zA-Z]\\w*)\\s*)*\\)\\s*\\{\\s*");

    /** Regex options of reserved words for command flow statements. */
    static final String CONTROL_FLOW_REGEX = "if|while";

    /** Pattern for control flow (if/while) statement with valid condition type.
     * Condition is second capturing group. */
    static final Pattern CONTROL_FLOW_PATTERN = Pattern.compile("\\s*(if|while)\\s*\\((\\s*(\\w+\\s*(\\|\\||&&))*\\s*\\w+\\s*)\\)\\s*\\{\\s*");

    /** Pattern for the boolean value of a if/while statement.
     * Each Condition is first capturing group. */
    static final Pattern CONDITION_PATTERN = Pattern.compile("(true|false|_\\w+|[a-zA-z]\\w*|-?\\d+(\\.\\d+)?)");
    
    /** Regex for a return line, which should be by itself. */
    static final String RETURN_REGEX = "\\s*return;\\s*";
    
    /** Pattern for a return line, which should be by itself. */
    static final Pattern RETURN_PATTERN = Pattern.compile("\\s*return;\\s*");

    /** Regex options of reserved boolean words for command flow conditions. */
    static final String BOOLEAN_REGEX = "true|false";

    /** Regex options of reserved boolean words for command flow conditions. */
    static final String BOOLEAN_TYPE_REGEX = "boolean|double|int";

    /** Pattern for a closing bracket line, which should be by itself. */
    static final Pattern CLOSING_BRACKET_PATTERN = Pattern.compile("\\s*}\\s*");

    /** Pattern for the start of a variable.
     * Final keyword is second capturing group.
     * Variable type is third capturing group. */
    static final Pattern VARIABLE_START_PATTERN = Pattern.compile("((final)\\s+)?(int|double|String|boolean|char)");

    /** Pattern for variable(s) name. Variable name is second capturing group.*/
    static final String VARIABLE_NAME_REGEX = "_\\w+|[a-zA-Z]\\w*";

    /** Pattern for if a variable is being assigned.
     * Name is second capturing group.
     * Value is fourth capturing group. */
    static final Pattern VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile("(?!final)((_\\w+|[a-zA-Z]\\w*)\\s*(=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*))\\s*),?");

    /**Regex for a line with a method call. */
    static final String METHOD_CALL_REGEX = "\\s*(([a-zA-Z]\\w*)\\s*\\(\\s*((_\\w+|[a-zA-Z]\\w*)\\s*)?(,\\s*(_\\w+|[a-zA-Z]\\w*)\\s*)*\\)\\s*;)\\s*";

    /** Pattern for when a method is called.
     * Method name is second capturing group.
     * Each parameter is fourth capturing group. */
    static final Pattern METHOD_CALL_PATTERN = Pattern.compile(METHOD_CALL_REGEX);

    /** Pattern used to find parameters (variable names) used in a method call. */
    static final Pattern PARAMETER_PATTERN = Pattern.compile(VARIABLE_NAME_REGEX);

    private String sourceFilePath;

    /**
     * Class constructor that receives a source file name.
     * @param sourceFileName the path of the source file.
     */
    private Sjavac(String sourceFileName){
        sourceFilePath = sourceFileName;
    }

    /**
     * Checks the code in s-Java file.
     * Prints if the code is legal or not.
     */
    private void validateCode(){

        File sourceFile = new File(sourceFilePath);

        try{
            //Parse the source file and produce an array of lines
            String[] lineArray = parseFile(sourceFile);

            //Check the syntax
            SyntaxChecker.checkSyntax(lineArray);

            //Check scope, variable, logic
            Scope mainScope = MainScopeChecker.buildMainScope(lineArray);
            MainScopeChecker.checkMainScope(mainScope);

            //If no exceptions were thrown, print 0
            System.out.println(LEGAL_CODE_OUTPUT);
        }
        catch (IOException error){
            printGeneralError(error.getMessage());
        }
        catch (IllegalCodeException error){
            System.out.println(ILLEGAL_CODE_OUTPUT);
        }
    }

    public static void main(String[] args){

        //Check that the number of system arguments is valid
        if (args.length != NUMBER_VALID_ARGUMENTS){
            printGeneralError(INVALID_NUMBER_OF_ARGUMENTS);
        }

        Sjavac mySjavac = new Sjavac(args[0]);
        mySjavac.validateCode();

    }

    /**
     * Counts the number of lines in the s-Java file.
     * @param file the s-Java file.
     * @return the number of lines.
     * @throws IOException an IO exception from the reader
     */
    private int countFileLines(File file) throws IOException{
        int lineCount = INITIAL_LINE_COUNT;
        BufferedReader lineCounter = new BufferedReader(new FileReader(file));
        String line = lineCounter.readLine();
        while(line != null){
            lineCount++;
            line = lineCounter.readLine();
        }

        lineCounter.close();
        return lineCount;

    }

    /**
     * Parses the s-Java file into an array of lines.
     * @param file the s-Java file.
     * @return array of the file lines.
     * @throws IOException an IO exception from the reader
     */
    private String[] parseFile(File file) throws IOException{

        BufferedReader reader;

        //Initialize an array to store the lines in the file
        int fileLineCount = countFileLines(file);
        String[] lineArray = new String[fileLineCount];

        reader = new BufferedReader(new FileReader(sourceFilePath));
        String line = reader.readLine();
        for(int index = 0; index < fileLineCount; index++){
            lineArray[index] = line;
            line = reader.readLine();
        }

        reader.close();
        return lineArray;
    }

    /**
     * Prints a general error message with a specific error message.
     * @param errorMessage the specific error message.
     */
    private static void printGeneralError(String errorMessage){
        if (errorMessage == null) {
            errorMessage = UNKNOWN_ERROR;
        }
        System.err.println(GENERAL_ERROR_OUTPUT);
        System.err.println(ERROR + errorMessage);
    }
}
