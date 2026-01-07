package jvlang;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 * @author Yumerain
 */
public class Lexer {

    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.symbol != Symbol.EOF);
        return tokens;
    }

    private Token nextToken() {
        skipWhitespace();
        if (isAtEnd()) return new Token(Symbol.EOF, line);

        start = current;
        char c = advance();

        // 数字字面量
        if (Character.isDigit(c)) return number();
        // 标识符or关键字
        if (Character.isLetter(c)) return identifier();
        // 字符串字面量
        if (c == '"') return string();
        // 处理单字符符号
        switch (c) {
            case '+': return new Token(Symbol.PLUS, line);
            case '-': return new Token(Symbol.MINUS, line);
            case '*': return new Token(Symbol.MULTIPLY, line);
            case '/': return new Token(Symbol.DIVIDE, line);
            case '%': return new Token(Symbol.MODULO, line);
            case '=': return checkNext('=') ? new Token(Symbol.EQUAL, line) : new Token(Symbol.ASSIGN, line);
            case '!': return checkNext('=') ? new Token(Symbol.NOTEQUAL, line) : new Token(Symbol.NOT, line);
            case '<': return checkNext('=') ? new Token(Symbol.LESS_EQUAL, line) : new Token(Symbol.LESS, line);
            case '>': return checkNext('=') ? new Token(Symbol.GREATER_EQUAL, line) : new Token(Symbol.GREATER, line);
            case '(': return new Token(Symbol.LPAREN, line);
            case ')': return new Token(Symbol.RPAREN, line);
            case '{': return new Token(Symbol.LBRACE, line);
            case '}': return new Token(Symbol.RBRACE, line);
            case ',': return new Token(Symbol.COMMA, line);
            case ';': return new Token(Symbol.SEMICOLON, line);
            case '.': return new Token(Symbol.DOT, line);
            case '&':
                if (checkNext('&')) {
                    return new Token(Symbol.AND, line);
                } else {
                    throw new JvsException("Single '&' is not supported at line " + line);
                }
            case '|':
                if (checkNext('|')) {
                    return new Token(Symbol.OR, line);
                } else {
                    throw new JvsException("Single '|' is not supported at line " + line);
                }
        }
        throw new JvsException("Unexpected character '" + c + "' at line " + line + " position " + current);
    }

    private Token number() {
        while (Character.isDigit(peek())) advance();

        // 检查是否有小数点
        if (peek() == '.') {
            // 检查小数点后必须跟数字
            if (!Character.isDigit(peekNext())) {
                throw new JvsException("Invalid float literal at line " + line);
            }
            advance(); // 吃掉小数点
            while (Character.isDigit(peek())) advance();
            return new Token(Symbol.NUMBER, Double.parseDouble(source.substring(start, current)), line);
        }
        return new Token(Symbol.NUMBER, Long.parseLong(source.substring(start, current)), line);
    }

    private Token identifier() {
        while (Character.isLetterOrDigit(peek()) || peek() == '_') advance();
        String text = source.substring(start, current);
        // 关键字
        Symbol keyword = Symbol.keyword(text);
        if (keyword != null) {
            if (keyword == Symbol.TRUE) {
                return new Token(Symbol.TRUE, true, line);
            } else if (keyword == Symbol.FALSE) {
                return new Token(Symbol.FALSE, false, line);
            } else {
                return new Token(keyword, null, line);
            }
        }
        // 自定义变量
        return new Token(Symbol.IDENTIFIER, text, line);
    }

    private Token string() {
        StringBuilder sb = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            char c = advance();
            if (c == '\\') {
                // 处理转义字符
                if (isAtEnd()) throw new JvsException("Unterminated escape sequence");
                char esc = advance();
                switch (esc) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: throw new JvsException("Invalid escape sequence: \\" + esc);
                }
            } else {
                // 检查未转义的换行符
                if (c == '\n') {
                    throw new JvsException("Unterminated string: newline not allowed without escape at line " + line);
                }
                sb.append(c);
            }
        }
        if (isAtEnd()) throw new JvsException("Unterminated string at line " + line);
        advance(); // 吃掉闭合引号
        return new Token(Symbol.TEXT, sb.toString(), line);
    }

    // 辅助方法
    private boolean isAtEnd() { return current >= source.length(); }
    private char advance() {
        char c = source.charAt(current++);
        if (c == '\n') line++;
        return c;
    }
    private char peek() { return isAtEnd() ? '\0' : source.charAt(current); }
    private char peekNext() { return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1); }
    private boolean checkNext(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                advance();
            } else if (c == '\n') {
                line++;
                advance();
            } else if (c == '/' && peekNext() == '/') { // 检测单行注释
                skipComment();
            } else {
                break;
            }
        }
    }

    private void skipComment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance(); // 跳过注释内容
        }
    }
}