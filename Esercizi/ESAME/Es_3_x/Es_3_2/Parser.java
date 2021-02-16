import java.io.*;
//Il Parser fa analisi sintattica per espressioni aritmetiche semplici, scritte in notazione infissa.
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

    public void prog() {         //P--> SL EOF --- Guida: =,print,read,cond,while,{
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
            default:
                error("syntax error in grammar (prog): token " + look + " can't be accepted");
                break;
        }
    }

    private void statlist() { //SL
        switch(look.tag){
            case '=':         //SL-->S SL' --- Guida: =,print,read,cond,while,{
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat();
                statlistp();
                break;
            default:
                error("syntax error in grammar (statlist): token " + look + " can't be accepted");
                break;
        }
    }
    
    private void statlistp() {  // SL'
        switch (look.tag) {
            case ';':           //SL'--> ;S SL' --- Guida: ;
                match(Token.semicolon.tag);
                stat();
                statlistp();
                break;
            case Tag.EOF:       //SL'--> ϵ --- Guida: EOF,}
            case '}':
                break;
            default:
                error("syntax error in grammar (statlistp): token " + look + " can't be accepted");
                break;
        }
    } 
    
    private void stat() {
        switch(look.tag){
            case '=':                       //S--> =ID(E)--- Guida: =
                match(Token.assign.tag);
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:                 //S--> print(EL) --- Guida: print
                match(Tag.PRINT);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case Tag.READ:                  //S--> read(ID) --- Guida: read
                match(Tag.READ);
                match(Token.lpt.tag);
                match(Tag.ID);
                match(Token.rpt.tag);
                break;
            case Tag.COND:                  //S--> cond WL else S  --- Guida: cond
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:                 //S--> while(B)S --- Guida: while
                match(Tag.WHILE);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                stat();
                break;
            case '{':                       //S--> {SL} --- Guida: {
                match(Token.lpg.tag);
                statlist();
                match(Token.rpg.tag);
                break;
            default:
                error("syntax error in grammar (stat): token " + look + " can't be accepted");
                break;
        }
    }

    private void whenlist() {      //WL
        switch(look.tag){
            case Tag.WHEN:         //WL --> WI WL' --- Guida: when
                whenitem();
                whenlistp();
                break;
            default:
                error("syntax error in grammar (whenlist): token " + look + " can't be accepted");
                break;
        }
    }
    
    private void whenlistp() { //WL'
        switch(look.tag){
            case Tag.WHEN:     //WL' --> WI WL' --- Guida: when
                whenitem();
                whenlistp();
                break;
            case Tag.ELSE:     //WL --> ϵ --- Guida: else
                break;
            default:
                error("syntax error in grammar (whenlistp): token " + look + " can't be accepted");
                break;
        }
    }  

    private void whenitem() { //WI
        switch(look.tag){
            case Tag.WHEN:    //WI --> when(B)doS --- Guida: when 
                match(Tag.WHEN);
                match(Token.lpt.tag);
                bexpr();
                match(Token.rpt.tag);
                match(Tag.DO);
                stat();
                break;
            default:
                error("syntax error in grammar (whenitem): token " + look + " can't be accepted");
                break;
        }
    }

    private void bexpr() {          //B
        switch(look.tag){           //B -- > RELOP E E --- Guida: RELOP
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("syntax error in grammar (bexpr): token " + look + " can't be accepted");
                break;
        }
    }
    
    private void expr() {       //E
        switch(look.tag){
            case '+':           //E --> +(EL) --- Guida: +
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case '-':           //E --> -EE --- Guida: -
                match(Token.minus.tag);
                expr();
                expr();
                break;
            case '*':           //E --> *(EL) --- Guida: *
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                match(Token.rpt.tag);
                break;
            case '/':           //E --> /EE --- Guida: /
                match(Token.div.tag);
                expr();
                expr();
                break;
            case Tag.NUM:       //E --> NUM --- Guida: NUM
                match(Tag.NUM);
                break;
            case Tag.ID:        //E --> ID --- Guida: ID
                match(Tag.ID);
                break;
            default:
                error("syntax error in grammar (expr): token " + look + " can't be accepted");
                break;
        }
    }
   
    private void exprlist() {       //EL
        switch(look.tag){
            case '+':               //EL -->  E EL' --- Guida: +,-,*,/,NUM,ID
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            default:
                error("syntax error in grammar (exprlist): token " + look + " can't be accepted");
                break;
        }
    }

    private void exprlistp() { //EL'
        switch(look.tag){
            case '+':          //EL' -->  E EL' --- Guida: +,-,*,/,NUM,ID
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            case ')':          //EL' --> ϵ --- Guida: )
                break;
            default:
                error("syntax error in grammar (exprlistp): token " + look + " can't be accepted");
                break;
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}