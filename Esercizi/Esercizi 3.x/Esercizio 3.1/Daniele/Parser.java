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

    private void expr() {
        switch(look.tag){
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

    private void exprp() {
        switch (look.tag) {
            case '+':
                match(Token.plus.tag);
                term();
                exprp();
                break;
            case '-':
                match(Token.minus.tag);
                term();
                exprp();
                break;
            case Tag.EOF:    ////i due casi epsilon, quindi nessuna produzione rilevante. da vedere se necessario break dopo )
                break;
            case ')':
                break;
        }
    }

    private void term() {
        switch(look.tag){
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

    private void termp() {
        switch(look.tag){
            case '*':
                match(Token.mult.tag);
                fact();
                term();
                break;
            case '/':
                match(Token.div.tag);
                fact();
                term();
                break;
            case '+':
                break;
            case '-':
                break;
            case ')':
                break;
            case Tag.EOF:
                break;
        }
    }

    private void fact() {
        switch(look.tag){
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}