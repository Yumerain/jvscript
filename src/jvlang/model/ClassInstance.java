package jvlang.model;

import jvlang.Scope;
import jvlang.stmt.ClassDefinition;
import jvlang.stmt.FuncDefinition;

/**
 * 类实例化表示
 * @author Yumerain
 */
public class ClassInstance {

    public final ClassDefinition definition;
    public final Scope fields; // 存储字段值的独立作用域

    public ClassInstance(ClassDefinition def, Scope fields) {
        this.definition = def;
        this.fields = fields;
    }

    public FuncDefinition getMethod(String name) {
        return definition.getMethod(name);
    }

}
