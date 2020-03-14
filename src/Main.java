import java.io.*;
import visitor.*;
import syntaxtree.*;
import symbol.*;

public class Main {
    public static void main(String[] args){
        try {
            String filename = "test_files/_MyTest.java";
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

