import java.io.*; 

public class Lexer {
    public static int line = 1;
    private char peek = ' ';
    
    
    private void readch(BufferedReader br) {   /* metodo che legge il CARATTERE SUCCESSIVO */
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    private void cleaner(BufferedReader br){
        /* metodo che esegue la traduzione in token del testo in imput */ 
		/* se durante la traduzione vengono incontrati spazi, new line, ecc vengono ingorati e viene letto il carattere successivo*/
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
    }

    public Token lexical_scan(BufferedReader br) {  

        cleaner(br);                        //Chiamo il metodo che ignora spazi, new line, ecc

        if (peek == '/'){
            readch(br);
            if (peek == '/') {              //Se leggo commento su una riga
                while (peek != (char)-1 && peek != '\n') { //Ciclo finchè non finisce la stringa o vado a capo
                   readch(br);
                }
                if (peek != (char)-1) {     //Se la stringa non è finita leggo il successivo carattere
                    readch(br);
                }
            } else if (peek == '*') {       //Se leggo un commento su più righe
                boolean flag = true;
                while (flag) {
                    readch(br);
                    if (peek == '*') {       //Se incontro * controllo che il successivo sia /, altrimenti ciclo ancora
                        readch(br);
                        if (peek == '/'){
                            flag = false;
                        }
                    }
                }
                readch(br);                    //Ciclo e ignoro spazi, new line, ecc
                cleaner(br);
            } else {
                peek = ' ';
                return Token.div;
            }
        }
        
        switch (peek) {
            case '!':
                peek = ' ';                 // se viene letto un carattere, rimette peek a ' ' per il buon funzionamento dello scanner 
                return Token.not;

	    // ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //
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

            case ';':
                peek = ' ';
                return Token.semicolon;
                            
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

        // ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //
            case '|':
                readch(br);
                if(peek == '|'){
                   peek = ' ';
                return Word.or; 
                }else{
                    System.out.println("Erroneous character"
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
                }else
                    peek = ' ';
                    return Word.lt;

            case '>':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.ge;
                }else
                    peek = ' ';
                    return Word.gt;

            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq;
                }else
                    peek = ' ';
                    return Token.assign;
          
            case (char)-1:
                return new Token(Tag.EOF);
                                                    // se peek non corrisponde a nessuno dei simboli conosciuti 
            default:                                          
                if (Character.isLetter(peek) || peek == '_') {     // se peek è una lettera, potrebbe essere l'inizio di una
                                                                    //parola chiave o di una nuova parola identificatore

        // ... gestire il caso degli identificatori e delle parole chiave //
                String identificatore = "";
                while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){  //continuo a comporre la stringa s finche trovo una lettera
                                                                                                // numeri e _
                    identificatore += peek;
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
                    
                    case "_":
                        identificatore = "";
                        System.err.println("Non c'è nulla dopo l'underscore");
                        return null;
                }

                String x = identificatore;
                identificatore = "";
                return new Word(Tag.ID,x);

            
            } else if (Character.isDigit(peek)) {

        // ... gestire il caso dei numeri ... //
                String Numero = "";
                while(Character.isDigit(peek)){ //finchè leggo numeri
                    Numero = Numero + peek;
                    readch(br);
                }

                if(Character.isDigit(peek)){
                    String x = Numero;
                    Numero = "";
                    return new NumberTok(Integer.parseInt(x));
                }else{                              // Se l'ultimo carattere letto non è un numero
                    System.err.println("Errore, non puoi inziare con un numero");
                    return null;
                }
            
            }else {
                    System.err.println("Erroneous character: " + peek );
                    return null;
                }
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
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
