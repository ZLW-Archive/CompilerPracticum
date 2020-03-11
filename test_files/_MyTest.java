class MoreThan4{
    public static void main(String[] a){
        System.out.println(new MT4().test(new B()));
    }
}

class MT4 {
    public int test (A a) {return 1;}
}

class A {}

class B extends A {}

