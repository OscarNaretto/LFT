//Es 4.1
import java.io.*; 
/* Il Valutatore valuta le espressioni aritmetiche semplici utilizzando le SDT della sintassi.*/ 
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

    /*Calcoliamo gli insime  guida cosi da capire 
    quale procedura attiva in base a ciò che legge il valutatore*/

    /*Se X è una variabile, il metodo invoca la procedura X passando a X come
    argomenti i suoi attributi ereditati e raccogliendo in variabili locali
    gli attributi sintetizzati restituiti da X*/


    public void start() {                   // S --> E EOF { print (E.val) }
        int expr_val = 0;                   //inizializzo a zero il valore dell'attributo 
        
        switch (look.tag){
            case '(':                       // S--> E$ ---- Guida = (, NUM
            case Tag.NUM:
                expr_val = expr();          //assegnamo all'attributo il valore di E           
                match(Tag.EOF);
                System.out.println("Il risultato atteso è: " + expr_val); //dobbiamo stampare il valore di E.val,azione richiesta dalle regole semantica 
                break;
            default:
                error("Error in grammar (start) after read(): token " + look + " can't be accepted");
                break;
        }
    }

    private int expr() {                        // E-->T{E'.i= T.val}E'{E.val = E'.val}
        int term_val, exprp_val = 0;

        switch (look.tag){
            case '(':                           //E -- > TE' ---- Guida = (, NUM
            case Tag.NUM:
                term_val = term();              //assegnamo all'attributo il valore di T
                exprp_val = exprp(term_val);    //assegnamo all'attributo il valore ereditario di E'
                break;
            default:
                error("Error in grammar (expr) after read(): token " + look + " can't be accepted");
                break;
        }
        return exprp_val;
    }

    private int exprp(int exprp_i) {        /*E'--> +T{E'1.i = E'.i + T.val } E1'{ E'val = E'1.val }
                                                    -T{E'1.i = E'.i - T.val } E1'{ E'val = E'1.val }
                                                    ϵ {E'.val = E'.i}*/
        int term_val, exprp_val = 0;               

        switch (look.tag) {
            case '+':                       //E--> +TE' ---- Guida = +
                match('+');
                term_val = term();          //assegnamo all'attributo il valore di T
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-':                       //E--> -TE'---- Guida = -
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);      // assegnamo all'attributo il valore ereditario E'1.i
                break;
            case ')':                       //E --> ϵ  ---- Guida = EOF, )  Nessuna produzione rilevante nei 2 casi           
            case Tag.EOF: 
                exprp_val = exprp_i;        //il sintetizzato di E' prende quindi il valore dell'ereditato
                break;
            default:
                error("Error in grammar (exprp) after read(): token " + look + " can't be accepted");
                break;
        }
        return exprp_val;
    }

    private int term() {                    //T --> F {T.i = F.val} T'{T.val = T'.val}
        int fact_val, term_val = 0;
        
        switch (look.tag){
            case '(':                       //T-->FT'----- Guida = (, NUM
            case Tag.NUM:
                fact_val = fact();
                term_val = termp(fact_val); // assegnamo all'attributo il valore ereditario T'.i
                break;
            default:
                error("Error in grammar (term) after read(): token " + look + " can't be accepted");
                break;
        }
        return term_val;
    }
    
    private int termp(int termp_i) {        /* T'--> *F{T'1.i = T'.i * F.val } T1'{ T'val = T'1.val }
                                                      F{T'1.i = T'.i / F.val } T1'{ T'val = T'1.val }
                                                      ϵ {T'.val = T'.i}*/
        int fact_val, termp_val = 0;        

        switch (look.tag){
            case '*':                       //T--> *FT' --- Guida = *
                match(Token.mult.tag);
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);  // assegnamo all'attributo il valore ereditario T'1.i
                break;
            case '/':                                   //T--> /FT' --- Guida = /
                match(Token.div.tag);
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);  // assegnamo all'attributo il valore ereditario T'1.i
                break;
            case '+':                                   //T-->ϵ     --- Guida = +,-,EOF               
            case '-':                                   /*Nessuna produzione rilevante nei 4 casi, poiché va in epsilon*/ 
            case ')':
            case Tag.EOF:             
                termp_val = termp_i;                    //il sintetizzato di T' prende quindi il valore dell'ereditato
                break;
            default:
                error("Error in grammar (termp) after read(): token " + look + " can't be accepted");
                break;
        }
        return termp_val;
    }
    
    private int fact() {                                //F-->(E) {F.val = E.val} | NUM.value
        int fact_val = 0;

        switch (look.tag){
            case '(':                                   //F-->(E) --- Guida = (
                match(Token.lpt.tag);
                fact_val = expr();                      // assegnamo all'attributo il valore E
                match(Token.rpt.tag);
                break;
            case Tag.NUM:                               //F-->NUM --- Guida = NUM
                fact_val = ((NumberTok)look).number;    //faccio un downcast per la  conversione da Token a NumberTok, per poter usare l'attributo number
                match(Tag.NUM);
                break;
            default:
                error("Error in grammar (fact) after read(): token " + look + " can't be accepted");
                break;
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt";                      
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
