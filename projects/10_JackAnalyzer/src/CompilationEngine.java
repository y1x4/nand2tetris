import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * read and compile.
 * Created by yixu on 2018/4/19.
 */
class CompilationEngine {

    private JackTokenizer tokenizer;
    private FileWriter outWriter;
    private String curToken;

    private static final String LINE_SEPT = System.lineSeparator();

    CompilationEngine(JackTokenizer j, File fileOut){
        try {
            this.tokenizer = j;
            outWriter = new FileWriter(fileOut);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    void start() {
        try {

            curToken = tokenizer.advance();
            compileClass();

            outWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * compile the whole CLASS.
     *
     * 'class' className '{' classVarDec* subroutineDec* '}'
     *
     */
    private void compileClass() throws IOException {
        outWriter.write("<class>".concat(LINE_SEPT));
        outWriter.write(tokenizer.getTokenLine());  //class
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //className
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //{

        curToken = tokenizer.advance();
        while (curToken.matches("static|field")) {  //classVarDec
            compileClassVarDec();
            curToken = tokenizer.advance();
        }
        while (curToken.matches("constructor|method|function")) {   //subroutineDec
            compileSubroutine();
            curToken = tokenizer.advance();
        }

        outWriter.write(tokenizer.getTokenLine());  // }
        outWriter.write("</class>".concat(LINE_SEPT));
    }

    /**
     * compile STATIC or FIELD declaration.
     *
     * (static | field) type varName(, varName)* ;
     */
    private void compileClassVarDec() throws IOException {
        outWriter.write("<classVarDec>".concat(LINE_SEPT));

        outWriter.write(tokenizer.getTokenLine());  //static | field
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //type
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //varName

        curToken = tokenizer.advance(); //, | ;
        while (curToken.equals(",")) {
            outWriter.write(tokenizer.getTokenLine());  //,
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());  //varName
            curToken = tokenizer.advance();
        }

        outWriter.write(tokenizer.getTokenLine());  //;
        outWriter.write("</classVarDec>".concat(LINE_SEPT));
    }

    /**
     * compile METHOD, FUNCTION or CONSTRUCTOR.
     *
     * (constructor | method | function) (void | type) subroutineName '(' parameterList ')'
     */
    private void compileSubroutine() throws IOException {
        outWriter.write("<subroutineDec>".concat(LINE_SEPT));

        outWriter.write(tokenizer.getTokenLine());  //constructor | method | function
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   // void | type
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());    //subroutineName
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //(

        curToken = tokenizer.advance();
        compileParameterList();

        outWriter.write(tokenizer.getTokenLine());  //)

        //subroutineBody: { varDec* statements }
        outWriter.write("<subroutineBody>".concat(LINE_SEPT));
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   // {

        curToken = tokenizer.advance();
        while (curToken.matches("var")) {  //varDec*
            compileVarDec();
            curToken = tokenizer.advance();
        }
        if (curToken.matches("let|if|while|do|return")) {   //statements
            compileStatements();
        }

        outWriter.write(tokenizer.getTokenLine());  // }
        outWriter.write("</subroutineBody>".concat(LINE_SEPT));

        outWriter.write("</subroutineDec>".concat(LINE_SEPT));
    }

    /**
     * compile PARAMETERs, might be null and not include "()".
     *
     * ((type varName)(, type varName)*)?
     */
    private void compileParameterList() throws IOException {
        outWriter.write("<parameterList>".concat(LINE_SEPT));

        if (! curToken.equals(")")) {    //(type varName)
            outWriter.write(tokenizer.getTokenLine());  //type
            curToken = tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());  //varName
            curToken = tokenizer.advance();
        }

        while (curToken.equals(",")) {    //(, type name)*
            outWriter.write(tokenizer.getTokenLine());  //,
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());  //type
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());  //varName
            curToken = tokenizer.advance();
        }

