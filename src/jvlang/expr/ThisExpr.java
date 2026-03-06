package jvlang.expr;

import jvlang.JvsException;
import jvlang.Scope;
import jvlang.model.ClassInstance;

/**
 * this表达式
 * @author Yumerain
 */
public class ThisExpr implements Expression {

    @Override
    public Object eval(Scope scope) {
        Object thisInstance = scope.getVariable("this");
        if (!(thisInstance instanceof ClassInstance)) {
            throw new JvsException("'this' is not bound to a class instance");
        }
        return thisInstance;
    }

}
