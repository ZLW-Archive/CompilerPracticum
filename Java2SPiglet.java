import java.io.*;
import java.util.Scanner;

import mini.visitor.*;
import mini.syntaxtree.*;
import mini.symbol.*;
import mini.*;

public class Java2SPiglet {
    //int i;
    //int args;

    public static void main(String[] args){
        try {
            Scanner scanner;
            String fileName;
            String filePath;
            if (args.length != 0)
            {
                fileName = args[0].substring(0, args[0].length()-5);
                filePath = args[0];
            }
            else
            {
                scanner = new Scanner(System.in);
                filePath = scanner.nextLine();
                fileName = filePath.substring(0, filePath.length()-5);
            }
            InputStream in = new FileInputStream(filePath);
            if (in == null)
                System.out.print("it is null");
            Node root = new MiniJavaParser(in).Goal();
            MType allClassList = new MClassList();
            root.accept(new BuildSymbolTableVisitor(), allClassList);
            ((MClassList) allClassList).printSymbolList(0);
            root.accept(new TypeCheckVisitor(), allClassList);
            String outPath = fileName + ".spg";
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

