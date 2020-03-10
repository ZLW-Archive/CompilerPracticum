class MyTest{
    public static void main(String[] s){
    }
}

class a {
    public int x () { return 1; }
}

class b extends a {
    int t;
    public int x(int t) {
        return 1;
    }

    public int y () {
        int t;
        t = this.x();
        return 1;
    }
}
