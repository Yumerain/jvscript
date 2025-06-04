package jvlang;

/**
 * 标记
 * @author Yumerain
 */
public class Token {

    public final Symbol symbol;

    public final Object value;

    public final int line;

    public Token(Symbol symbol, int line) {
        this.symbol = symbol;
        this.value = null;
        this.line = line;
    }

    public Token(Symbol symbol, Object value, int line) {
        this.symbol = symbol;
        this.value = value;
        this.line = line;
    }

    @Override
    public String toString() {
        return symbol.value() + (value != null ? "("+ value +")" : "");
    }
}