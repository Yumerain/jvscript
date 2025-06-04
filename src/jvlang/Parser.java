package jvlang;

import jvlang.expr.BinaryExpr;
import jvlang.expr.Expression;
import jvlang.expr.FuncCall;
import jvlang.expr.Literal;
import jvlang.expr.ClassExpr;
import jvlang.expr.UnaryExpr;
import jvlang.expr.Variable;
import jvlang.model.FieldDeclaration;
import jvlang.stmt.Assignment;
import jvlang.stmt.ExprStatement;
import jvlang.stmt.FuncDefinition;
import jvlang.stmt.IfStatement;
import jvlang.stmt.ReturnStatement;
import jvlang.stmt.Statement;
import jvlang.stmt.ClassDefinition;
import jvlang.stmt.VarDeclaration;
import jvlang.stmt.WhileLoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 递归下降自顶向下的语法分析
 * @author Yumerain
 */
public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        if (isAtEnd()) return tokens.get(tokens.size() - 1); // 返回最后一个 Token（通常是 EOF）
        return tokens.get(current);
    }

    // 查看下一个 Token（不移动 current 指针）
    private Token peekNext() {
        int nextIndex = current + 1;
        if (nextIndex >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // 返回 EOF Token
        }
        return tokens.get(nextIndex);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        // 不能调用peek().symbol == Symbol.EOF，否则死循环
        return current >= tokens.size();
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean check(Symbol symbol) {
        return !isAtEnd() && peek().symbol == symbol;
    }

    private Token consume(Symbol symbol, String message) {
        if (check(symbol)) return advance();
        throw new RuntimeException(message + " at line " + peek().line);
    }

    private boolean match(Symbol symbol) {
        if (!check(symbol)) return false;
        advance();
        return true;
    }

    // 解析整个程序BNF
    // <program> ::= <statement-list>
    // 最终表达式解析流程图
    // expression()
    //  -> logicalOr()
    //    -> logicalAnd()
    //      -> equality()
    //        -> comparison()
    //          -> arithAddSub()
    //            -> arithMulDiv()
    //              -> factor()
    //                -> 处理字面量/变量/括号/一元运算符/函数调用
    public Program parse() {
        return new Program(statementList());
    }

    // 解析语句列表：用循环迭代代替了递归调用，效果与bnf一样，更高效且避免递归栈溢出
    // BNF: <statement-list> ::= <statement> <statement-list> | ε
    private List<Statement> statementList() {
        List<Statement> statements = new ArrayList<>();
        // 对应递归终止条件 ε (当遇到"}"或EOF)
        while (!isAtEnd() && peek().symbol != Symbol.RBRACE) {
            Statement stmt = statement();
            if (stmt != null) {
                statements.add(stmt);
                // 通过循环实现，隐式对应 <statement-list> 递归调用
            }
        }
        return statements;
    }

    // 使用严格递归实现，直接对应BNF但效率较低
    // BNF: <statement-list> ::= <statement> <statement-list> | ε
    //private List<Statement> statementList() {
    //    if (isAtEnd() || peek().symbol == Symbol.RBRACE) {
    //        return Collections.emptyList(); // ε
    //    }
    //    Statement stmt = statement();
    //    List<Statement> res = statementList(); // 递归调用
    //    List<Statement> result = new ArrayList<>();
    //    result.add(stmt);
    //    result.addAll(res);
    //    return result;
    //}



    // 解析单个语句
    private Statement statement() {
        // 遇到EOF时返回null，由上层处理
        if (match(Symbol.EOF)) {
            return null;
        }

        // 由起始关键字识别的语句
        if (match(Symbol.VAR)) return varDeclaration();         // 变量声明
        if (match(Symbol.IF)) return ifCondition();             // if语句
        if (match(Symbol.WHILE)) return whileLoop();            // while循环
        if (match(Symbol.FUNC)) return funcDefinition();        // 函数定义
        if (match(Symbol.CLASS)) return classDefinition();      // 类定义
        if (match(Symbol.RETURN)) return returnStatement();     // 函数返回值

        // 赋值或表达式语句
        if (checkIdentifier()) {
            // 1. 确保当前Token是标识符（由外部 checkIdentifier() 保证）
            Token identifierToken = consume(Symbol.IDENTIFIER, "Internal error: expected identifier");

            // 2. 查看下一个Token是否是等号（ASSIGN）
            if (check(Symbol.ASSIGN)) {
                // --- 情况1：解析赋值语句 ---
                advance(); // 消费等号
                Expression value = expression(); // 解析右侧表达式
                consume(Symbol.SEMICOLON, "Expect ';' after assignment statement");
                return new Assignment(new Variable(identifierToken.value.toString()), value);
            } else {
                // --- 情况2：回退并解析表达式语句 ---
                // 回退到标识符的起始位置（因为已经消费了标识符Token）
                current--;
                // 解析完整的表达式（可能包含更复杂的结构，如函数调用）
                Expression expr = expression();
                // 强制要求分号结尾
                consume(Symbol.SEMICOLON, "Expect ';' after expression statement");
                return new ExprStatement(expr);
            }
        }

        throw new RuntimeException("Unexpected token: " + peek().symbol + " at line " + peek().line);
    }

    // 辅助方法：检查当前Token是否为标识符
    private boolean checkIdentifier() {
        return peek().symbol == Symbol.IDENTIFIER;
    }

    private Statement returnStatement() {
        Expression expr = null;
        if (!check(Symbol.SEMICOLON)) {
            expr = expression();
        }
        consume(Symbol.SEMICOLON, "Expect ';' after return");
        return new ReturnStatement(expr);
    }

    // 解析变量声明语句 <var-declaration> ::= "var" <identifier> [ "=" <expression> ] ";"
    private VarDeclaration varDeclaration() {
        // 1.无需消费var，因为match(Symbol.VAR)已处理
        // 2. 解析标识符 <identifier>
        Token name = consume(Symbol.IDENTIFIER, "Expect variable name after 'var'");
        // 3. 解析可选的初始化表达式 [ "=" <expression> ]
        Expression initializer = null;
        if (match(Symbol.ASSIGN)) {
            initializer = expression();
        }
        // 4. 匹配结尾分号
        consume(Symbol.SEMICOLON, "Expect ';' after variable declaration");
        return new VarDeclaration(name.value.toString(), initializer);
    }

    // 解析表达式 <expression> -> <logical-or>
    private Expression expression() {
        return logicalOr(); // 从最低优先级开始
    }

    // 解析逻辑或 <logical-or>  ::= <logical-and> ( "||" <logical-or> )?
    private Expression logicalOr() {
        Expression expr = logicalAnd(); // 更高优先级
        while (match(Symbol.OR)) {
            Expression right = logicalAnd();
            expr = new BinaryExpr(expr, Symbol.OR, right);
        }
        return expr;
    }

    // 解析逻辑与 <logical-and> ::= <equality> ( "&&" <logical-and> )?
    // 左结合性：通过while循环处理连续的&&（如 a && b && c 解析为 (a && (b && c))）
    // 优先级控制：通过调用 equality() 保证 && 的优先级低于 ||（由 logicalOr 处理），但高于 ==/!=
    private Expression logicalAnd() {
        // 解析更高优先级的 equality 表达式
        Expression expr = equality();
        // 循环处理所有连续的 "&&" 运算符
        while (match(Symbol.AND)) {
            Expression right = equality(); // 注意：这里调用 equality()，不是递归调用 logicalAnd()
            expr = new BinaryExpr(expr, Symbol.AND, right);
        }
        return expr;
    }

    // 相等判断
    // 支持多个相等性比较：例如 a == b != c（虽然这种写法不常见，但语法允许）
    // 严格优先级：==/!= 优先级低于 </> 等比较运算符，所以调用 comparison()
    // <equality> ::= <comparison> ( ("==" | "!=") <comparison> )*
    private Expression equality() {
        Expression expr = comparison(); // 解析更高优先级的 comparison
        // 处理所有连续的 == 或 !=
        while (true) {
            if (match(Symbol.EQUAL)) {
                Expression right = comparison();
                expr = new BinaryExpr(expr, Symbol.EQUAL, right);
            } else if (match(Symbol.NOTEQUAL)) {
                Expression right = comparison();
                expr = new BinaryExpr(expr, Symbol.NOTEQUAL, right);
            } else {
                break;
            }
        }
        return expr;
    }

    // 大小比较判断 <comparison> ::= <add-sub> ( ("<" | ">" | "<=" | ">=") <add-sub> )*
    private Expression comparison() {
        Expression expr = arithAddSub(); // 解析更高优先级的加减表达式
        while (true) {
            if (match(Symbol.LESS)) {
                expr = new BinaryExpr(expr, Symbol.LESS, arithAddSub());
            } else if (match(Symbol.GREATER)) {
                expr = new BinaryExpr(expr, Symbol.GREATER, arithAddSub());
            } else if (match(Symbol.LESS_EQUAL)) {
                expr = new BinaryExpr(expr, Symbol.LESS_EQUAL, arithAddSub());
            } else if (match(Symbol.GREATER_EQUAL)) {
                expr = new BinaryExpr(expr, Symbol.GREATER_EQUAL, arithAddSub());
            } else {
                break;
            }
        }
        return expr;
    }

    // 数学加减运算 <add-sub> ::= <mul-div> ( ("+" | "-") <mul-div> )*
    private Expression arithAddSub() {
        Expression expr = arithMulDiv(); // 解析更高优先级的乘除模
        while (true) {
            if (match(Symbol.PLUS)) {
                expr = new BinaryExpr(expr, Symbol.PLUS, arithMulDiv());
            } else if (match(Symbol.MINUS)) {
                expr = new BinaryExpr(expr, Symbol.MINUS, arithMulDiv());
            } else {
                break;
            }
        }
        return expr;
    }

    // 数学乘除取余运算 <mul-div> ::= <factor> ( ("*" | "/" | "%") <mul-div> )*
    private Expression arithMulDiv() {
        Expression expr = factor(); // 解析基础因子
        while (true) {
            if (match(Symbol.MULTIPLY)) {
                expr = new BinaryExpr(expr, Symbol.MULTIPLY, factor());
            } else if (match(Symbol.DIVIDE)) {
                expr = new BinaryExpr(expr, Symbol.DIVIDE, factor());
            } else if (match(Symbol.MODULO)) {
                expr = new BinaryExpr(expr, Symbol.MODULO, factor());
            } else {
                break;
            }
        }
        return expr;
    }

    // 解析因子 <factor> ::= <literal> | <identifier> | <unary-expression> | "(" <expression> ")" | <function-call>
    private Expression factor() {
        // 一元运算符 ! 或 -
        if (match(Symbol.MINUS) || match(Symbol.NOT)) {
            Symbol op = previous().symbol;
            Expression right = factor(); // 递归解析右侧表达式，右结合性：!!x 解析为 !(!x)
            return new UnaryExpr(op, right);
        }
        // 括号表达式
        if (match(Symbol.LPAREN)) { // 括号表达式
            Expression expr = expression();
            consume(Symbol.RPAREN, "Expect ')' after expression");
            return expr; // 直接返回内部表达式，不需要特殊节点
        }
        // 函数调用：标识符后紧跟 (
        if (peek().symbol == Symbol.IDENTIFIER && peekNext().symbol == Symbol.LPAREN) {
            return funcCall();
        }
        // 字面量或变量
        Token token = advance();
        switch (token.symbol) {
            case NUMBER:
            case TEXT:
            case TRUE:
            case FALSE:
                return new Literal(token.value);
            case IDENTIFIER:
                return new Variable(token.value.toString());
            default:
                throw new RuntimeException("Unexpected token: " + peek().symbol + " at line " + peek().line);
        }
    }

    // 函数调用
    private FuncCall funcCall() {
        // 函数名
        Token nameToken = consume(Symbol.IDENTIFIER, "Expect function name");
        // 起始(
        consume(Symbol.LPAREN, "Expect '(' after function name");
        // 参数
        List<Expression> args = argumentList();
        // 结束)
        consume(Symbol.RPAREN, "Expect ')' after arguments");
        return new FuncCall(nameToken.value.toString(), args);
    }

    // 解析参数列表
    private List<Expression> argumentList() {
        List<Expression> args = new ArrayList<>();
        if (!check(Symbol.RPAREN)) { // 如果有参数
            do {
                args.add(expression()); // 解析每个参数表达式
            } while (match(Symbol.COMMA)); // 逗号分隔
        }
        return args;
    }

    // 根据值推断类型
    private Symbol inferType(Object value) {
        if (value instanceof Long) return Symbol.INT;
        if (value instanceof Double) return Symbol.FLOAT;
        if (value instanceof Boolean) return Symbol.BOOL;
        if (value instanceof String) return Symbol.STRING;
        throw new RuntimeException("Cannot infer type for value: " + value);
    }

    /**
     * 条件语句
     * <if-statement> ::= "if" <expression> "{" <statement-list> "}" [ "else" "{" <statement-list> "}" ]
     */
    private Statement ifCondition() {
        // 1. 匹配 "if"
        //consume(Symbol.IF, "Expect 'if' keyword");
        // 2. 解析条件表达式 <expression>
        Expression condition = expression();
        // 3. 匹配 "{" 并解析语句块 <statement-list>
        consume(Symbol.LBRACE, "Expect '{' after if condition");
        List<Statement> thenBranch = statementList();
        consume(Symbol.RBRACE, "Expect '}' after if block");
        // 4. 解析可选的 else 分支
        // [ "else" ... ] → if (match(Symbol.ELSE)) { ... }
        List<Statement> elseBranch = null;
        if (match(Symbol.ELSE)) {
            consume(Symbol.LBRACE, "Expect '{' after else");
            elseBranch = statementList();
            consume(Symbol.RBRACE, "Expect '}' after else block");
        }
        // 5. 构建AST节点
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    /**
     * 循环语句
     * <while-loop> ::= "while" <expression> "{" <statement-list> "}"
     */
    private Statement whileLoop() {
        //consume(Symbol.WHILE, "Expect 'while' keyword"); // 消费 while 关键字
        Expression condition = expression();     // 解析条件表达式

        consume(Symbol.LBRACE, "Expect '{' after while condition"); // 消费 {
        List<Statement> body = statementList();  // 解析循环体语句列表
        consume(Symbol.RBRACE, "Expect '}' after while block"); // 消费 }
        return new WhileLoop(condition, body);
    }

    // 函数定义
    // 函数体：<function-definition> ::= "func" <identifier> "(" <parameter-list> ")" "{" <statement-list> "}"
    // 函数参数列表：<parameter-list> ::= <identifier> ("," <identifier>)* | ε
    private Statement funcDefinition() {
        // consume(Symbol.FUNC, "Expect 'func' keyword"); // 消费 func
        // 解析函数名
        Token nameToken = consume(Symbol.IDENTIFIER, "Expect function name after 'func'");
        String functionName = nameToken.value.toString();
        // 解析参数列表
        consume(Symbol.LPAREN, "Expect '(' after function name");
        List<String> parameters = defineParameterList();
        consume(Symbol.RPAREN, "Expect ')' after parameters");
        // 解析函数体
        consume(Symbol.LBRACE, "Expect '{' before function body");
        List<Statement> body = statementList();
        consume(Symbol.RBRACE, "Expect '}' after function body");

        return new FuncDefinition(functionName, parameters, body);
    }

    // 参数列表解析辅助方法
    private List<String> defineParameterList() {
        List<String> params = new ArrayList<>();

        // 处理空参数列表的情况
        if (check(Symbol.RPAREN)) return params;

        do {
            Token paramToken = consume(Symbol.IDENTIFIER, "Expect parameter name");
            params.add(paramToken.value.toString());
        } while (match(Symbol.COMMA));

        return params;
    }

    // 结构体定义
    private ClassDefinition classDefinition() {
        // 1. 消费 class 关键字
        // consume(Symbol.CLASS, "Expect 'class' keyword");
        // 2. 解析结构体名称
        Token nameToken = consume(Symbol.IDENTIFIER, "Expect class name");
        String className = nameToken.value.toString();
        // 3. 解析字段列表
        consume(Symbol.LBRACE, "Expect '{' after class name");
        List<FieldDeclaration> fields = defineFieldList();
        consume(Symbol.RBRACE, "Expect '}' after class body");

        return new ClassDefinition(className, fields);
    }

    private List<FieldDeclaration> defineFieldList() {
        List<FieldDeclaration> fields = new ArrayList<>();

        while (!check(Symbol.RBRACE) && !isAtEnd()) {
            // 1. 必须包含 var 关键字
            consume(Symbol.VAR, "Field declaration must start with 'var'");

            // 2. 解析字段名
            Token fieldToken = consume(Symbol.IDENTIFIER, "Expect field name");
            String fieldName = fieldToken.value.toString();

            // 3. 解析可选初始化表达式
            Expression initializer = null;
            if (match(Symbol.ASSIGN)) {
                initializer = expression();
            }

            // 4. 强制分号结尾
            consume(Symbol.SEMICOLON, "Expect ';' after field declaration");

            // 加入定义的字段集合
            fields.add(new FieldDeclaration(fieldName, initializer));
        }

        return fields;
    }

    // 结构体实例化
    private Expression classInstantiation() {
        Token nameToken = consume(Symbol.IDENTIFIER, "Expect class name");
        String className = nameToken.value.toString();

        consume(Symbol.LBRACE, "Expect '{' after class name");
        Map<String, Expression> initializers = new HashMap<>();

        if (!check(Symbol.RBRACE)) {
            do {
                Token fieldToken = consume(Symbol.IDENTIFIER, "Expect field name");
                consume(Symbol.ASSIGN, "Expect '=' after field name");
                Expression initExpr = expression();
                initializers.put(fieldToken.value.toString(), initExpr);
            } while (match(Symbol.COMMA));
        }

        consume(Symbol.RBRACE, "Expect '}' after class initializers");
        return new ClassExpr(className, initializers);
    }

}