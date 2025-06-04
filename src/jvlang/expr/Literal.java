package jvlang.expr;

import jvlang.Scope;

/**
 * 字面量
 * @author Yumerain
 */
public class Literal implements Expression {

    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public Object eval(Scope scope) {
        return value;
    }
}
