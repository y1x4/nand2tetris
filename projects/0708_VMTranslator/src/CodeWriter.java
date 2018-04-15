import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yixu on 2018/4/8.
 */
public class CodeWriter {

    private final String LINE_SEPT = System.lineSeparator();
    private PrintWriter outPrinter;
    private int suffix = 0;

    private final StringBuilder pushToStack = new StringBuilder()
            .append("@SP").append(LINE_SEPT)
            .append("A=M").append(LINE_SEPT)
            .append("M=D").append(LINE_SEPT)
            .append("@SP").append(LINE_SEPT)
            .append("M=M+1").append(LINE_SEPT);

    private final StringBuilder popFromStack = new StringBuilder()
            .append("@SP").append(LINE_SEPT)
            .append("AM=M-1").append(LINE_SEPT)
            .append("D=M").append(LINE_SEPT);

    public CodeWriter(File fileOut) {
        try {
            outPrinter = new PrintWriter(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    //add sub and or eq lt gt neg not
    public void writeArithmetic(String cmType) {

        Map<String, String> writeMap = new HashMap<String, String>(){{
            put("add", "M=M+D");
            put("sub", "M=M-D");
            put("and", "M=M&D");
            put("or", "M=M|D");
            put("neg", "M=-M");
            put("not", "M=!M");
        }};

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//" + cmType +  LINE_SEPT);

        if ("add_sub_and_or".contains(cmType)) {

            sb.append("@SP").append(LINE_SEPT)
                    .append("AM=M-1").append(LINE_SEPT)
                    .append("D=M").append(LINE_SEPT)
                    .append("M=0").append(LINE_SEPT)    //reset 0
                    .append("A=A-1").append(LINE_SEPT)
                    .append(writeMap.get(cmType)).append(LINE_SEPT);

        } else if ("eq_gt_lt".contains(cmType)) {

            String label = "START_" + cmType.toUpperCase() + suffix;
            suffix++;
            sb.append("@SP").append(LINE_SEPT)
                    .append("AM=M-1").append(LINE_SEPT)
                    .append("D=M").append(LINE_SEPT)
                    .append("M=0").append(LINE_SEPT)    //reset 0
                    .append("A=A-1").append(LINE_SEPT)
                    .append("D=M-D").append(LINE_SEPT)
                    .append("M=-1").append(LINE_SEPT)   //true
                    .append("@").append(label).append(LINE_SEPT)
                    .append("D;J").append(cmType.toUpperCase()).append(LINE_SEPT)
                    .append("@SP").append(LINE_SEPT)
                    .append("A=M-1").append(LINE_SEPT)
                    .append("M=0").append(LINE_SEPT)    //false
                    .append("(").append(label).append(")").append(LINE_SEPT);
        } else {
            // neg not
            sb.append("@SP").append(LINE_SEPT)
                    .append("A=M-1").append(LINE_SEPT)  //A - address of stack[sp]
                    .append(writeMap.get(cmType)).append(LINE_SEPT);
        }

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
    }


    public String writePushPop(String command, String segment, int index) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//" + command + " " +segment + " " + index +  LINE_SEPT);

        if (segment.equals("constant")) {
            //get value(index)
            sb.append("@").append(index).append(LINE_SEPT)
                    .append("D=A").append(LINE_SEPT)
                    //push to stack
                    .append("@SP").append(LINE_SEPT)
                    .append("A=M").append(LINE_SEPT)
                    .append("M=D").append(LINE_SEPT)
                    .append("@SP").append(LINE_SEPT)
                    .append("M=M+1").append(LINE_SEPT);

        } else if ("local_argument_this_that".contains(segment)) {
            //un-direct address
            Map<String, String> writeMap = new HashMap<String, String>(){{
                put("local", "LCL");
                put("argument", "ARG");
                put("this", "THIS");
                put("that", "THAT");
            }};

            if (command.equals("push")) {
                //get real address
                sb.append("@").append(writeMap.get(segment)).append(LINE_SEPT)
                        .append("D=M").append(LINE_SEPT)
                        .append("@").append(index).append(LINE_SEPT)
                        .append("A=D+A").append(LINE_SEPT)
                        //get value
                        .append("D=M").append(LINE_SEPT)
                        //push to stack
                        .append("@SP").append(LINE_SEPT)
                        .append("A=M").append(LINE_SEPT)
                        .append("M=D").append(LINE_SEPT)
                        .append("@SP").append(LINE_SEPT)
                        .append("M=M+1").append(LINE_SEPT);

            } else if (command.equals("pop")) {
                //store real address to R13
                sb.append("@").append(writeMap.get(segment)).append(LINE_SEPT)
                        .append("D=M").append(LINE_SEPT)
                        .append("@").append(index).append(LINE_SEPT)
                        .append("D=D+A").append(LINE_SEPT)
                        .append("@R13").append(LINE_SEPT)
                        .append("M=D").append(LINE_SEPT)
                        //pop from stack
                        .append("@SP").append(LINE_SEPT)
                        .append("AM=M-1").append(LINE_SEPT)
                        .append("D=M").append(LINE_SEPT)
                        .append("M=0").append(LINE_SEPT)    //reset stack peak to 0
                        //store value
                        .append("@R13").append(LINE_SEPT)
                        .append("A=M").append(LINE_SEPT)
                        .append("M=D").append(LINE_SEPT)
                        //reset R13 = 0
                        .append("@R13").append(LINE_SEPT)
                        .append("M=0").append(LINE_SEPT);
            }
        } else {
            int pos;
            //direct address --- 3 this, 4 that; temp R5-R12; static 16-
            if (segment.equals("pointer"))
                pos = 3 + index;
            else if (segment.equals("temp"))
                pos = 5 + index;
            else
                pos = 16 + index;

            if (command.equals("push")) {
                //get address
                sb.append("@").append(pos).append(LINE_SEPT)
                        .append("D=M").append(LINE_SEPT)
                        //push to stack
                        .append("@SP").append(LINE_SEPT)
                        .append("A=M").append(LINE_SEPT)
                        .append("M=D").append(LINE_SEPT)
                        .append("@SP").append(LINE_SEPT)
                        .append("M=M+1").append(LINE_SEPT);

            } else if (command.equals("pop")) {
                //pop from stack
                sb.append("@SP").append(LINE_SEPT)
                        .append("AM=M-1").append(LINE_SEPT)
                        .append("D=M").append(LINE_SEPT)
                        .append("M=0").append(LINE_SEPT)
                        //store value
                        .append("@").append(pos).append(LINE_SEPT)
                        .append("M=D").append(LINE_SEPT);
            }
        }

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
        return sb.toString();
    }


    //label symbol
    protected String writeLabel(String symbol) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//label " + symbol +  LINE_SEPT);

        sb.append("(").append(symbol).append(")").append(LINE_SEPT);

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
        return sb.toString();
    }

    protected String writeGoto(String symbol) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//goto " + symbol + LINE_SEPT);

        sb.append("@").append(symbol).append(LINE_SEPT)
                .append("0;JMP").append(LINE_SEPT);

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
        return sb.toString();
    }

