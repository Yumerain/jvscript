package jvlang.stmt;

import jvlang.Scope;
import jvlang.model.ReturnSignal;
import jvlang.expr.Expression;

/**
 * 返回值语句
 * @author Yumerain
 */
public class ReturnStatement implements Statement {

    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void exec(Scope scope) {
        Object value = (expression != null) ? expression.eval(scope) : null;
        throw new ReturnSignal(value); // 抛出返回值
    }
}