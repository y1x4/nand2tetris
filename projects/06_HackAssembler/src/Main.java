import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        Parser p = new Parser(args[0]);
        // Parser p = new Parser("/Users/yixu/Desktop/nand2tetris/projects/06/Max/MaxL.asm");


        /*System.out.println(new Code().jump("JGT"));

        Parser p = new Parser();
        String s1 = "dest=comp;jump";
        String s2 = "comp;jump";
        String s3 = "dest=comp";
        String[] s = {s1, s2, s3};
        for (String str : s) {
            p.curInstruction = str;
            System.out.println(p.dest());
            System.out.println(p.comp());
            System.out.println(p.jump());
        }

        //System.out.println(p.toHexAndFill(200));
        String[] s5 = {"@i", "M=1", "@sum", "M=0", "(LOOP)", "@LOOP", "@i", "D=M"};
        Parser p = new Parser();
        for (int i = 0; i < s5.length; i++) {
            p.instructions.add(s5[i]);
        }
        p.doParse();
        */

    }
}
