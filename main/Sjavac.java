package ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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


    private String sourceFilePath;


    public Sjavac(String sourceFileName){
        sourceFilePath = sourceFileName;
    }

    private void validateCode(){


        File sourceFile = new File(sourceFilePath);

        try{
            String[] lineArray = parseFile(sourceFile);

            // All code goes here


            System.out.println(LEGAL_CODE_OUTPUT);
        }
        catch (IOException error){
            printGeneralError(error.getMessage());
            System.exit(0);
        }
        catch (IllegalCodeException){
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

    private int countFileLines(File file) throws IOException{
        int lineCount = INITIAL_LINE_COUNT;
        BufferedReader lineCounter = new BufferedReader(new FileReader(file));
        String line = lineCounter.readLine(); // Need to handle empty file???
        while(line != null){
            lineCount++;
            line = lineCounter.readLine();
        }

        lineCounter.close();
        return lineCount;

    }

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

    private static void printGeneralError(String errorMessage){
        if (errorMessage == null) {
            errorMessage = UNKNOWN_ERROR;
        }
        System.out.println(GENERAL_ERROR_OUTPUT);
        System.err.println(ERROR + errorMessage);
        System.exit(0);
    }
}
