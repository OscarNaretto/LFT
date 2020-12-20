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

    public void prog() {
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                statlist();
                match(Tag.EOF);
                break;
        }
    }

    private void statlist() {
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat();
                statlistp();
                break;
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;
            case Tag.EOF:
                break;
        }
    }

    private void stat() {
        switch(look.tag){
            case '=':
                match(Token.assign.tag);
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case Tag.READ:
                match(Tag.READ);
                match(Token.lpt.tag);
                match(Tag.ID);
                match(Token.rpt.tag);
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                break;
            case '{':
                match(Token.lpg.tag);
                statlist();
                match(Token.rpg.tag);
                break;
        }
    }

    private void whenlist() {
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;
        }
    }

    private void whenlistp() {
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;
            case Tag.ELSE: break;
        }
    }
    

    private void whenitem() {
        switch(look.tag){
            case Tag.WHEN:
                match(Tag.WHEN);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                match(Tag.DO);
                stat();
                break;
        }
    }

    private void bexpr() {
        switch(look.tag){
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
        }
    }

    private void expr() {
        switch(look.tag){
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case '-':
                match(Token.minus.tag);
                expr();
                expr();
                break;
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
        }
    }

    private void exprlist() {
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
        }
    }

    private void exprlistp() {
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            case ')':
                break;
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}