import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yixu on 2018/4/8.
 */
public class Parser {

    private List<String> instructions;
    private int index;
    public String curInstruction;

    private final int C_ARITHMETIC = 0;
    private final int C_PUSH = 1;
    private final int C_POP = 1;

    private final int C_LABEL = 2;
    private final int C_GOTO = 3;
    private final int C_IF = 4;

    private final int C_CALL = 5;
    private final int C_RETURN = 6;
    private final int C_FUNCTION = 7;

    public Parser(File file) {
        instructions = new ArrayList<>();
        index = 0;
        readFile(file);
    }

    private void readFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            // 一次读入一行，直到读入null为文件结束
            while ((line = reader.readLine()) != null) {
                //去//后的注释内容，去首尾空格，若不为空，加入instructions
                int commentsIndex = line.indexOf("//");
                if (commentsIndex != -1)
                    line = line.substring(0, commentsIndex);
                line = line.trim();
                if (!line.isEmpty()) {
                    //System.out.println(line);
                    instructions.add(line);
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

    public boolean hasMoreCommands() {
        return index < instructions.size();
    }

    public void advance() {
        curInstruction = instructions.get(index++);
    }

    public String commandType() {
        /*
        switch (curInstruction.split(" ")[0]) {
            case "push":
                return C_PUSH;
            case "pop":
                return C_POP;
            case "label":
                return C_LABEL;
            case "goto":
                return C_GOTO;
            case "if_goto":
                return C_IF;
            case "function":
                return C_FUNCTION;
            case "call":
                return C_CALL;
            case "return":
                return C_RETURN;
            default:
                return C_ARITHMETIC;
        }
        */
        return curInstruction.split(" ")[0];
    }

    public String arg1() {
        String[] cms = curInstruction.split(" ");
        if (cms.length > 1)
            return cms[1];
        else
            return "";
    }

    public int arg2() {
        String[] cms = curInstruction.split(" ");
        if (cms.length > 2)
            return Integer.valueOf(cms[2]);
        else
            return -1;
    }
}
