# JvScript

## 脚本语言，基于java

### 依赖
为方便测试，支持多行字符串的jdk即可，语法上仅用到java8

### 启动项目
Test.java功能一览

Terminal.java一个简易控制台，输入>切换到单行查模式(回车立即执行)，输入>>切换到多行模式(输出<<回车即执行多行脚本)。

复合数据结构class正在实现中，等有空时再继续。

### 语法定义见「bnf.txt」

### 语法示例(内置函数print和println)
```
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
func nineXnine()
{
    var a = 1;
    while(a <= 9 ) {
        var b = 1;
        while(b <= a) {
            print(b, "*", a, "=", a*b, "\t");
            b = b + 1;
        }
        a = a + 1;
        println();
    }
}
println("----：输出九九乘法表第1次");
nineXnine();
println("----：输出九九乘法表第2次");
nineXnine();
```