package minijava;

public class ErrorPrint {

    public static void print(String s) {
        System.out.println(s);
    }

    public static void print(String format, Object ... args) {
        System.out.printf(format + "\n", args);
    }

}
