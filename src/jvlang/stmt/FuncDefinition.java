package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;

import java.util.List;

/**
 * 函数定义语句
 * @author Yumerain
 */
public class FuncDefinition implements Statement {

    public final String name;

    public final List<String> parameters;

    public final List<Statement> body;

    public Scope definitionScope;

    public FuncDefinition(String name, List<String> parameters, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        // 捕获定义时的作用域
        this.definitionScope = scope;
        // 将函数注册到当前作用域
        this.definitionScope.defineFunction(name, this);
        return ExecutionResult.CONTINUE;
    }
}