        outWriter.write("</parameterList>".concat(LINE_SEPT));
    }

    /**
     * var type varName(, varName)* ;
     */
    private void compileVarDec() throws IOException {
        outWriter.write("<varDec>".concat(LINE_SEPT));
        outWriter.write(tokenizer.getTokenLine());  // var
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   //type
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());    //varName

        curToken = tokenizer.advance();     // , | ;
        while (curToken.equals(",")) {
            outWriter.write(tokenizer.getTokenLine());    //,
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());    //varName
            curToken = tokenizer.advance();
        }

        outWriter.write(tokenizer.getTokenLine());  // ;
        outWriter.write("</varDec>".concat(LINE_SEPT));
    }

    /**
     * statement*
     */
    private void compileStatements() throws IOException {
        outWriter.write("<statements>".concat(LINE_SEPT));

        while (curToken.matches("let|if|while|do|return")) {
            switch (curToken) {
                case "let":
                    compileLet();
                    curToken = tokenizer.advance();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    curToken = tokenizer.advance();
                    break;
                case "do":
                    compileDo();
                    curToken = tokenizer.advance();
                    break;
                case "return":
                    compileReturn();
                    curToken = tokenizer.advance();
                    break;
                default:
                    break;
            }
        }

        outWriter.write("</statements>".concat(LINE_SEPT));
    }


    /**
     * do subroutineCall;
     * do param (. param) '(' expressionList ')' ';'
     */
    private void compileDo() throws IOException {
        outWriter.write("<doStatement>".concat(LINE_SEPT));
        outWriter.write(tokenizer.getTokenLine());  //do

        //subroutineCall
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  //subroutineName | className | varName

        curToken = tokenizer.advance();
        if (curToken.equals(".")) {
            outWriter.write(tokenizer.getTokenLine());  //.
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());  //subroutineName
            tokenizer.advance();
        }

        outWriter.write(tokenizer.getTokenLine());  //(
        curToken = tokenizer.advance();
        compileExpressionList();
        outWriter.write(tokenizer.getTokenLine());  //)

        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  // ;
        outWriter.write("</doStatement>".concat(LINE_SEPT));
    }

    /**
     * let varName ([ expression ])? = expression ;
     */
    private void compileLet() throws IOException {
        outWriter.write("<letStatement>".concat(LINE_SEPT));
        outWriter.write(tokenizer.getTokenLine());  // let
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   //varName

        curToken = tokenizer.advance();
        if (curToken.equals("[")) {     //[expression]?
            outWriter.write(tokenizer.getTokenLine());  //[
            curToken = tokenizer.advance();
            compileExpression();
            outWriter.write(tokenizer.getTokenLine());   //]
            tokenizer.advance();
        }

        outWriter.write(tokenizer.getTokenLine());  // =
        curToken = tokenizer.advance();
        compileExpression();
        outWriter.write(tokenizer.getTokenLine());  //;
        outWriter.write("</letStatement>".concat(LINE_SEPT));
    }

    /**
     * whie ( expression ) { statements }
     */
    private void compileWhile() throws IOException {
        outWriter.write("<whileStatement>".concat(LINE_SEPT));

        outWriter.write(tokenizer.getTokenLine());  // while
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  // (
        curToken = tokenizer.advance();
        compileExpression();    //expression
        outWriter.write(tokenizer.getTokenLine());  // )
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());  // {

        curToken = tokenizer.advance();
        if (curToken.matches("let|if|while|do|return")) {   //statements
            compileStatements();
        }

        outWriter.write(tokenizer.getTokenLine());  // }
        outWriter.write("</whileStatement>".concat(LINE_SEPT));
    }

    /**
     * return (expression)? ;
     */
    private void compileReturn() throws IOException {
        outWriter.write("<returnStatement>".concat(LINE_SEPT));
        outWriter.write(tokenizer.getTokenLine());  // return

        curToken = tokenizer.advance();     // expression?
        if (! curToken.equals(";")) {
            compileExpression();
        }

        outWriter.write(tokenizer.getTokenLine());  // ;
        outWriter.write("</returnStatement>".concat(LINE_SEPT));
    }

    //might also include ELSE.

    /**
     * if ( expression ) { statements }
     * ( else { statements } )?
     */
    private void compileIf() throws IOException {
        outWriter.write("<ifStatement>".concat(LINE_SEPT));

        outWriter.write(tokenizer.getTokenLine());  // if
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   // (

        curToken = tokenizer.advance();     // expression
        compileExpression();
        outWriter.write(tokenizer.getTokenLine());   // )
        tokenizer.advance();
        outWriter.write(tokenizer.getTokenLine());   // {

        curToken = tokenizer.advance();
        compileStatements();

        outWriter.write(tokenizer.getTokenLine());   // }

        curToken = tokenizer.advance();
        if (curToken.equals("else")) {   //else
            outWriter.write(tokenizer.getTokenLine());  //else
            tokenizer.advance();
            outWriter.write(tokenizer.getTokenLine());   // {

            curToken = tokenizer.advance();
            compileStatements();

            outWriter.write(tokenizer.getTokenLine());   // }
            curToken = tokenizer.advance();
        }

        outWriter.write("</ifStatement>".concat(LINE_SEPT));
    }


    /**
     * term (op term)*
     */
    private void compileExpression() throws IOException {
        outWriter.write("<expression>".concat(LINE_SEPT));

        compileTerm();
        while (curToken.matches("\\+|-|\\*|/|&|\\||<|>|=")) {
            outWriter.write(tokenizer.getTokenLine());  // op
            curToken = tokenizer.advance();
            compileTerm();
        }

        outWriter.write("</expression>".concat(LINE_SEPT));
    }

    /**
     * (expression) | unaryOp term | param ([expression])
     */
    private void compileTerm() throws IOException {
        outWriter.write("<term>".concat(LINE_SEPT));

        if (curToken.equals("(")) {   //(expression)
            outWriter.write(tokenizer.getTokenLine());  //(
            curToken = tokenizer.advance();
            compileExpression();
            outWriter.write(tokenizer.getTokenLine());  //)
            curToken = tokenizer.advance();
        } else if (curToken.matches("-|~")) {   //unaryOp
            outWriter.write(tokenizer.getTokenLine());
            curToken = tokenizer.advance();
            compileTerm();
        } else {
            outWriter.write(tokenizer.getTokenLine());  //param
            curToken = tokenizer.advance();
            if (curToken.equals("[")) {     //[expression]
                outWriter.write(tokenizer.getTokenLine());
                curToken = tokenizer.advance();
                compileExpression();
                outWriter.write(tokenizer.getTokenLine());
                curToken = tokenizer.advance();
            } else if (curToken.equals("(")) {  //(expressionList)
                outWriter.write(tokenizer.getTokenLine());
                curToken = tokenizer.advance();
                compileExpressionList();
                outWriter.write(tokenizer.getTokenLine());
                curToken = tokenizer.advance();
            } else if (curToken.equals(".")) {   //.param(expressionList)
                outWriter.write(tokenizer.getTokenLine());  //.
                tokenizer.advance();
                outWriter.write(tokenizer.getTokenLine());  //param
                tokenizer.advance();
                outWriter.write(tokenizer.getTokenLine());  //(
                curToken = tokenizer.advance();
                compileExpressionList();
                outWriter.write(tokenizer.getTokenLine());  //)
                curToken = tokenizer.advance();
            }
        }

        outWriter.write("</term>".concat(LINE_SEPT));
    }


    //compile EXPRESSIONs, might be null.
    /**
     * (expression (, expression )* )?
     */
    private void compileExpressionList() throws IOException {
        outWriter.write("<expressionList>".concat(LINE_SEPT));

        if (!curToken.equals(")")) {
            compileExpression();
        }
        while (curToken.equals(",")) {
            outWriter.write(tokenizer.getTokenLine());  //,
            curToken = tokenizer.advance();
            compileExpression();
        }

        outWriter.write("</expressionList>".concat(LINE_SEPT));
    }
}
