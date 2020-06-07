
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import kanga.syntaxtree.*;
import kanga.visitor.*;
import kanga.*;

public class Kanga2MIPS
{
    public static void main(String[] args)
    {
        //String[] fileNames = {"BinaryTree", "BubbleSort", "Factorial", "LinearSearch",
        //        "LinkedList", "MoreThan4", "QuickSort", "TreeVisitor"};

        try {

            String fileName;// = "_MyTest";
            //String filePath = "./temp/" + fileName + ".kg";
            String filePath;// = "./temp/"+fileName+".kg";
            String outputPath;// = "./temp/" + fileName + ".s";
            if (args.length != 0) {
                filePath = args[0];
                fileName = filePath.substring(0, args[0].length()-3);
                outputPath = fileName + ".s";
            }
            else
            {
                Scanner scanner = new Scanner(System.in);
                filePath = scanner.nextLine();
                fileName =filePath.substring(0, filePath.length()-3);
                outputPath = fileName + ".s";
            }
            //System.out.println("All Finish!");
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
