package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;
import jvlang.expr.Expression;

import java.util.List;

/**
 * 循环语句
 * @author Yumerain
 */
public class WhileLoop implements Statement {

    public final Expression condition;
    public final List<Statement> body;

    public WhileLoop(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        while (true) {
            // 1. 计算条件
            Object condValue = condition.eval(scope);

            // 2. 检查布尔类型
            if (!(condValue instanceof Boolean)) {
                throw new RuntimeException("While condition must be boolean, got " + condValue.getClass().getSimpleName());
            }

            // 3. 条件为 false 时退出循环
            if (!(Boolean) condValue) break;

            // 4. 执行循环体（每次迭代创建新作用域）
            Scope loopScope = new Scope(scope);
            for (Statement stmt : body) {
                stmt.exec(loopScope);
            }
        }
        return ExecutionResult.CONTINUE;
    }
}
