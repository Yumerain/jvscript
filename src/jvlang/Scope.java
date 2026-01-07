package jvlang;

import jvlang.stmt.FuncDefinition;
import jvlang.stmt.ClassDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * 作用域
 * @author Yumerain
 */
public class Scope {
    // 类定义
    private final Map<String, ClassDefinition> classes = new HashMap<>();
    // 函数定义
    private final Map<String, FuncDefinition> functions = new HashMap<>();
    // 变量定义
    private final Map<String, Object> variables = new HashMap<>();
    // 类型定义
    private final Map<String, Symbol> types = new HashMap<>();
    // 父级作用域
    private final Scope parent;

    public Scope() { this.parent = null; }
    public Scope(Scope parent) { this.parent = parent; }

    public void declareVariable(String name, Object value) {
        if (variables.containsKey(name)) {
            throw new JvsException("Variable already declared: " + name);
        }
        variables.put(name, value);
    }

    public void declareVariable(String name, Symbol type, Object value) {
        if (variables.containsKey(name)) {
            throw new JvsException("Variable already declared: " + name);
        }
        variables.put(name, value);
        types.put(name, type);
    }

    public void setVariable(String name, Object value) {
        if (variables.containsKey(name)) {
            Symbol type = types.get(name);
            checkType(type, value); // 检查赋值是否符合类型
            variables.put(name, value);
        } else if (parent != null) {
            parent.setVariable(name, value);
        } else {
            throw new JvsException("Undefined variable: " + name);
        }
    }

    public Object getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.getVariable(name);
        } else {
            throw new JvsException("Undefined variable: " + name);
        }
    }

    public Symbol getType(String name) {
        if (types.containsKey(name)) {
            return types.get(name);
        } else if (parent != null) {
            return parent.getType(name);
        } else {
            throw new JvsException("Undefined variable: " + name);
        }
    }

    public void checkType(Symbol expectedType, Object value) {
        if (expectedType == null) return; // 如果未指定类型，跳过检查
        if (expectedType == Symbol.INT && !(value instanceof Long)) {
            throw new JvsException("Type mismatch: expected int, got " + value.getClass().getSimpleName());
        }
        if (expectedType == Symbol.FLOAT && !(value instanceof Double)) {
            throw new JvsException("Type mismatch: expected float, got " + value.getClass().getSimpleName());
        }
        if (expectedType == Symbol.BOOL && !(value instanceof Boolean)) {
            throw new JvsException("Type mismatch: expected bool, got " + value.getClass().getSimpleName());
        }
        if (expectedType == Symbol.STRING && !(value instanceof String)) {
            throw new JvsException("Type mismatch: expected string, got " + value.getClass().getSimpleName());
        }
    }

    // 存储函数定义
    public void defineFunction(String name, FuncDefinition function) {
        if (functions.containsKey(name)) {
            throw new JvsException("Function already defined: " + name);
        }
        functions.put(name, function);
    }

    // 查找函数定义（支持作用域链）
    public FuncDefinition getFunction(String name) {
        if (functions.containsKey(name)) {
            return functions.get(name);
        }
        return parent != null ? parent.getFunction(name) : null;
    }

    public void defineClass(String name, ClassDefinition clxss) {
        if (classes.containsKey(name)) {
            throw new JvsException("class already defined: " + name);
        }
        classes.put(name, clxss);
    }

    public ClassDefinition getClassDefine(String name) {
        ClassDefinition clxss = classes.get(name);
        if (clxss == null && parent != null) {
            return parent.getClassDefine(name);
        }
        return clxss;
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name) || (parent != null && parent.hasVariable(name));
    }

}
