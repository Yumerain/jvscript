package jvlang.expr;

import jvlang.ExecutionResult;
import jvlang.JvsException;
import jvlang.Scope;
import jvlang.stmt.FuncDefinition;
import jvlang.stmt.Statement;

import java.util.List;

/**
 * 函数调用
 * <unary-expression> ::= ("-" | "!") <factor>
 * @author Yumerain
 */
public class FuncCall implements Expression {

    private final String name;

    private final List<Expression> args;

    public FuncCall(String name, List<Expression> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public Object eval(Scope scope) {
        // 打印函数print
        if ("print".equals(name)) {
            for (Expression arg : args) {
                System.out.print(arg.eval(scope));
            }
            return null; // print 不返回值
        }
        // 打印换行函数println
        if ("println".equals(name)) {
            for (Expression arg : args) {
                System.out.print(arg.eval(scope));
            }
            System.out.println();
            return null; // println 不返回值
        }
        // 用户自定义函数调用实现
        // 1. 查找函数定义
        FuncDefinition function = scope.getFunction(name);
        if (function == null) {
            throw new JvsException("Undefined function: " + name);
        }

        // 2. 验证参数数量
        if (args.size() != function.parameters.size()) {
            throw new JvsException("Function " + name + " expects " +
                    function.parameters.size() + " arguments but got " + args.size());
        }

        // 3. 创建函数作用域（继承函数定义时的作用域）
        Scope funcScope = new Scope(function.definitionScope);

        // 4. 绑定参数值
        for (int i = 0; i < function.parameters.size(); i++) {
            String paramName = function.parameters.get(i);
            Object argValue = args.get(i).eval(scope);
            funcScope.declareVariable(paramName, argValue);
        }

        // 5. 执行函数体
        for (Statement stmt : function.body) {
            ExecutionResult result = stmt.exec(funcScope);
            if (result.isReturn) {
                return result.returnValue; // 直接返回
            }
        }
        return null; // 隐式返回 null
    }

}
