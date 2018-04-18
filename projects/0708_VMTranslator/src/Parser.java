import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Instruction Parser
 * Created by yixu on 2018/4/8.
 */
class Parser {

    private List<String> instructions;
    private int index;
    private String curInstruction;


     Parser(File file) {
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

     boolean hasMoreCommands() {
        return index < instructions.size();
    }

     String advance() {
        curInstruction = instructions.get(index++);
        return curInstruction;
    }

     String commandType() {
        return curInstruction.split(" ")[0];
    }

     String arg1() {
        String[] cms = curInstruction.split(" ");
        if (cms.length > 1)
            return cms[1];
        else
            return "";
    }

     int arg2() {
        String[] cms = curInstruction.split(" ");
        if (cms.length > 2)
            return Integer.valueOf(cms[2]);
        else
            return -1;
    }
}
