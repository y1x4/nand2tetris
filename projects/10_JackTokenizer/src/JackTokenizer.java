import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Receive a .jack file, delete comments and split to tokens.
 *
 * Created by yixu on 2018/4/19.
 */
class JackTokenizer {

    private List<String> tokens;
    private int index;
    private String curToken;

    private static final List<String> keywords = Arrays.asList(
            "class", "constructor", "function", "method", "field", "static", "var",
            "int", "char", "boolean", "void", "true", "false", "null", "this",
            "let", "do", "if", "else", "while", "return"
    );
    private static final List<String> symbols = Arrays.asList(
            "{", "}", "(", ")", "[", "]", ".", ",", ";",
            "+", "-", "*", "/", "&", "|", "<", ">", "=", "~"
    );


    JackTokenizer(File file) {
        tokens = new ArrayList<>();
        index = 0;
        parseFile(file);
    }


    /**
     * Stage 1: use this constructor and main() to compile .jack files to tokens files in the directory.
     * @param
     */
    /*
    JackTokenizer(File fileDir) {

        File[] files = fileDir.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getName().endsWith(".jack")) {
                tokens = new ArrayList<>();
                index = 0;
                parseFile(file);

                String fileOutPath = file.getAbsolutePath().replace(".jack", "T.xml");
                File fileOut = new File(fileOutPath);
                try {
                    FileWriter outWriter = new FileWriter(fileOut);
                    outWriter.write("<tokens>" + System.lineSeparator());

                    for (int i = 0; i < tokens.size(); i++) {

                        advance();
                        //System.out.println(curToken + "     " + tokenType());
                        switch (curToken) {
                            case "<":
                                curToken = "&lt;";
                                break;
                            case ">":
                                curToken = "&gt;";
                                break;
                            case "&":
                                curToken = "&amp;";
                                break;
                            default:
                                break;
                        }
                        if (tokenType().equals("stringConstant")) {
                            curToken = curToken.substring(1, curToken.length() - 1);
                        }
                        outWriter.write(getTokenLine());
                    }
                    outWriter.write("</tokens>");
                    outWriter.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
*/


    private void parseFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            boolean inComment = false;
            // 一次读入一行，直到读入null为文件结束
            while ((line = reader.readLine()) != null) {

                //去  //注释 、 /*注释*/ 、 隔行/*注释*/ ，分割成tokens
                //line = line.replaceAll("^//+$", "");
                if (line.contains("//"))
                    line = line.substring(0, line.indexOf("//"));
                line = line.replaceAll("/\\*(.*?)\\*/", "");
                if (line.trim().startsWith("/*")) {
                    inComment = true;
                }
                if (line.trim().endsWith("*/")) {
                    inComment = false;
                    line = line.substring(line.indexOf("*/") + 2);
                }

                if (!line.isEmpty() && !inComment) {
                    //split symbols, int value and string values
                    Pattern p = Pattern.compile("\\{|}|\\(|\\)|\\[|]|\\.|,|;|\\+|-|\\*|/|&|\\||<|>|=|~" +
                            "|\\d+|\"(.*?)\"|[A-Za-z_][A-Za-z1-9_]*");
                    Matcher m = p.matcher(line);
                    while (m.find()) {
                        //curToken = m.group();
                        //System.out.println("<" + tokenType() + ">    " + curToken);
                        tokens.add(m.group());
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

    private boolean hasMoreTokens() {
        return index < tokens.size();
    }

    String advance() {
        if (hasMoreTokens()) {
            curToken = tokens.get(index++);
            return curToken;
        } else {
            return "NO MORE TOKEN!";
        }
    }

    private String tokenType() {

        if (keywords.contains(curToken))
            return "keyword";
        else if (symbols.contains(curToken))
            return "symbol";
        else if (curToken.matches("[0-9]*") && Integer.valueOf(curToken) <= 32767)
            return "integerConstant";
        else if (curToken.matches("\"(.*?)\""))
            return "stringConstant";
        else if (curToken.matches("[A-Za-z_][A-Za-z1-9_]*"))
            return "identifier";
        else
            return "WRONG CODE!";
    }

    String getTokenLine() {

        String type = tokenType();

        switch (curToken) {
            case "<":
                curToken = "&lt;";
                break;
            case ">":
                curToken = "&gt;";
                break;
            case "&":
                curToken = "&amp;";
                break;
            default:
                break;
        }
        if (tokenType().equals("stringConstant")) {
            curToken = curToken.substring(1, curToken.length() - 1);
        }
        String res =  "<" + type + "> " + curToken + " </" + type + ">" + System.lineSeparator();
        //System.out.println(res);
        return res;
    }

    String keyword() {
        return curToken;
    }

    String symbol() {
        return curToken;
    }

    String identifier() {
        return curToken;
    }

    int intVal() {
        return Integer.valueOf(curToken);
    }

    String strVal() {
        return curToken.substring(1, curToken.length() - 1);
    }



/*
    public static void main(String[] args) {


        String s = new Scanner(System.in).nextLine();

        s = s.replaceAll("^//.*", "");

        Pattern p = Pattern.compile("\\{|}|\\(|\\)|\\[|]|\\.|,|;|\\+|-|\\*|/|&|\\||<|>|=|~" +
                "|\"(.*?)\"|\\d+|[A-Za-z_][A-Za-z1-9_]*");
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group());
            //tokens.add(m.group());
        }


        Scanner in = new Scanner(System.in);
        File file = new File(in.next());
        JackTokenizer j = new JackTokenizer(file);

    }

*/

}
