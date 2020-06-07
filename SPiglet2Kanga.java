import spiglet.syntaxtree.Node;
import spiglet.visitor.BuildGraphVisitor;
import spiglet.visitor.ToKangaVisitor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import spiglet.*;

public class SPiglet2Kanga {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            String fileName;// = scanner.nextLine();
            String filePath;// = fileName + ".spg";
            String outputPath;// = fileName + ".kg";
            if (args.length != 0) {
                filePath = args[0];
                fileName = filePath.substring(0, args[0].length()-4);
                outputPath = fileName + ".kg";
            }
            else
            {
                filePath = scanner.nextLine();
                fileName = filePath.substring(0, filePath.length()-4);
                outputPath = fileName+".kg";
            }
            InputStream in = new FileInputStream(filePath);
            SpigletParser spigletParser = new SpigletParser(in);
            Node root = spigletParser.Goal();
            BuildGraphVisitor buildGraphVisitor = new BuildGraphVisitor();
            root.accept(buildGraphVisitor);
            ToKangaVisitor toKangaVisitor = new ToKangaVisitor(buildGraphVisitor.label2flowGraph);

            PrintStream ps = new PrintStream(new FileOutputStream(outputPath));
            System.setOut(ps);

            root.accept(toKangaVisitor, null);
//            System.out.println("All Finish!");

//            for (String file : fileNames) {
//                fileName = file;
//                filePath = "./outputs/" + fileName + ".spg";
//                in = new FileInputStream(filePath);
//                spigletParser.ReInit(in);
//                root = spigletParser.Goal();
//                buildGraphVisitor = new BuildGraphVisitor();
//                root.accept(buildGraphVisitor);
//                toKangaVisitor = new ToKangaVisitor(buildGraphVisitor.label2flowGraph);
//
//                ps = new PrintStream(new FileOutputStream("./outputs/" + fileName + ".kg"));
//                System.setOut(ps);
//                root.accept(toKangaVisitor, null);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

