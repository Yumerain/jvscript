package jvlang.stmt;

import jvlang.Scope;

/**
 * 语句
 * @author Yumerain
 */
public interface Statement {

    void exec(Scope scope);

}
