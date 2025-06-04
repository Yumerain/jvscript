package jvlang.model;

import jvlang.expr.Expression;

/**
 * 字段声明内部类
 * @author Yumerain
 */
public class FieldDeclaration {

    public final String name;
    public final Expression initializer;

    public FieldDeclaration(String name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

}
