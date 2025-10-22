package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;
import jvlang.expr.Expression;

/**
 * 返回值语句
 * @author Yumerain
 */
public class ReturnStatement implements Statement {

    // 可为 null，表示无返回值
    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        Object value = (expression != null) ? expression.eval(scope) : null;
        return ExecutionResult.returnWith(value);
    }
}