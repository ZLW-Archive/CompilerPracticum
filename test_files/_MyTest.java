
class Test{
    public static void main(String[] aa){
        B b;
        A a;

        int p;
        C c;
        D d;
        int[] pp;
        pp = new int[100];
        d = new D();
        p=d.setaa();
        p=d.hello();
        p=d.hello2();
        p=d.seta();
        p=d.hello();
        p=d.hello2();
        c = d;
        p=c.hello();
        p=c.hello2();
        p=c.seta();
        p=c.hello();
        p=c.hello2();
        if ((2<1) && (c.h()))
            System.out.println(999);
        else
            System.out.println(998);
        System.out.println(pp.length);

        b = new B();
        a = b.test();
        a = b.tt();
    }
}

class A{
    int b;
    public A test () {System.out.println(88888); return new A();}
    public int getb(int t1, int t2, int t3, int t4, int t5, int t6, int t7, int t8, int t9, int t10, int t11, int t12, int t13
    ,int t14, int t15, int t16, int t17, int t18) {return t18;}
}

class B extends A{
    int c;
    public A tt()
    {
        B bb;
        A aa;
        int [] cc;
        aa = new A();
        cc = new int[aa.getb(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18)];
        System.out.println(99999999);
        System.out.println(aa.getb(0,1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8));
        aa = new B();
        aa = aa.test();
        return aa;
    }
}

class C
{
    int a;
    public boolean h()
    {
        System.out.println(99);
        return false;
    }
    public int hello()
    {
        System.out.println(a);
        return 0;
    }
    public int hello2()
    {
        System.out.println(a);
        return 0;
    }
    public int seta()
    {
        a = 3;
        return 0;
    }
}
class D extends C
{
    int a;

    public int hello()
    {
        System.out.println(a);
        return 0;
    }

    public int setaa()
    {
        a = 1;
        return 0;
    }

}