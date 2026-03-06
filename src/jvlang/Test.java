package jvlang;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        // 程序源代码
        String source = """
    println("=== 测试字符串 ===");
    var str = "Hello World";
    println(str);
    println("=== 测试算术运算 ===");
    println("9-2*3+1=", 9-2*3+1);
    println("=== 测试数字变量 ===");
    var a = 1;
    var b = 2;
    println("a=",a,",b=",b,",a+b=",a + b);
    println("=== 测试字符串变量 ===");
    var x = "xx";
    var y = "yy";
    println("str(xx)+str(yy)=", x+y);
    println("=== 测试条件语句 ===");
    println("a=", a, ",b=", b, ",a>b ?= ", a>b);
    println("a=", a, ",b=", b, ",a>=b ?= ", a>=b);
    println("a=", a, ",b=", b, ",a<b ?= ", a<b);
    println("a=", a, ",b=", b, ",a<=b ?= ", a<=b);
    println("a=", a, ",b=", b, ",a==b ?= ", a==b);
    println("=== 测试循环语句 ===");
    var c = 10;
    println("loop count=", c, ":");
    for c > 0 {
        print("it=", c, ",");
        c = c - 1;
    }
    println();
    println("=== 测试函数 ===");
    fun bye() {
        println("无参函数：bye bye~");
    }
    fun sayHello(msg) {
        println("有参函数：Hello World, " + msg);
    }
    fun fn2fn() {
        println("函数内调用函数");
        bye();
    }
    sayHello("haha");
    sayHello(x);
    sayHello(y);
    fn2fn();
    println("=== 测试有参有返回函数 ===");
    fun add(a, b) {
        return a + b;
    }
    var aa = 10;
    var bb = 90;
    println(aa + "+" + bb + "=" + add(aa,bb));
    println("=== 测试有参有返回函数，递归，斐波那契数列 ===");
    fun fibo(number) {
        if number == 0 || number == 1 {
            return number;
        } else {
            return fibo(number - 1) + fibo(number - 2);
        }
    }
    var d = 0;
    for d <= 10 {
        print(fibo(d) + "\t");
        d = d + 1;
    }
    println();
    
    println("=== 测试四则运算函数 ===");
    fun calc(a, b, opt) {
        print(a, opt, b, "=");
        if opt == "+" {
            return a + b;
        }
        if opt == "-" {
            return a - b;
        }
        if opt == "*" {
            return a * b;
        }
        if opt == "/" {
            return a / b;
        }
        if opt == "%" {
            return a % b;
        }
        return null;
    }
    println(calc(8, 2, "+"));
    println(calc(8, 2, "-"));
    println(calc(8, 2, "*"));
    println(calc(8, 2, "/"));
    println(calc(8, 5, "%"));
    
    println("=== 测试定义九九乘法表 ===");
    fun ninenine()
    {
        var a = 1;
        for(a <= 9) {
            var b = 1;
            for(b <= a) {
                print(b, "*", a, "=", a*b, "\\t");
                b = b + 1;
            }
            a = a + 1;
            println();
        }
    }
    println("=== 测试输出九九乘法表 ===");
    ninenine();

    println("=== 测试类定义与实例化 ===");
    class Point {
        var x = 0;
        var y = 0;
    }
    var p = Point(x = 10, y = 20);
    println("Point created: x=", p.x, ", y=", p.y);
    
    println("=== 测试字段访问 ===");
    println("p.x = ", p.x);
    println("p.y = ", p.y);
    
    println("=== 测试字段赋值 ===");
    p.x = 100;
    p.y = 200;
    println("After assignment: p.x = ", p.x, ", p.y = ", p.y);
    
    println("=== 测试this关键字和方法 ===");
    class Counter {
        var count = 0;
    
        fun increment() {
            this.count = this.count + 1;
        }
    
        fun getCount() {
            return this.count;
        }
    }
    
    var counter = Counter();
    println("Initial count: ", counter.count);
    counter.increment();
    println("After increment: ", counter.count);
    counter.increment();
    println("After second increment: ", counter.count);
    
    println("=== 测试方法返回值 ===");
    var result = counter.getCount();
    println("getCount() returned: ", result);
    
    println("=== 测试带参数的方法 ===");
    class Calculator {
        var value = 0;
    
        fun add(num) {
            this.value = this.value + num;
        }
    
        fun multiply(num) {
            this.value = this.value * num;
        }
    }
    
    var calc = Calculator(value = 10);
    println("Initial value: ", calc.value);
    calc.add(5);
    println("After add(5): ", calc.value);
    calc.multiply(2);
    println("After multiply(2): ", calc.value);
    
    println("=== 所有测试完成 ===");
""";
        //System.out.println(source);
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Program program = parser.parse();

        Scope root = new Scope();
        program.exec(root);
    }
}