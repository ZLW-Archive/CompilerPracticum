class test {
	public static void main(String[] asdfghjk) {
		A a;
		B b;
		boolean c;
		int d;
		E e;
		A f;
		C g;
		D h;
		int[] i;
		i = new int[20];
		d = 19;
		while (0 < d) {
			i[d] = d;
			d = d - 1;
		}
		e = new E();
		b = new B();
		g = new C();
		f = e.e(b);
		a = b;
		System.out.println(b.print());
		System.out.println(a.add());
		System.out.println(b.add());
		System.out.println(a.add());
		System.out.println(b.print());
		c = b.set_true();
		c = a.set_false();
		if ((a.loc()) && (a.set_true())) {
			System.out.println(5428543);
		}
		else {
			if (b.loc()) {
				System.out.println(5428543);
			}
			else {
				System.out.println(666);
			}
		}
		while ((b.show()) < 50) {
			d = a.add();
			System.out.println(b.show());
			System.out.println(g.func(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
			h = new D();
			System.out.println(h.func(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, i[13], i[14],15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
		}
	}
}

class E {
	public A e(A a) {
		int b;
		b = a.add();
		return a;
	}
}

class A {
	int memb;
	boolean loc;

	public int print() {
		return memb;
	}

	public int add() {
		memb = memb + 10;
		return memb;
	}

	public boolean set_true() {
		loc = true;
		return loc;
	}

	public boolean set_false() {
		loc = false;
		return loc;
	}

	public boolean loc() {
		return loc;
	}
}

class B extends A {
	int memb;

	public int add() {
		memb = memb + 10;
		return memb;
	}

	public boolean set_false() {
		loc = false;
		return loc;
	}

	public int show() {
		return memb;
	}
}

class C extends B {
	public int func(int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11,
			int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19, int a20, int a21, int a22, int a23,int a24) {
		return a13 + a24;
	}
}

class D extends C {
	public int func(int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11,
			int a12, int a13, int a14, int a15, int a16, int a17, int a18, int a19, int a20, int a21, int a22, int a23,int a24) {
		return a2 * a13;
	}
}

