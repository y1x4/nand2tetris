import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * read and parse; remove script and space.
 * Created by yixu on 2018/4/6.
 */
public class Parser {

    private List<String> instructions;
    private List<String> hexCodes;
    private Map<String, Integer> labelSymbols;
    private int index;
    private String curInstruction;

    private int A_COMMAND = 0;
    private int C_COMMAND = 1;
    private int L_COMMAND = 2;

    private Code code = new Code();
    private SymbolTable symbolTable = new SymbolTable();

    private String filePath;    //i.e. Users/yixu/Desktop/nand2tetris/projects/06/Max/MaxL.asm

    //读入文件
    public Parser(String fileName) {
        instructions = new ArrayList<>();
        hexCodes = new ArrayList<>();
        labelSymbols = new HashMap<>();
        index = 0;
        readFile(fileName);
        doParse();
        writeFile();
    }

    private void readFile(String fileName) {

        File file = new File(fileName);
        filePath = file.getAbsolutePath();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            // 一次读入一行，直到读入null为文件结束
            while ((line = reader.readLine()) != null) {
                //去//后的注释内容，去空格，若不为空，加入instructions
                int commentsIndex = line.indexOf("//");
                if (commentsIndex != -1)
                    line = line.substring(0, commentsIndex);
                line = line.replace(" ", "");
                if (!line.isEmpty()) {
                    //System.out.println(line);
                    if (line.startsWith("(")) {
                        // (LOOP) --> LOOP
                        labelSymbols.put(line.substring(1, line.length() - 1), instructions.size());
                    } else {
                        instructions.add(line);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private void doParse() {

        //add label symbols to SymbolTable
        for (Map.Entry<String, Integer> entry : labelSymbols.entrySet()) {
            symbolTable.addEntry(entry.getKey(), entry.getValue());
        }

        while (hasMoreCommands()) {
            advance();
            String machcode = "";

            if (commandType() == C_COMMAND) {
                machcode += "111" + code.comp(comp()) + code.dest(dest()) + code.jump(jump());
            } else {
                //A_COMMAND TYPE
                String symbol = symbol();
                int address;
                if (symbol.matches("[0-9]+")) {
                    address = Integer.valueOf(symbol);
                } else {
                    if (! symbolTable.contains(symbol))
                        symbolTable.addEntry(symbol, -1);
                    address = symbolTable.getAddress(symbol);
                }
                machcode = toHexAndFill(address);
            }

            hexCodes.add(machcode);

            /*
            for (int i = 0; i < 16; i++) {
                System.out.print(machcode.charAt(i));
                if ((i + 1) % 4 == 0)
                    System.out.print(" ");
            }
            System.out.println();
            */
        }
    }

    private String toHexAndFill(int num) {
        String res = Integer.toBinaryString(num);
        for (int i = res.length(); i < 16; i++) {
            res = "0" + res;
        }
        return res;
    }

    private void writeFile() {

        File writeFile = new File(filePath.replace("asm", "hack"));
        FileWriter fw = null;
        try {
            fw = new FileWriter(writeFile);
            for (String code : hexCodes) {
                //System.out.println(code);
                fw.write(code);
                fw.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean hasMoreCommands() {
        return index < instructions.size();
    }

    //get current instruction
    private void advance() {
        curInstruction =  instructions.get(index++);
    }

    private int commandType() {
        if (curInstruction.startsWith("@"))
            return A_COMMAND;
        else if (curInstruction.startsWith("("))
            return L_COMMAND;
        else
            return C_COMMAND;
    }

    //A_COMMAND or L_COMMAND type
    private String symbol() {
        if (commandType() == A_COMMAND)
            return curInstruction.substring(1);
        else
            return curInstruction.substring(1, curInstruction.length() - 1);
    }

    //before "="
    private String dest() {
        int endIndex = curInstruction.indexOf("=");
        if (endIndex == -1)
            return "null";
        else
            return curInstruction.substring(0, endIndex);
    }

    //after "=" and before ";"
    private String comp() {
        int lastIndex = curInstruction.indexOf(";");
        if (lastIndex == -1)
            lastIndex = curInstruction.length();
        return curInstruction.substring(curInstruction.indexOf("=") + 1, lastIndex);
    }

    //after ";"
    private String jump() {
        int startIndex = curInstruction.indexOf(";");
        if (startIndex == -1)
            return "null";
        else
            return curInstruction.substring(startIndex + 1);
    }
}
