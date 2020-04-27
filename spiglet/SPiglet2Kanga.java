import java.io.*;
import visitor.*;
import syntaxtree.*;

public class SPiglet2Kanga {
    public static void main(String[] args){
        try {
            String fileName = "_MyTest";
            String filePath = "./outputs/" + fileName + ".sp";
            InputStream in = new FileInputStream(filePath);
            Node root = new SpigletParser(in).Goal();
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

