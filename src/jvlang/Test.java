package jvlang;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        // 程序源代码
        String source = """
                println("----：字符串");
                var str = "Hello World";
                println(str);
                println("----：算术运算");
                println("9-2*3+1=", 9-2*3+1);
                println("----：数字变量");
                var a = 1;
                var b = 2;
                println("a=",a,",b=",b,",a+b=",a + b);
                println("----：字符串变量");
                var x = "xx";
                var y = "yy";
                println("str(xx)+str(yy)=", x+y);
                println("----：条件语句");
                if a > b {
                    println("a>b");
                } else {
                    println("a<=b");
                }
                println("----：循环语句");
                var c = 10;
                while c > 0 {
                    print("loop c=", c, ",");
                    c = c - 1;
                }
                println();
                println("----：无参函数");
                func bye() {
                    print("bye bye~");
                }
                println("----：有参函数");
                func sayHello(msg) {
                    bye();
                    println("Hello World, " + msg);
                }
                sayHello("haha,");
                sayHello(x);
                sayHello(y);
                println("----：定义九九乘法表");
                func ninenine()
                {
                    var a = 1;
                    while(a <= 9 ) {
                        var b = 1;
                        while(b <= a) {
                            print(b, "*", a, "=", a*b, "\\t");
                            b = b + 1;
                        }
                        a = a + 1;
                        println();
                    }
                }
                println("----：输出九九乘法表第1次");
                ninenine();
                println("----：输出九九乘法表第2次");
                ninenine();
                
                println("----：类定义");
//                class Person {
//                    var age;
//                    var name;
//                }
//                func sum(a, b) {
//                    return a + b;
//                }
//                println("sum(4+5)=", sum(4, 5));
//                var ab = sum(2,8);
//                println("2+8=", ab);
//                var p = Person();
//                println("name=",p.name, "age=",p.age);
                """;

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        Scope root = new Scope();
        program.exec(root);
    }
}