package jvlang.stmt;

import jvlang.Scope;
import jvlang.expr.Expression;

import java.util.List;

/**
 * 条件语句
 * @author Yumerain
 */
public class IfStatement implements Statement {

    public final Expression condition;

    public final List<Statement> thenBranch;

    public final List<Statement> elseBranch;

    public IfStatement(Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public void exec(Scope scope) {
        // 1. 计算条件表达式
        Object condValue = condition.eval(scope);
        // 2. 确保结果为布尔类型
        if (!(condValue instanceof Boolean)) {
            throw new RuntimeException("If condition must be boolean, got " + condValue.getClass().getSimpleName());
        }
        boolean conditionResult = (Boolean) condValue;
        // 3. 根据条件执行对应分支
        if (conditionResult) {
            executeBranch(thenBranch, scope);
        } else if (elseBranch != null) {
            executeBranch(elseBranch, scope);
        }
    }

    // 执行分支语句（自动创建子作用域）
    private void executeBranch(List<Statement> branch, Scope parentScope) {
        Scope branchScope = new Scope(parentScope); // 创建子作用域
        for (Statement stmt : branch) {
            stmt.exec(branchScope);
        }
    }
}
