class MoreThan4{
    public static void main(String[] a){
        System.out.println(new MT4().test(new B()));
    }
}

class MT4 {
    public int test (A a) {return 1;}
}

class B extends A {int test; public int dosomething(){int test; ok = 10; return test;}}

class A extends C {}

class C extends B {int ok;}


