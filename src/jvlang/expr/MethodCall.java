package jvlang.expr;

import jvlang.ExecutionResult;
import jvlang.JvsException;
import jvlang.Scope;
import jvlang.model.ClassInstance;
import jvlang.stmt.FuncDefinition;
import jvlang.stmt.Statement;

import java.util.List;

/**
 * 方法调用表达式
 * @author Yumerain
 */
public class MethodCall implements Expression {

    public final Expression target;
    public final String methodName;
    public final List<Expression> arguments;

    public MethodCall(Expression target, String methodName, List<Expression> arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public Object eval(Scope scope) {
        Object targetObj = target.eval(scope);

        if (!(targetObj instanceof ClassInstance)) {
            throw new JvsException("Method call on non-class instance: " + targetObj.getClass().getSimpleName());
        }

        ClassInstance instance = (ClassInstance) targetObj;
        FuncDefinition method = instance.getMethod(methodName);

        if (method == null) {
            throw new JvsException("Undefined method '" + methodName + "' in class " + instance.definition.name);
        }

        if (arguments.size() != method.parameters.size()) {
            throw new JvsException("Method " + methodName + " expects " +
                    method.parameters.size() + " arguments but got " + arguments.size());
        }

        Scope methodScope = new Scope(instance.fields);
        methodScope.declareVariable("this", instance);

        for (int i = 0; i < method.parameters.size(); i++) {
            String paramName = method.parameters.get(i);
            Object argValue = arguments.get(i).eval(scope);
            methodScope.declareVariable(paramName, argValue);
        }

        for (Statement stmt : method.body) {
            ExecutionResult result = stmt.exec(methodScope);
            if (result.isReturn) {
                return result.returnValue;
            }
        }

        return null;
    }

}
