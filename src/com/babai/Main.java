// ТЕСТОВОЕ ЗАДАНИЕ Задача: "Калькулятор" для поступления на проект Java Mentor

package com.babai;

import java.util.Scanner;

public class Main
{

    static String input(){                          //Ввод формулы и удаление пробелов
        Scanner in = new Scanner(System.in);
        System.out.print("Введите формулу: ");
        return in.nextLine().replaceAll(" ","");
    }

    public static void main(String[] args)
    {

        // Запись формулы в объект
        Formula myform = new Formula(input());
        myform.fillin();
        // Проверка цифр
        if(!myform.arab) {       // Если римские цифры
            NeArab neaa = new NeArab (myform.a); // инициализация проверки арабских цифр
            NeArab neab = new NeArab (myform.b);
            myform.aint = neaa.num;
            myform.bint = neab.num;
        }
        else {           // Если арабские цифры
            ArabCheck ara = new ArabCheck (myform.a,myform.b);  // инициализация проверки на условие 1-10
            myform.aint = ara.a;
            myform.bint = ara.b;
        }
// Вычисление
        Calc sum = new Calc (myform.aint,myform.operation,myform.bint);  // инициализация калькулятор
        sum.go(); // Вычисление суммы

        //Печать результата
        System.out.print("Результат");
        if (myform.operation.equals("/")) { System.out.print(" с учетом классического округления до целого");}
        if (myform.operation.equals("-") && sum.res <0 && myform.arab) { System.out.print(" (считаем, что арабские числа могут быть отрицательными)");}
        System.out.print(": ");
        if (myform.arab) {
            System.out.print(sum.res);
        }
        else {
            ConvertArab sumara = new ConvertArab (sum.res);
            System.out.print(sumara.resarab);
        }
    }
}


// Класс для формулы
class Formula{
    String stroka; // Формула строка без пробелов
    String a;  // первый операнд строка
    int aint;  // первый операнд число
    String b;  // второй операнд строка
    int bint;   // второй операнд число
    String operation;  // Знак операции
    boolean arab;  // арабская или римская формула

    Formula (String s){
        stroka = s;
    }
    void fillin(){
        int pos = 100000; // позиция операции в строке
        if (stroka.indexOf('+') > 0 && stroka.indexOf('+') < pos) { operation = "+"; pos = stroka.indexOf('+');}
        if (stroka.indexOf('-') > 0 && stroka.indexOf('-') < pos) { operation = "-"; pos = stroka.indexOf('-');}
        if (stroka.indexOf('*') > 0 && stroka.indexOf('*') < pos) { operation = "*"; pos = stroka.indexOf('*');}
        if (stroka.indexOf('/') > 0 && stroka.indexOf('/') < pos) { operation = "/"; pos = stroka.indexOf('/');}

        try{
            if (pos == 100000) throw new StringIndexOutOfBoundsException("Ошибка: Не была указана операция +-*/");
            a = stroka.substring(0,pos);
            b = stroka.substring(pos+1); // здесь если остается 100000 то вылетает
        }   catch (StringIndexOutOfBoundsException e) {
            System.out.println(e); // вывести исключение
            System.exit(0); //Terminates jvm;
        }
        try {
            Integer.parseInt(a);   // проверка цифра или нет
            arab = true;
        } catch (NumberFormatException e) {
            arab = false;        // если не цифра значит считаем что римская
        }
    }
}

class Calc{       // Вычисление результата
    double a;    // дабл чтобы сделать округление
    double b;    // дабл чтобы сделать округление
    String oper;
    int res;
    Calc (int x, String o, int y){
        a = x;
        b = y;
        oper = o;
    }
    void go(){
        switch (oper) {
            case "+": res=(int)(a+b); break;
            case "-": res=(int)(a-b); break;
            case "*": res=(int)(a*b); break;
            case "/": res=(int)(a/b+0.5); break; // классическое округление
            default: System.out.println("Случилось невероятное!");
        }
    }

}

class ArabCheck{         // проверяет обе ли арабские цифры в диапазоне 1-10
    int a;
    int b;
    ArabCheck(String x, String y){
        try{a = Integer.parseInt(x);
            b = Integer.parseInt(y);
        }catch (NumberFormatException nfe){
            System.out.println("Ошибка ArabCheck1: Неправильно ввели числа. Надо либо два арабских от 1 до 10, либо два римских от I до X");
            System.out.println("NumberFormatException: " + nfe.getMessage());
            System.exit(0); //Terminates jvm;
        }
        if (a<1 || a>10 || b<1 || b>10) { System.out.println("Ошибка ArabCheck2: Неправильно ввели числа. Надо либо два арабских от 1 до 10, либо два римских от I до X"); System.exit(0);} //Terminates jvm;
    }
}

class NeArab{       // Проверяет являются ли символы арабскими цифрами от I-X
    int num;
    NeArab(String n){
        switch(n){
            case "I": num = 1; break;
            case "II": num = 2; break;
            case "III": num = 3; break;
            case "IV": num = 4; break;
            case "V": num = 5; break;
            case "VI": num = 6; break;
            case "VII": num = 7; break;
            case "VIII": num = 8; break;
            case "IX": num = 9; break;
            case "X": num = 10; break;
            default: System.out.println("Ошибка NeArab: Неправильно ввели числа. Надо либо два арабских от 1 до 10, либо два римских от I до X"); System.exit(0); //Terminates jvm;
        }
    }
}

class ConvertArab{   // Конвертор араб в рим
    String resarab = "NIHIL (получилось меньше одного - таких римских цифр не бывает)";
    ConvertArab(int res){
        int[] ara = new int[] {100,90,50,40,10,9,5,4,1};
        String[] rim = new String[] {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int i = 0;
        StringBuilder sb = new StringBuilder();

        while (res > 0){
            if (ara[i] <= res)  {    // i - элемент массива арабских цифр меньше либо равен числу res то
                res -=  ara[i];   // от числа вычитаем его эквивалент в массиве арабском
                sb.append(rim[i]);  }     // в строку записываем его римское значение
            else {i++;}
        }
        if(sb.length() >0) {resarab = sb.toString();}
    }
}