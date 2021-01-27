/*analisi sintattica per espressioni aritmetiche semplici,
scritte in notazione infissa.*/

import java.io.*;

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
        look = lex.lexical_scan(pbr);           // lettura del prossimo Token
        System.out.println("token = " + look);
    }

    void error(String s) {                      // procedura degli errori
	    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {                         // verifichiamo che il token sia uguale al parametro di questa procedura
	    if (look.tag == t) {
	        if (look.tag != Tag.EOF) move();    //passiamo al simbolo successivo in caso non fossero uguali
	    } else error("syntax error"); 
    }

    /*Calcoliamo gli isemeni guida cosi da capire 
    quale procedura attiva in base a ciò che legge il parser*/

    public void start() {
        switch (look.tag){
            case '(':                       // S--> E$ ---- Guida = (, NUM
            case Tag.NUM:
                expr();
                match(Tag.EOF);
                break;
            default:
                error("syntax error in grammar (start): token " + look + " can't be accepted");
                break;
        }
    }

    private void expr() {
        switch (look.tag){
            case '(':                      //E -- > TE' ---- Guida = (, NUM
            case Tag.NUM:
                term(); 
                exprp();
                break;
            default:
                error("syntax error in grammar (expr): token " + look + " can't be accepted");
                break;
        }
    }

    private void exprp() {
	    switch (look.tag) {
            case '+':                       // E--> +TE' ---- Guida = +
                match(Token.plus.tag);
                term();
                exprp();
                break;
            case '-':                       // E--> -TE'---- Guida = -
                match(Token.minus.tag);
                term();
                exprp();
                break;
            case ')': break;                // E --> ϵ  ---- Guida = EOF, )
            case Tag.EOF: break;
            default:
                error("syntax error in grammar (exprp): token " + look + " can't be accepted");
                break;
	    }
    }

    private void term() {
        switch (look.tag){
            case '(':                 //T-->FT'----- Guida = (, NUM
            case Tag.NUM:
                fact();
                termp();
                break;
            default:
                error("syntax error in grammar (term): token " + look + " can't be accepted");
                break;
        }
    }
    
    private void termp() {
        switch (look.tag){
            case '*':                  //T--> *FT' --- Guida = *
                match(Token.mult.tag);
                fact();
                termp();
                break;
            case '/':                  //T--> /FT' --- Guida = /
                match(Token.div.tag);
                fact();
                termp();
                break;
            case '+':                  //T--> ϵ     --- Guida = +,-,EOF               /*Nessuna produzione rilevante nei 4 casi*/
            case '-':
            case ')':
            case Tag.EOF: break;
            default:
                error("syntax error in grammar (termp): token " + look + " can't be accepted");
                break;
        }
    }

    private void fact() {
        switch (look.tag){
            case '(':                   //F-->(E) --- Guida = (
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
            case Tag.NUM:               //F-->NUM --- Guida = NUM
                match(Tag.NUM);
                break;
            default:
                error("syntax error in grammar (fact): token " + look + " can't be accepted");
                break;
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
