package jvlang.stmt;

import jvlang.Scope;
import jvlang.expr.Expression;
import jvlang.expr.FieldAccess;
import jvlang.expr.Variable;
import jvlang.model.ClassInstance;

/**
 * 赋值语句
 * <assignment>
 * @author Yumerain
 */
public class Assignment implements Statement {

    public final Expression target; // 可以是 Variable 或 FieldAccess

    public final Expression value;

    public Assignment(Expression target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public void exec(Scope scope) {
        Object value = this.value.eval(scope);

        if (target instanceof Variable) {
            // 普通变量赋值
            String varName = ((Variable) target).name;
            scope.setVariable(varName, value);
        } else if (target instanceof FieldAccess) {
            // 字段赋值
            FieldAccess fieldAccess = (FieldAccess) target;
            Object instance = fieldAccess.target.eval(scope);

            if (!(instance instanceof ClassInstance)) {
                throw new RuntimeException("Field assignment on non-struct instance");
            }

            ClassInstance struct = (ClassInstance) instance;
            struct.fields.setVariable(fieldAccess.fieldName, value);
        } else {
            throw new RuntimeException("Invalid assignment target");
        }
    }

}
