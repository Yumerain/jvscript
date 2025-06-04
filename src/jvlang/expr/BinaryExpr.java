package jvlang.expr;

import jvlang.Scope;
import jvlang.Symbol;

/**
 * 二元运算符节点（如 a + b）
 * @author Yumerain
 */
public class BinaryExpr implements Expression {

    // 整数统一用long
    public static final int LONG = 1;
    // 小数统一用double
    public static final int DOUBLE = 2;
    // 其它
    public static final int UNKNOWN = 99;

    public final Expression left;
    public final Symbol operator;
    public final Expression right;

    public BinaryExpr(Expression left, Symbol operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object eval(Scope scope) {
        Object leftValue = left.eval(scope);
        Object rightValue = right.eval(scope);

        // 1. 处理逻辑运算符（需要优先处理）
        if (operator == Symbol.AND || operator == Symbol.OR) {
            return logical(leftValue, rightValue);
        }

        // 2. 处理比较运算符
        if (isComparison(operator)) {
            return comparison(leftValue, rightValue);
        }

        // 数字之间的运算
        if (leftValue instanceof Number && rightValue instanceof Number) {
            Number l = (Number)leftValue;
            Number r = (Number)rightValue;
            // 获取最大类型做为运算结果
            int finalType = bigType(l, r);
            if (finalType == UNKNOWN) {
                throw new RuntimeException("Unsupported data type " + l.getClass().getName() + " and " + r.getClass().getName());
            }
            switch (operator) {
                case PLUS:
                    return plus(finalType, l, r);
                case MINUS:
                    return minus(finalType, l, r);
                case MULTIPLY:
                    return multiply(finalType, l, r);
                case DIVIDE:
                    return divide(finalType, l, r);
                case MODULO:
                    return modulo(finalType, l, r);
                default :
                    throw new RuntimeException("Unsupported operator: " + operator);
            }
        }
        // 字符串加法运算
        if (operator == Symbol.PLUS) {
            if (leftValue instanceof String || rightValue instanceof String) {
                return String.valueOf(leftValue).concat(String.valueOf(rightValue));
            }
        }
        // 其它类型的运算不支持
        throw new RuntimeException("Unsupported operation type: " + leftValue + " " +  operator + " " + rightValue);
    }


    // 加法
    private Number plus(int maxType, Number left, Number right) {
        switch (maxType) {
            case LONG:
                return Long.valueOf(left.longValue() + right.longValue());
            case DOUBLE:
                return Double.valueOf(left.doubleValue() + right.doubleValue());
        }
        throw new RuntimeException("Unsupported data type for plus");
    }

    // 减法
    private Number minus(int maxType, Number left, Number right) {
        switch (maxType) {
            case LONG:
                return Long.valueOf(left.longValue() - right.longValue());
            case DOUBLE:
                return Double.valueOf(left.doubleValue() - right.doubleValue());
        }
        throw new RuntimeException("Unsupported data type for minus");
    }

    // 乘法
    private Number multiply(int maxType, Number left, Number right) {
        switch (maxType) {
            case LONG:
                return Long.valueOf(left.longValue() * right.longValue());
            case DOUBLE:
                return Double.valueOf(left.doubleValue() * right.doubleValue());
        }
        throw new RuntimeException("Unsupported data type for multiply");
    }

    // 除法
    private Number divide(int maxType, Number left, Number right) {
        switch (maxType) {
            case LONG:
                return Long.valueOf(left.longValue() / right.longValue());
            case DOUBLE:
                return Double.valueOf(left.doubleValue() / right.doubleValue());
        }
        throw new RuntimeException("Unsupported data type for divide");
    }

    // 取模
    private Number modulo(int maxType, Number left, Number right) {
        switch (maxType) {
            case LONG:
                return Long.valueOf(left.longValue() % right.longValue());
            case DOUBLE:
                return Double.valueOf(left.doubleValue() % right.doubleValue());
        }
        throw new RuntimeException("Unsupported data type for modulo");
    }

    // 最大的类型
    static int bigType(Number obj1, Number obj2) {
        int t1 = numberType(obj1);
        int t2 = numberType(obj2);
        return Math.max(t1, t2);
    }

    // 数字的具体类型
    static int numberType(Number num) {
        if (num instanceof Long) {
            return LONG;
        } else if (num instanceof Double) {
            return DOUBLE;
        }
        return UNKNOWN;
    }

    // 新增方法：处理逻辑运算符
    private Boolean logical(Object leftVal, Object rightVal) {
        // 类型检查
        if (!(leftVal instanceof Boolean)) {
            throw new RuntimeException("Left operand of " + operator + " must be boolean");
        }
        if (!(rightVal instanceof Boolean)) {
            throw new RuntimeException("Right operand of " + operator + " must be boolean");
        }

        boolean a = (Boolean) leftVal;
        boolean b = (Boolean) rightVal;

        switch (operator) {
            case AND: return a && b;
            case OR:  return a || b;
            default:  throw new RuntimeException("Unreachable code");
        }
    }

    // 新增方法：处理比较运算符
    private Boolean comparison(Object leftVal, Object rightVal) {
        // 处理 null 相等性
        if (leftVal == null || rightVal == null) {
            return nullComparison(leftVal, rightVal);
        }

        // 数字比较
        if (leftVal instanceof Number && rightVal instanceof Number) {
            return numberComparison(
                    ((Number) leftVal).doubleValue(),
                    ((Number) rightVal).doubleValue()
            );
        }

        // 字符串比较
        if (leftVal instanceof String && rightVal instanceof String) {
            return stringComparison(
                    (String) leftVal,
                    (String) rightVal
            );
        }

        // 布尔值比较
        if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
            return booleanComparison(
                    (Boolean) leftVal,
                    (Boolean) rightVal
            );
        }

        // 默认对象比较（仅支持 == 和 !=）
        return objectComparison(leftVal, rightVal);
    }

