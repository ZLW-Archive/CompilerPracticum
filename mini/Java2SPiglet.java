package mini;
import java.io.*;
import mini.visitor.*;
import mini.syntaxtree.*;
import mini.symbol.*;

public class Java2SPiglet {
    int i;
    int args;
    public static void main(String[] args){
        try {
            String fileName = "_MyTest";
            String filePath = "./test_files/" + fileName + ".java";
            InputStream in = new FileInputStream(filePath);
            if (in == null)
                System.out.print("it is null");
            Node root = new MiniJavaParser(in).Goal();
            MType allClassList = new MClassList();
            root.accept(new BuildSymbolTableVisitor(), allClassList);
            ((MClassList) allClassList).printSymbolList(0);
            root.accept(new TypeCheckVisitor(), allClassList);
            String outPath = "./temp/" + fileName + ".spg";
            PrintStream ps = new PrintStream(new FileOutputStream(outPath));
            System.setOut(ps);
            root.accept(new ToSPigletVisitor((MClassList) allClassList), null);
//            System.out.println("All Finish!");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (TokenMgrError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

