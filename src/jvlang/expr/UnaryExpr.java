package jvlang.expr;

import jvlang.Scope;
import jvlang.Symbol;

/**
 * 一元运算表达式
 * @author Yumerain
 */
public class UnaryExpr implements Expression {

    public final Symbol operator;

    public final Expression right;

    public UnaryExpr(Symbol operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object eval(Scope scope) {
        return null;
    }
}
