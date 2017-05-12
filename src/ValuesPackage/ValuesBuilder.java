package ValuesPackage;

public class ValuesBuilder {
    private int a;
    private int b;
    private int c;

    public ValuesBuilder setA(int a) {
        this.a = a;
        return this;
    }

    public ValuesBuilder setB(int b) {
        this.b = b;
        return this;
    }

    public ValuesBuilder setC(int c) {
        this.c = c;
        return this;
    }

    public Values createValues() {
        return new Values(a, b, c);
    }
}