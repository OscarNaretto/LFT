import java.io.*; 

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        // ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;
            
            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;
            
            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                peek = ' ';
                return Token.div;

            case ';':
                peek = ' ';
                return Token.semicolon;

// ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

                case '|':
                    readch(br);
                    if(peek == '|'){
                        peek = ' ';
                        return Word.or;
                    }else{
                        System.err.println("Erroneous character"
                            + " after | : "  + peek );
                        return null;                        
                    }

                case '<':
                    readch(br);
                    if(peek == '='){
                        peek = ' ';
                        return Word.le;
                    }else if(peek == '>'){
                        peek = ' ';
                        return Word.ne;
                    }else{
                        peek = ' ';
                        return Word.lt;
                    }

                case '>':
                    readch(br);
                    if(peek == '='){
                        peek = ' ';
                        return Word.ge;
                    }else{
                        peek = ' ';
                        return Word.gt;
                    }
                
                case '=':
                    readch(br);
                    if(peek == '='){
                        peek = ' ';
                        return Word.eq;
                    }else{
                        peek = ' ';
                        return Token.assign;
                    }
                    
// ... gestire il caso degli identificatori e delle parole chiave //
          
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {
                    String identificatore = "";

                    while(Character.isLetter(peek)){
                        identificatore = identificatore + peek;
                        readch(br);
                    }

                    switch(identificatore){
                        case "cond":
                            identificatore = "";
                        return Word.cond;
                    
                        case "when":
                            identificatore = "";
                        return Word.when;

                        case "then":
                            identificatore = "";
                        return Word.then;

                        case "else":
                            identificatore = "";
                        return Word.elsetok;

                        case "while":
                            identificatore = "";
                        return Word.whiletok;

                        case "do":
                            identificatore = "";
                        return Word.dotok;

                        case "seq":
                            identificatore = "";
                        return Word.seq;

                        case "print":
                            identificatore = "";
                        return Word.print;

                        case "read":
                            identificatore = "";
                        return Word.read;  

                    }
                    
                    return new Word(Tag.ID, identificatore);

 // ... gestire il caso dei numeri ... //

                } else if (Character.isDigit(peek)) {
                    String numero = "";

                    while(Character.isDigit(peek)){
                        numero += peek;
                        readch(br);
                    }
                    return new NumberTok( Integer.parseInt(numero));
                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
            }
        }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
