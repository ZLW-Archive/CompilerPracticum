package spiglet.symbol;

public class Printer {

    public void print(String format, Object ... args) {
        System.out.printf(format, args);
    }

    public void print(String string) {
        System.out.printf(string);
    }

}
