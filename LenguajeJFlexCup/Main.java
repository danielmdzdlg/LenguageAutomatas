package LenguajeJFlexCup;

import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws Exception {
        String input = "alto x = 5 + 3 * 2";
        parser p = new parser(new Lexer(new StringReader(input)));
        p.parse();
        System.out.println("Análisis correcto");
    }
}