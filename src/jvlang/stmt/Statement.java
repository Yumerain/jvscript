package jvlang.stmt;

import jvlang.ExecutionResult;
import jvlang.Scope;

/**
 * 语句
 * @author Yumerain
 */
public interface Statement {

    ExecutionResult exec(Scope scope);

}
