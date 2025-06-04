package jvlang;

import jvlang.stmt.Statement;

import java.util.List;

/**
 * 程序根节点
 * @author Yumerain
 */
public class Program {

    public final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    public void exec(Scope scope) {
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).exec(scope);
        }
    }
}
