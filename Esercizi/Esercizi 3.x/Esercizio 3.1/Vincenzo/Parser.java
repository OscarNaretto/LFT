import java.io.*;
import java.text.BreakIterator;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

     // S--> E$ ---- Guiga = (, NUM
    public void start() {
    switch(look.tag){
        case '(':
            expr();
            match(Tag.EOF);
            break;
        case Tag.NUM:
            expr();
            match(Tag.EOF);
            break;
     }
    }

    //E -- > TE' ---- Guiga = (, NUM
    private void expr() {
        switch (look.tag) {
            case '(':
                term();
                exprp();
                break;
            case Tag.NUM:
                term();
                exprp();
                break;
        }
	
    }

    // E--> +TE' ---- Guiga = +
    // E--> -TE'---- Guiga = -
    // E --> 3  ---- Guiga = EOF, )
    private void exprp() {
	switch (look.tag) {
        case '+':
            match(Token.plus.tag);
            term();
            expr();
            break;
        case '-':
            match(Token.minus.tag);
            term();
            expr();
            break;
            case ')': break;
            case Tag.EOF: break;
	    }
    }

    //T-->FT'----- Guida = (, NUM
    private void term() {
        switch (look.tag) {
            case '(':
              fact();
              term();  
                break;
            case Tag.NUM:
                fact();
                term(); 
                break;
        }
    }

    //T--> *FT' --- Guida = *
    //T--> /FT' --- Guida = /
    //T-->3     --- Guida = +,-,EOF
    private void termp() {
        switch(look.tag){
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
                break;
            case '/':
                match(Token.div.tag);
                fact();
                termp();
                break;
            case '+': break;
            case '-': break;
            case ')': break;
            case Tag.EOF: break;
        }
    }
     
    //F-->NUM --- Guida = NUM
    //F-->(E) ---Guida = (
    private void fact() {
        switch(look.tag){
            case Tag.NUM:
                match(Tag.NUM);
                 break;
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
        }


    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Testo.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}