import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VmTranslator {


    public static void main(String[] args) {

        // valid SOURCE or .vm FILE
        if (args.length != 1) {
            System.out.println("Correct command is:java VmTranslator [filename|directory]");
        }

        File file;
        file = new File(args[0]);
        List<File> vmFiles = new ArrayList<>();
        String fileOutPath;

        boolean needInit = false;
        if (file.isFile()) {
            vmFiles.add(file);
            if (file.getName().equals("Sys.vm"))
                needInit = true;
            fileOutPath = file.getAbsolutePath().replace(".vm", ".asm");
        } else {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.getName().endsWith(".vm")) {
                    vmFiles.add(f);
                    if (f.getName().equals("Sys.vm"))
                        needInit = true;
                }
            }
            fileOutPath = file.getAbsolutePath() + "/" +  file.getName() + ".asm";
        }

        File fileOut = new File(fileOutPath);
        CodeWriter writer = new CodeWriter(fileOut, needInit);
        Parser parser;

        for (File f : vmFiles) {

            parser = new Parser(f);
            String cmType;

            System.out.println(f.getName());

            //start parsing
            while (parser.hasMoreCommands()) {

                writer.writeComment(parser.advance());
                cmType = parser.commandType();

                switch (cmType) {
                    case "push":
                    case "pop":
                        if (parser.arg1().equals("static")) {
                            //push static 1 ---> push Xxx. 1
                            writer.writePushPop(cmType, f.getName().replace("vm", ""), parser.arg2());
                        } else {
                            writer.writePushPop(cmType, parser.arg1(), parser.arg2());
                        }
                        break;
                    case "label":
                        writer.writeLabel(parser.arg1());
                        break;
                    case "goto":
                        writer.writeGoto(parser.arg1());
                        break;
                    case "if-goto":
                        writer.writeIf(parser.arg1());
                        break;
                    case "call":
                        writer.writeCall(parser.arg1(), parser.arg2());
                        break;
                    case "return":
                        writer.writeReturn();
                        break;
                    case "function":
                        writer.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    default:
                        writer.writeArithmetic(cmType);
                        break;
                }
            }

        }

        //save file
        writer.close();
        System.out.println("File created: " + fileOutPath + System.lineSeparator());
    }
}
