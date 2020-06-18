import java.io.*;
import java.util.Scanner;

import minijava.visitor.*;
import minijava.symbol.*;
import minijava.*;
import spiglet.*;
import spiglet.visitor.*;
import kanga.*;
import kanga.visitor.*;

public class Java2MIPS {
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

            // miniJava typeCheck + toSPiglet
            minijava.syntaxtree.Node root = new MiniJavaParser(in).Goal();
            MType allClassList = new MClassList();
            root.accept(new BuildSymbolTableVisitor(), allClassList);
            ((MClassList) allClassList).printSymbolList(0);
            root.accept(new TypeCheckVisitor(), allClassList);
            String outPath = fileName + ".spg";
            PrintStream ps = new PrintStream(new FileOutputStream(outPath));
            System.setOut(ps);
            root.accept(new ToSPigletVisitor((MClassList) allClassList), null);

            // SPiglet to Kanga
            in = new FileInputStream(outPath);
            SpigletParser spigletParser = new SpigletParser(in);
            spiglet.syntaxtree.Node root2 = spigletParser.Goal();
            BuildGraphVisitor buildGraphVisitor = new BuildGraphVisitor();
            root2.accept(buildGraphVisitor);
            ToKangaVisitor toKangaVisitor = new ToKangaVisitor(buildGraphVisitor.label2flowGraph);
            outPath = fileName+".kg";
            ps = new PrintStream(new FileOutputStream(outPath));
            System.setOut(ps);
            root2.accept(toKangaVisitor, null);

            // Kanga to Mips
            in = new FileInputStream(outPath);
            KangaParser kangaParser = new KangaParser(in);
            kanga.syntaxtree.Node root3 = kangaParser.Goal();
            ToMIPSVisitor toMIPSVisitor = new ToMIPSVisitor();
            outPath = fileName+".s";
            ps = new PrintStream(new FileOutputStream(outPath));
            System.setOut(ps);
            root3.accept(toMIPSVisitor, null);

//            System.out.println("All Finish!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

