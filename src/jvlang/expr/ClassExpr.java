package jvlang.expr;

import jvlang.JvsException;
import jvlang.Scope;
import jvlang.model.FieldDeclaration;
import jvlang.model.ClassInstance;
import jvlang.stmt.ClassDefinition;

import java.util.Map;

/**
 * 类实例化表达式
 * @author Yumerain
 */
public class ClassExpr implements Expression {

    public final String className;
    public final Map<String, Expression> fieldInitializers;

    public ClassExpr(String className, Map<String, Expression> fieldInitializers) {
        this.className = className;
        this.fieldInitializers = fieldInitializers;
    }

    @Override
    public Object eval(Scope scope) {
        // 1. 获取结构体定义
        ClassDefinition classDef = scope.getClassDefine(className);
        if (classDef == null) {
            throw new JvsException("Undefined struct: " + className);
        }
        // 2. 创建实例作用域
        Scope instanceScope = new Scope(scope);
        // 3. 初始化字段值
        for (FieldDeclaration field : classDef.fields) {
            // 3.1 检查是否提供了初始化值
            if (fieldInitializers.containsKey(field.name)) {
                Object value = fieldInitializers.get(field.name).eval(scope);
                instanceScope.declareVariable(field.name, value);
            } else if (field.initializer != null) {
                // 使用默认初始化表达式
                Object value = field.initializer.eval(scope);
                instanceScope.declareVariable(field.name, value);
            } else {
                // 未初始化字段设为null
                instanceScope.declareVariable(field.name, null);
            }
        }
        return new ClassInstance(classDef, instanceScope);
    }

}
