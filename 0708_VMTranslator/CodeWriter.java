import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * CodeWriter - write code to the output file.
 * Created by yixu on 2018/4/8.
 */
class CodeWriter {

    private final String LINE_SEPT = System.lineSeparator();
    private PrintWriter outPrinter;
    private int suffix = 0;

     CodeWriter(File fileOut, boolean needInit) {
        try {
            outPrinter = new PrintWriter(fileOut);
            if (needInit) {
                writeInit();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //initial call, at head of output file
    private void writeInit() {

        outPrinter.write(
                "   //Initialize".concat(LINE_SEPT)

                //SP = 256
                .concat("@256").concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@SP").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT));
                /*
                .concat("@300").concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@1").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //
                .concat("@400").concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@2").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //
                .concat("@3000").concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@3").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //
                .concat("@3010").concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@4").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT));
                */

        writeCall("Sys.init", 0);
    }


    //add sub and or, eq lt gt, neg not
     void writeArithmetic(String cmType) {

        Map<String, String> writeMap = new HashMap<String, String>(){{
            put("add", "M=M+D");
            put("sub", "M=M-D");
            put("and", "M=M&D");
            put("or", "M=M|D");
            put("neg", "M=-M");
            put("not", "M=!M");
        }};

        if ("add_sub_and_or".contains(cmType)) {

            outPrinter.write(""
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("AM=M-1").concat(LINE_SEPT)
                    .concat("D=M").concat(LINE_SEPT)
                    //.concat("M=0").concat(LINE_SEPT)    //reset 0
                    .concat("A=A-1").concat(LINE_SEPT)
                    .concat(writeMap.get(cmType)).concat(LINE_SEPT));

        } else if ("eq_gt_lt".contains(cmType)) {

            String label = "START_" + cmType.toUpperCase() + "_" + suffix;
            suffix++;
            outPrinter.write(""
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("AM=M-1").concat(LINE_SEPT)
                    .concat("D=M").concat(LINE_SEPT)
                    //.concat("M=0").concat(LINE_SEPT)    //reset 0
                    .concat("A=A-1").concat(LINE_SEPT)
                    .concat("D=M-D").concat(LINE_SEPT)
                    .concat("M=-1").concat(LINE_SEPT)   //true
                    .concat("@" + label).concat(LINE_SEPT)
                    .concat("D;J").concat(cmType.toUpperCase()).concat(LINE_SEPT)
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("A=M-1").concat(LINE_SEPT)
                    .concat("M=0").concat(LINE_SEPT)    //false
                    .concat("(" + label + ")").concat(LINE_SEPT));
        } else {
            // neg not
            outPrinter.write(""
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("A=M-1").concat(LINE_SEPT)  //A - address of stack[sp]
                    .concat(writeMap.get(cmType)).concat(LINE_SEPT));
        }
    }


     void writePushPop(String command, String segment, int index) {

        if (segment.equals("constant")) {
            //get value(index)
            outPrinter.write(""
                    .concat("@" + index).concat(LINE_SEPT)
                    .concat("D=A").concat(LINE_SEPT)
                    //push to stack
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("A=M").concat(LINE_SEPT)
                    .concat("M=D").concat(LINE_SEPT)
                    .concat("@SP").concat(LINE_SEPT)
                    .concat("M=M+1").concat(LINE_SEPT));

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
                outPrinter.write(""
                        .concat("@").concat(writeMap.get(segment)).concat(LINE_SEPT)
                        .concat("D=M").concat(LINE_SEPT)
                        .concat("@" + index).concat(LINE_SEPT)
                        .concat("A=D+A").concat(LINE_SEPT)
                        //get value
                        .concat("D=M").concat(LINE_SEPT)
                        //push to stack
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("A=M").concat(LINE_SEPT)
                        .concat("M=D").concat(LINE_SEPT)
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("M=M+1").concat(LINE_SEPT));

            } else if (command.equals("pop")) {
                //store real address to R13
                outPrinter.write(""
                        .concat("@").concat(writeMap.get(segment)).concat(LINE_SEPT)
                        .concat("D=M").concat(LINE_SEPT)
                        .concat("@" + index).concat(LINE_SEPT)
                        .concat("D=D+A").concat(LINE_SEPT)
                        .concat("@R13").concat(LINE_SEPT)
                        .concat("M=D").concat(LINE_SEPT)
                        //pop from stack
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("AM=M-1").concat(LINE_SEPT)
                        .concat("D=M").concat(LINE_SEPT)
                        //.concat("M=0").concat(LINE_SEPT)    //reset stack peak to 0
                        //store value
                        .concat("@R13").concat(LINE_SEPT)
                        .concat("A=M").concat(LINE_SEPT)
                        .concat("M=D").concat(LINE_SEPT));
                        //reset R13 = 0
                        //.concat("@R13").concat(LINE_SEPT)
                        //.concat("M=0").concat(LINE_SEPT));
            }
        } else {

            String pos;
            //direct address --- 3 this, 4 that; temp R5-R12; static 16-
            switch (segment) {
                case "pointer":
                    pos = "" + 3 + index;
                    break;
                case "temp":
                    pos = "" + 5 + index;
                    break;
                default:
                    //Xxx.j
                    pos = segment + index;
                    break;
            }

            if (command.equals("push")) {
                //get address
                outPrinter.write(""
                        .concat("@" + pos).concat(LINE_SEPT)
                        .concat("D=M").concat(LINE_SEPT)
                        //push to stack
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("A=M").concat(LINE_SEPT)
                        .concat("M=D").concat(LINE_SEPT)
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("M=M+1").concat(LINE_SEPT));

            } else if (command.equals("pop")) {
                //pop from stack
                outPrinter.write(""
                        .concat("@SP").concat(LINE_SEPT)
                        .concat("AM=M-1").concat(LINE_SEPT)
                        .concat("D=M").concat(LINE_SEPT)
                        //.concat("M=0").concat(LINE_SEPT)
                        //store value
                        .concat("@" + pos).concat(LINE_SEPT)
                        .concat("M=D").concat(LINE_SEPT));
            }
        }
    }


    //label symbol
     void writeLabel(String symbol) {
        outPrinter.write("(" + symbol + ")" + LINE_SEPT);
    }

     void writeGoto(String symbol) {
        outPrinter.write(""
                .concat("@" + symbol).concat(LINE_SEPT)
                .concat("0;JMP").concat(LINE_SEPT));
    }

    void writeIf(String symbol) {

        //-1 true, jump to label
        outPrinter.write(""
                .concat("@SP").concat(LINE_SEPT)
                .concat("AM=M-1").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                //.concat("M=0").concat(LINE_SEPT)
                .concat("@").concat(symbol).concat(LINE_SEPT)
                .concat("D;JNE").concat(LINE_SEPT));
    }

    //numArgs >= 0
     void writeCall(String funcName, int numArgs) {

        String label = "RETURN_ADDRESS_" + suffix;
        suffix++;
        //push RETURN_ADDRESS
        outPrinter.write(""
                .concat("@" + label).concat(LINE_SEPT)
                .concat("D=A").concat(LINE_SEPT)
                .concat("@SP").concat(LINE_SEPT)
                .concat("A=M").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                .concat("@SP").concat(LINE_SEPT)
                .concat("M=M+1").concat(LINE_SEPT));

        //push LCL ARG THIS THAT
        writePushPop("push", "local", 0);
        writePushPop("push", "argument", 0);
        writePushPop("push", "this", 0);
        writePushPop("push", "that", 0);

        //reset ARG base address = SP - (n + 5)
        outPrinter.write(""
                .concat("@SP").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                .concat("@" + (numArgs + 5)).concat(LINE_SEPT)
                .concat("D=D-A").concat(LINE_SEPT)
                .concat("@ARG").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //reset LCL base address to SP
                .concat("@SP").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                .concat("@LCL").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //goto f
                .concat("@" + funcName).concat(LINE_SEPT)
                .concat("0;JMP").concat(LINE_SEPT)
                //(return address)
                .concat("(" + label + ")").concat(LINE_SEPT));

    }

    void writeReturn() {

        outPrinter.write(""
                //FRAME(R14) = LCL
                .concat("@LCL").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                .concat("@R14").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT)
                //RET(R15) = *(FRAME - 5)
                .concat("@5").concat(LINE_SEPT)
                .concat("A=D-A").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                .concat("@R15").concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT));

        //*ARG = pop()
        writePushPop("pop", "argument", 0);
        //SP = ARG+1
        outPrinter.write(""
                 .concat("@ARG").concat(LINE_SEPT)
                 .concat("D=M").concat(LINE_SEPT)
                 .concat("@SP").concat(LINE_SEPT)
                 .concat("M=D+1").concat(LINE_SEPT)
                 //recover
                 .concat(recover("THAT"))
                 .concat(recover("THIS"))
                 .concat(recover("ARG"))
                 .concat(recover("LCL"))
                 //goto RET(R15)
                 .concat("@R15").concat(LINE_SEPT)
                 .concat("A=M").concat(LINE_SEPT)
                 .concat("0;JMP").concat(LINE_SEPT));
    }
    private String recover(String dest) {
        return  ""
                .concat("@R14").concat(LINE_SEPT)
                .concat("AM=M-1").concat(LINE_SEPT)
                .concat("D=M").concat(LINE_SEPT)
                .concat("@").concat(dest).concat(LINE_SEPT)
                .concat("M=D").concat(LINE_SEPT);
    }

    /* numLocals >= 0
     */
    void writeFunction(String funcName, int numLocals) {

        writeLabel(funcName);
        //locals init to 0
        for (int i = 0; i < numLocals; i++) {
            writePushPop("push", "constant", 0);
        }
    }



    void writeComment(String instruction) {
        outPrinter.write("  //" + instruction + LINE_SEPT);
    }

     void close() {
        outPrinter.close();
    }
}
