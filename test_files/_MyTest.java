class Test{
    public static void main(String[] a){
    }
}

class A{
    public A test () {return new A();}
}

class B extends A{
    public A test () {return new B();}
}

