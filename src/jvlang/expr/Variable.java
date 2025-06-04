package jvlang.expr;

import jvlang.Scope;

/**
 * 变量
 * @author Yumerain
 */
public class Variable implements Expression {

    public final String name;

    public Variable(String id) {
        this.name = id;
    }

    @Override
    public Object eval(Scope scope) {
        // 从作用域获取值
        return scope.getVariable(name);
    }
}
