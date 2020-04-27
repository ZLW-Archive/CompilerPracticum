import java.io.*;
import visitor.*;
import syntaxtree.*;
import symbol.*;

public class Main {
    int i;
    int args;
    public static void main(String[] args){
        try {
            String filename = "./test_files/_MyTest.java";
            InputStream in = new FileInputStream(filename);
            if (in == null)
                System.out.print("it is null");
            Node root = new MiniJavaParser(in).Goal();
            MType allClassList = new MClassList();
            root.accept(new BuildSymbolTableVisitor(), allClassList);
            ((MClassList) allClassList).printSymbolList(0);
            root.accept(new TypeCheckVisitor(), allClassList);
//            PrintStream ps = new PrintStream(new FileOutputStream(".//test.spg"));
//            System.setOut(ps);
            root.accept(new ToPigletVisitor((MClassList) allClassList), null);
//            PrintStream sps = new PrintStream(new FileOutputStream(".//stest.spg"));
//            System.setOut(sps);
            root.accept(new ToSPigletVisitor((MClassList) allClassList), null);
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

