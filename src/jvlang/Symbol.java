package jvlang;

import java.util.HashMap;
import java.util.Map;

/**
 * 关键字和标记符
 * @author Yumerain
 */
public enum Symbol {

    // 标识符
    IDENTIFIER("ID"),

    // 字面量
    NUMBER("NUM"),
    TEXT("TEXT"),

    // 关键字
    VAR("var"),
    FUNC("func"),
    CLASS("class"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    TRUE("true"),
    FALSE("false"),

    // 类型
    INT("int"),
    FLOAT("float"),
    BOOL("bool"),
    STRING("string"),

    // 赋值
    ASSIGN("="),

    // 算述运算符
    PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), MODULO("%"),

    // 逻辑运算符
    EQUAL("=="), NOTEQUAL("!="),
    LESS("<"), LESS_EQUAL("<="),
    GREATER(">"), GREATER_EQUAL(">="),
    AND("&&"), OR("||"), NOT("!"),

    // 分隔符
    LPAREN("("), RPAREN(")"),
    LBRACE("{"), RBRACE("}"),
    COMMA(","), SEMICOLON(";"),
    DOT("."),

    // 逻辑控制
    RETURN("return"),

    // 内部函数
    PRINT("print"),

    // 结束符
    EOF("EOF");

    private final String value;

    private Symbol(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Symbol keyword(String key) {
        return keywords.get(key);
    }

    // 关键字定义，方便快速取用
    private static final Map<String, Symbol> keywords = new HashMap<String, Symbol>() {{
        // 关键字
        put(VAR.value, VAR);
        put(FUNC.value, FUNC);
        put(CLASS.value, CLASS);
        put(IF.value, IF);
        put(ELSE.value, ELSE);
        put(WHILE.value, WHILE);
        put(TRUE.value, TRUE);
        put(FALSE.value, FALSE);
        put(RETURN.value, RETURN);
    }};

}
