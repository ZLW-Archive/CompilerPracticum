import syntaxtree.Node;
import visitor.BuildGraphVisitor;

import java.io.FileInputStream;
import java.io.InputStream;

public class SPiglet2Kanga {
    public static void main(String[] args) {
        try {
            String fileName = "_MyTest";
            String filePath = "./outputs/" + fileName + ".spg";
            InputStream in = new FileInputStream(filePath);
            Node root = new SpigletParser(in).Goal();
            root.accept(new BuildGraphVisitor());
            System.out.println("All Finish!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

