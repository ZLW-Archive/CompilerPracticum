package kanga;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import kanga.syntaxtree.*;
import kanga.visitor.*;

public class Kanga2MIPS
{
    public static void main(String[] args)
    {
        String[] fileNames = {"BinaryTree", "BubbleSort", "Factorial", "LinearSearch",
                "LinkedList", "MoreThan4", "QuickSort", "TreeVisitor"};

        try {

            String fileName = "_MyTest";
            //String filePath = "./temp/" + fileName + ".kg";
            String filePath = "./temp/"+fileName+".kg";
            String outputPath = "./temp/" + fileName + ".s";
            if (args.length != 0) {
                filePath = args[0];
                outputPath = args[0] + ".kg";
            }
            System.out.println("All Finish!");
            InputStream in = new FileInputStream(filePath);
            KangaParser kangaParser = new KangaParser(in);
            //System.out.println("All Finish!");
            Node root = kangaParser.Goal();
            //BuildGraphVisitor buildGraphVisitor = new BuildGraphVisitor();
            //root.accept(buildGraphVisitor);
            ToMIPSVisitor toMIPSVisitor = new ToMIPSVisitor();
            //System.out.println("All Finish!");
            PrintStream ps = new PrintStream(new FileOutputStream(outputPath));
            System.setOut(ps);
            //System.out.println("All Finish!");
            root.accept(toMIPSVisitor, null);
            //System.out.println("All Finish!");
            PrintStream log = new PrintStream("./log.txt");
            for (String file : fileNames) {
                fileName = file;
                //System.out.println(fileName);
                System.setOut(log);
                filePath = "./kanga/inputs/" + fileName + ".kg";
                System.out.println(filePath);
                in = new FileInputStream(filePath);
                kangaParser.ReInit(in);
                root = kangaParser.Goal();

                ps = new PrintStream(new FileOutputStream("./kanga/outputs/" + fileName + ".s"));
                System.setOut(ps);
                root.accept(toMIPSVisitor, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
