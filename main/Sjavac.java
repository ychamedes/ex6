package ex6.main;

import ex6.Exceptions.*;

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

    /** Pattern for variable(s) declaration, including possible final keyword and initialization.*/
    static final Pattern VARIABLE_PATTERN = Pattern.compile("\\s*((final)\\s+)?(int|double|String|boolean|char)\\s+(?!final)((_\\w+|[a-zA-Z]\\w*)\\s*(=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*))?\\s*)(,\\s*(_\\w+|[a-zA-Z]\\w*)\\s*(=\\s*(true|false|\\\"\\w*\\\"|\\d+(\\.\\d+)?|_\\w+|[a-zA-Z]\\w*))?\\s*)*;\\s*");

    /** Pattern for method declaration, including the void keyword and valid parameter conditions. */
    static final Pattern METHOD_PATTERN = Pattern.compile("\\s*void\\s+([a-zA-Z]\\w*)\\s*\\((\\s*(final\\s+)?(int|double|String|boolean|char)\\s+(_\\w+|[a-zA-Z]\\w*)\\s*)?(,\\s*(final\\s+)?(int|double|String|boolean|char)\\s+(_\\w+|[a-zA-Z]\\w*)\\s*)*\\)\\s*\\{\\s*");

    /** Regex options of reserved words for command flow statements. */
    static final String CONTROL_FLOW_REGEX = "if|while";

    /** Pattern for control flow (if/while) statement with valid condition type. */
    static final Pattern CONTROL_FLOW_PATTERN = Pattern.compile("\\s*(if|while)\\s*\\(\\s*(\\w+\\s*(\\|\\||&&))*\\s*\\w+\\s*\\)\\s*\\{\\s*");

    /** Pattern for a return line, which should be by itself. */
    static final Pattern RETURN_PATTERN = Pattern.compile("\\s*return;\\s*");




    private String sourceFilePath;

    /**
     * Class constructor that receives a source file name.
     * @param sourceFileName the path of the source file.
     */
    public Sjavac(String sourceFileName){
        sourceFilePath = sourceFileName;
    }

    /**
     * Checks the code in s-Java file.
     * Prints if the code is legal or not.
     */
    private void validateCode(){


        File sourceFile = new File(sourceFilePath);

        try{
            String[] lineArray = parseFile(sourceFile);

            SyntaxChecker.checkSyntax(lineArray);

            System.out.println(LEGAL_CODE_OUTPUT);
        }
        catch (IOException error){
            printGeneralError(error.getMessage());
            System.exit(0);
        }
        catch (IllegalCodeException error){
            System.out.println(ILLEGAL_CODE_OUTPUT);
            System.exit(0);
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
        System.out.println(GENERAL_ERROR_OUTPUT);
        System.err.println(ERROR + errorMessage);
        System.exit(0);
    }
}
