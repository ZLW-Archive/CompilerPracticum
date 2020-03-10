import java.io.*;
import visitor.*;
import syntaxtree.*;
import symbol.*;

class MyVisitor extends DepthFirstVisitor {
    public void visit(VarDeclaration n) {
        Identifier id = (Identifier)n.f1;
        System.out.println("VarName: " + id.f0.toString());
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }
}

public class Main {
    public static void main(String[] args){
        try {
            String filename = "test_files/Factorial-error.java";
            InputStream in = new FileInputStream(filename);
            Node root = new MiniJavaParser(in).Goal();
            MType allClassList = new MClassList();
            root.accept(new BuildSymbolTableVisitor(), allClassList);
            root.accept(new TypeCheckVisitor(), allClassList);
            System.out.println("All Finish!");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (TokenMgrError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

