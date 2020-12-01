import java.io.*;
import java.text.BreakIterator;

import jdk.nashorn.internal.parser.Token;

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

    //P--> SL EOF --- Guida = =,print,read,cond,while,{
    public void prog(){
        switch (look.tag) {
            case '=':
                statlist();
                match(Tag.EOF);
                break;
            case Tag.PRINT:
                statlist();
                match(Tag.EOF);
                break; 
            case Tag.READ:
                statlist();
                match(Tag.EOF);
                 break;   
            case Tag.COND:
                statlist();
                match(Tag.EOF);
                break;
            case Tag.WHILE:
                statlist();
                match(Tag.EOF);
                break;
            case '{':
                statlist();
                match(Tag.EOF);
            break;
        }
    }

    //SL-->S ST --- Guida = =,print,read,cond,while,{
    private void statlist(){
        switch(look.tag){
            case '=':
                stat();
                statlistp();
                break;
            case Tag.PRINT:
                stat();;
                statlistp();
                break;
            case Tag.READ:
                stat();
                statlistp();
                break;
            case Tag.COND:
                stat();
                statlistp();
                break;
            case Tag.WHILE:
                stat();
                statlistp();
                break;
            case '{':
                stat();
                statlistp();
                break;
        }

    }

    //SL'--> ;S SL' --- Guida = ;
    //SL'--> 3 --- Guida = EOF
    private void statlistp(){
        switch(look.tag){
            case '=':
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;
            case Tag.EOF: break;
        }
    }

    //S--> ID(E)--- Guida = =
    //S--> print(EL) --- Guida = print
    //S--> read(ID) --- Guida = read
    //S--> cond(WL)else(S) --- Guida = cond
    //S--> while(B)S --- Guida = while
    //S--> {SL} --- Guida = {
    private void stat(){
        switch(look.tag){
            case '=':
                match(Tag.ID);
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
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
                match(Token.lpt.tag);
                whenlist();
                match(Tag.ELSE);
                match(Token.lpt.tag);
                stat();
                match(Token.rpt.tag);
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

    //WL --> WIWL' --- Guida = when
    private void whenlist(){
        switch(look.tag){
            case Tag.WHEN:
            whenlistp();
            whenlistp();
            break;
        }
    }

    //WL --> WIWL' --- Guida = when
    //WL --> 3 --- Guida = when
    private void whenlistp(){
        switch(look.tag){
            case Tag.WHEN:
            whenlistp();
            whenlistp();
            break;
        }
    }

    //WI --> when(B)doS --- Guida = when 
    private void whenitem(){
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

    //B -- > RELOP EE --- Guida = RELOP
    private void bexpr(){
        switch(look.tag){
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
        }

    }
    
    //E --> +(EL) --- Guida = +
    //E --> -EE --- Guida = -
    //E --> *(EL) --- Guida = *
    //E --> /EE --- Guida = /
    //E --> NUM --- Guida = NUM
    //E --> ID --- Guida = ID
    private void expr(){
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

    //EL -->  EEL' --- Guida = =,print,read,cond,while,{
    private void exprlist(){
        switch(look.tag){
        case '=':
            expr();
            exprlistp();
            break;
        case Tag.PRINT:
            expr();
            exprlistp();
            break;
        case Tag.READ:
            expr();
            exprlistp();
            break;
        case Tag.COND:
            expr();
            exprlistp();
            break;
        case Tag.WHILE:
            expr();
            exprlistp();
            break;
        case '{':
            expr();
            exprlistp();
            break;
        }

    }


    //EL' -->  EEL' --- Guida = =,print,read,cond,while,{
    //EL' --> 3 --- Guida = )
    private void exprlistp(){
        switch(look.tag){
            case '=':
                expr();
                exprlistp();
                break;
            case Tag.PRINT:
                expr();
                exprlistp();
                break;
            case Tag.READ:
                expr();
                exprlistp();
                break;
            case Tag.COND:
                expr();
                exprlistp();
                break;
            case Tag.WHILE:
                expr();
                exprlistp();
                break;
            case '{':
                expr();
                exprlistp();
                break;
            case ')': break;
            }

    
    }

		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}