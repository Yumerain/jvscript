package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;
import jvlang.model.FieldDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结构体定义
 * @author Yumerain
 */
public class ClassDefinition implements Statement {

    public final String name;

    public final List<FieldDeclaration> fields;

    public final List<FuncDefinition> methods;

    private final Map<String, FuncDefinition> methodMap = new HashMap<>();

    public ClassDefinition(String name, List<FieldDeclaration> fields) {
        this.name = name;
        this.fields = fields;
        this.methods = new ArrayList<>();
    }

    public ClassDefinition(String name, List<FieldDeclaration> fields, List<FuncDefinition> methods) {
        this.name = name;
        this.fields = fields;
        this.methods = methods != null ? methods : new ArrayList<FuncDefinition>();
        for (FuncDefinition method : this.methods) {
            methodMap.put(method.name, method);
        }
    }

    @Override
    public ExecutionResult exec(Scope scope) {
        for (FuncDefinition method : methods) {
            method.definitionScope = scope;
        }
        // 将类型注册到作用域
        scope.defineClass(name, this);
        return ExecutionResult.CONTINUE;
    }

    public FuncDefinition getMethod(String name) {
        return methodMap.get(name);
    }

}
