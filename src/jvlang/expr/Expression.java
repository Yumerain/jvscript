package jvlang.expr;

import jvlang.Scope;

/**
 * 表达式
 * @author Yumerain
 */
public interface Expression {

    Object eval(Scope scope);

}
