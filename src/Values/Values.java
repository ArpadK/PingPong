package Values;

public class Values {
    private final int a;
    private final int b;
    private final int c;

    private Values(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static Values createValues(int a, int b, int c) {
        return new Values(a, b, c);
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }
}
