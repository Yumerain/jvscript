package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;
import jvlang.expr.Expression;

/**
 * 表达式语句
 * @author Yumerain
 */
public class ExprStatement implements Statement {

    public final Expression expression;

    public ExprStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        expression.eval(scope);
        return ExecutionResult.CONTINUE;
    }

}
