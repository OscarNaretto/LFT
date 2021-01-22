import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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
        int expr_val = 0;
        
        switch (look.tag){
            case '(':
            case Tag.NUM:
                expr_val = expr();            
                match(Tag.EOF);
                System.out.println("Il risultato atteso è: " + expr_val);
                break;
            default:
                error("syntax error");
                break;
        }
    }

    private int expr() { 
        int term_val, exprp_val = 0;

        switch (look.tag){
            case '(':
            case Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                break;
            default:
                error("syntax error");
                break;
        }
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val = 0;

        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-': 
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case ')':              //i due casi epsilon, quindi nessuna produzione rilevante
            case Tag.EOF: 
                exprp_val = exprp_i;    //il sintetizzato di E' prende quindi il valore dell'ereditato
                break;
            default:
                error("syntax error");
                break;
        }
        return exprp_val;
    }

    private int term() { 
        int fact_val, term_val = 0;
        
        switch (look.tag){
            case '(':
            case Tag.NUM:
                fact_val = fact();
                term_val = termp(fact_val);
                break;
            default:
                error("syntax error");
                break;
        }
        return term_val;
    }
    
    private int termp(int termp_i) { 
        int fact_val, termp_val = 0;

        switch (look.tag){
            case '*':
                match(Token.mult.tag);
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match(Token.div.tag);
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;
            case '+':               //4 casi con produzione epsilon di nuovo
            case '-':
            case ')':
            case Tag.EOF:             
                termp_val = termp_i;
                break;
            default:
                error("syntax error");
                break;
        }
        return termp_val;
    }
    
    private int fact() { 
        int fact_val = 0;

        switch (look.tag){
            case '(':
                match(Token.lpt.tag);
                fact_val = expr();
                match(Token.rpt.tag);
                break;
            case Tag.NUM:
                fact_val = ((NumberTok)look).number;
                match(Tag.NUM);
                break;
            default:
                error("syntax error");
                break;
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