    // 数字比较具体实现
    private Boolean numberComparison(double a, double b) {
        switch (operator) {
            case EQUAL:         return a == b;
            case NOTEQUAL:      return a != b;
            case LESS:          return a < b;
            case GREATER:       return a > b;
            case LESS_EQUAL:    return a <= b;
            case GREATER_EQUAL: return a >= b;
            default: throw new RuntimeException("Invalid comparison operator for numbers: " + operator);
        }
    }

    // 字符串比较具体实现
    private Boolean stringComparison(String a, String b) {
        switch (operator) {
            case EQUAL:    return a.equals(b);
            case NOTEQUAL: return !a.equals(b);
            case LESS:     return a.compareTo(b) < 0;
            case GREATER:  return a.compareTo(b) > 0;
            case LESS_EQUAL:    return a.compareTo(b) <= 0;
            case GREATER_EQUAL: return a.compareTo(b) >= 0;
            default: throw new RuntimeException("Invalid comparison operator for strings: " + operator);
        }
    }

    // 布尔值比较具体实现
    private Boolean booleanComparison(boolean a, boolean b) {
        switch (operator) {
            case EQUAL:    return a == b;
            case NOTEQUAL: return a != b;
            default: throw new RuntimeException("Invalid comparison operator for booleans: " + operator);
        }
    }

    // null值比较处理
    private Boolean nullComparison(Object a, Object b) {
        // 只有 == 和 != 支持null比较
        switch (operator) {
            case EQUAL:    return a == b;
            case NOTEQUAL: return a != b;
            default: throw new RuntimeException("Null values can only be compared with == or !=");
        }
    }

    // 对象比较处理（默认实现）
    private Boolean objectComparison(Object a, Object b) {
        switch (operator) {
            case EQUAL:    return a.equals(b);
            case NOTEQUAL: return !a.equals(b);
            default: throw new RuntimeException("Object comparison only supports == and != for type: "
                    + a.getClass().getSimpleName());
        }
    }

    // 判断是否为比较运算符
    private boolean isComparison(Symbol op) {
        return op == Symbol.EQUAL || op == Symbol.NOTEQUAL ||
                op == Symbol.LESS || op == Symbol.GREATER ||
                op == Symbol.LESS_EQUAL || op == Symbol.GREATER_EQUAL;
    }

}