    protected void writeIf(String symbol) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//if-goto " + symbol +  LINE_SEPT);

        //-1 true, jump to label
        sb.append("@SP").append(LINE_SEPT)
                .append("AM=M-1").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("M=0").append(LINE_SEPT)
                .append("@").append(symbol).append(LINE_SEPT)
                .append("D;JNE").append(LINE_SEPT);

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
    }

    //numArgs >= 0
    protected void writeCall(String funcName, int numArgs) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//call " + funcName + " " + numArgs + LINE_SEPT);

        //push RETURN_ADDRESS LCL ARG THIS THAT
        sb.append("@RETURN_ADDRESS_").append(funcName).append(LINE_SEPT)
                .append("D=A").append(LINE_SEPT)
                .append("@SP").append(LINE_SEPT)
                .append("A=M").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                .append("@SP").append(LINE_SEPT)
                .append("M=M+1").append(LINE_SEPT)
                .append(writePushPop("push", "local", 0))
                .append(writePushPop("push", "argument", 0))
                .append(writePushPop("push", "this", 0))
                .append(writePushPop("push", "that", 0))
                //reset ARG = SP - n - 5
                .append("@SP").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@").append(numArgs).append(LINE_SEPT)
                .append("D=D-A").append(LINE_SEPT)
                .append("@").append(5).append(LINE_SEPT)
                .append("D=D-A").append(LINE_SEPT)
                .append("@ARG").append(LINE_SEPT)
                .append("A=M").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                //reset LCL to SP
                .append("@SP").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@LCL").append(LINE_SEPT)
                .append("A=M").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                //goto f
                .append(writeGoto(funcName))
                .append("(RETURN_ADDRESS_").append(funcName).append(")").append(LINE_SEPT);

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
    }

    protected void writeReturn() {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//return" + LINE_SEPT);

        //FRAME = LCL
        sb.append("@LCL").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@R11").append(LINE_SEPT)
                .append("A=M").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                //RET(R12) = *(FRAME - 5)
                .append("@5").append(LINE_SEPT)
                .append("A=D-A").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@R12").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                //*ARG = pop(); SP = ARG+1
                .append(writePushPop("pop", "argument", 0))
                .append("@ARG").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@SP").append(LINE_SEPT)
                .append("M=D+1").append(LINE_SEPT)
                //
                .append(recover("THAT"))
                .append(recover("THIS"))
                .append(recover("ARG"))
                .append(recover("LCL"))
                .append(writeGoto("R12"));

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
    }

    private String recover(String dest) {

        return new StringBuilder()
                .append("@R11").append(LINE_SEPT)
                .append("AM=M-1").append(LINE_SEPT)
                .append("D=M").append(LINE_SEPT)
                .append("@").append(dest).append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
                .toString();
    }

    /* numLocals >= 0
     */
    protected void writeFunction(String funcName, int numLocals) {

        StringBuilder sb = new StringBuilder();
        outPrinter.write("//function " + funcName + " " + numLocals + LINE_SEPT);

        sb.append("(").append(funcName).append(")").append(LINE_SEPT)
                .append("@0").append(LINE_SEPT)
                .append("D=A").append(LINE_SEPT);
        for (int i = 0; i < numLocals; i++) {
            sb.append("@SP").append(LINE_SEPT)
                    .append("A=M").append(LINE_SEPT)
                    .append("M=D").append(LINE_SEPT)
                    .append("@SP").append(LINE_SEPT)
                    .append("M=M+1").append(LINE_SEPT);
        }
        /*
        sb.append("(").append(funcName).append(")").append(LINE_SEPT)
                .append("@").append(numLocals).append(LINE_SEPT)
                .append("D=A").append(LINE_SEPT)
                .append("@R13").append(LINE_SEPT)
                .append("A=M").append(LINE_SEPT)
                .append("M=D").append(LINE_SEPT)
        */

        outPrinter.write(sb.toString());
        System.out.println(sb.toString());
    }

    public void close() {
        outPrinter.close();
    }
}
