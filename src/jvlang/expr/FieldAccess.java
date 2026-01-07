package jvlang.expr;

import jvlang.JvsException;
import jvlang.Scope;
import jvlang.model.ClassInstance;

/**
 * 字段访问
 * @author Yumerain
 */
public class FieldAccess implements Expression {

    public final Expression target;
    public final String fieldName;

    public FieldAccess(Expression target, String name) {
        this.target = target;
        this.fieldName = name;
    }

    @Override
    public Object eval(Scope scope) {
        // 1. 获取目标对象
        Object targetObj = target.eval(scope);

        // 2. 验证是否为结构体实例
        if (!(targetObj instanceof ClassInstance)) {
            throw new JvsException("Field access on non-struct type: "
                    + targetObj.getClass().getSimpleName());
        }

        ClassInstance instance = (ClassInstance) targetObj;

        // 3. 检查字段是否存在
        if (!instance.fields.hasVariable(fieldName)) {
            throw new JvsException("Undefined field '" + fieldName
                    + "' in struct " + instance.definition.name);
        }

        // 4. 返回字段值
        return instance.fields.getVariable(fieldName);
    }
}
