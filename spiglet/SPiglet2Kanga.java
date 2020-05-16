import syntaxtree.Node;
import visitor.BuildGraphVisitor;
import visitor.ToKangaVisitor;
import visitor.ToKangaVisitor_t;

import java.io.FileInputStream;
import java.io.InputStream;

public class SPiglet2Kanga {
    public static void main(String[] args) {
        try {
            String fileName = "_MyTest";
            String filePath = "./outputs/" + fileName + ".spg";
            InputStream in = new FileInputStream(filePath);
            Node root = new SpigletParser(in).Goal();
            BuildGraphVisitor buildGraphVisitor = new BuildGraphVisitor();
            root.accept(buildGraphVisitor);
            ToKangaVisitor toKangaVisitor = new ToKangaVisitor(buildGraphVisitor.label2flowGraph);

//            PrintStream ps = new PrintStream(new FileOutputStream("./outputs/temp.kanga"));
//            System.setOut(ps);

            root.accept(toKangaVisitor, null);
            System.out.println("All Finish!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

