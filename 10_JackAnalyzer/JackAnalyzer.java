import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JackAnalyzer {

    public static void main(String[] args) {

        // valid SOURCE or .jack FILE
        String arg;
        if (args.length == 0) {
            System.out.println("Input the file or directory SOURCE: ");
            arg = new Scanner(System.in).next();
        } else {
            arg = args[0];
        }

        File file;
        file = new File(arg);
        List<File> jackFiles = new ArrayList<>();

        if (file.isFile()) {
            jackFiles.add(file);
        } else {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.getName().endsWith(".jack")) {
                    jackFiles.add(f);
                }
            }
        }

        for (File f : jackFiles) {

            JackTokenizer jackTokenizer = new JackTokenizer(f);
            String fileOutPath = f.getAbsolutePath().replace(".jack", ".xml");
            File fileOut = new File(fileOutPath);
            new CompilationEngine(jackTokenizer, fileOut).start();

            System.out.println(f.getName());

            //save file
            //writer.close();
            //System.out.println("File created: " + fileOutPath + System.lineSeparator());
        }
    }
}
