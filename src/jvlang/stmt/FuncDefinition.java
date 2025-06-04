package jvlang.stmt;

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

    public final Scope definitionScope; // 新增字段

    public FuncDefinition(String name, List<String> parameters, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.definitionScope = new Scope();
    }

    @Override
    public void exec(Scope scope) {
        // 将函数注册到当前作用域
        scope.defineFunction(name, this);
    }
}
