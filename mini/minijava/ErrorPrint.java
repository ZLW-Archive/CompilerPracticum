package mini.minijava;

public class ErrorPrint {

    public static void print(String s) {
        System.out.println(s);
        System.exit(233);
    }

    public static void print(String format, Object ... args) {
        System.out.printf(format + "\n", args);
        System.exit(233);
    }

}
