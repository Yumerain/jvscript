package jvlang.stmt;

import jvlang.Scope;
import jvlang.Symbol;
import jvlang.expr.Expression;

/**
 * 变量声明语句 <var-declaration>
 * @author Yumerain
 */
public class VarDeclaration implements Statement {

    public final String identifier;

    public final Expression initializer;

    public Symbol type;

    public VarDeclaration(String identifier, Expression initializer) {
        this.identifier = identifier;
        this.initializer = initializer;
    }

    public void exec(Scope scope) {
        Object value = null;
        if (initializer != null) {
            value = initializer.eval(scope);
            // 如果未指定类型，推断类型
            if (this.type == null) {
                this.type = inferType(value);
            } else {
                scope.checkType(this.type, value); // 检查初始化值是否符合指定类型
            }
        }
        scope.declareVariable(identifier, this.type, value);
    }

    // 根据值推断类型
    private Symbol inferType(Object value) {
        if (value instanceof Long) return Symbol.INT;
        if (value instanceof Double) return Symbol.FLOAT;
        if (value instanceof Boolean) return Symbol.BOOL;
        if (value instanceof String) return Symbol.TEXT;
        throw new RuntimeException("Cannot infer type for value: " + value);
    }

}
