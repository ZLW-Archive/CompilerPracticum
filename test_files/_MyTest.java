class Test {
    public static void main(String[] a){
    }
}

class A {}

class B extends A {
    public int over() {
        int t;
        Start x;
        t = x.start();
        return 1;
    }
}

class Start {
    boolean start;
    public int start () {
        A a;
        int c;
        a = new B();
        c = a.over();
        return 0;
    }
}

