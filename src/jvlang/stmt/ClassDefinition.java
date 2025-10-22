package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;
import jvlang.model.FieldDeclaration;

import java.util.List;

/**
 * 结构体定义
 * @author Yumerain
 */
public class ClassDefinition implements Statement {

    public final String name;

    public final List<FieldDeclaration> fields;

    public ClassDefinition(String name, List<FieldDeclaration> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        // 将结构体类型注册到作用域
        scope.defineClass(name, this);
        return ExecutionResult.CONTINUE;
    }

}
