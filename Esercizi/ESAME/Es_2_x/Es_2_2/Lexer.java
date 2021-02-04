//Es2.2

import java.io.*;
/* Il Lexer è un analizzatore lessicale che riceve in Input un programma scritto in un linguaggio di programmazione,
    come sequenza di caratteri. 
    E restituisce come Output una sequenza di token che corrisponde ad un elemento atomico del linguaggio.
*/
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

    public Token lexical_scan(BufferedReader br) {
        /* metodo che esegue la traduzione in token del testo in imput */
		/* se durante la traduzione vengono incontrati spazi, new line, ecc vengono ingorati e viene letto il carattere successivo*/
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }

        switch (peek) {      //faccio uno switch sul carattere per restituire il rispettivo token
            case '!':
                peek = ' ';  /* se viene letto un carattere, rimette peek a ' ' per il buon funzionamento dello scanner */
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

            case '/':
                peek = ' ';
                return Token.div;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '&':
                readch(br); // leggo il simbolo sucessivo
                if (peek == '&') {// se il simbolo sucessivo e' & allora restituisco il token del and (&&)
                    peek = ' ';
                    return Word.and; // token &&
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

        // ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //
            case '|':
                readch(br); //leggo il simbolo sucessivo 
                if(peek == '|'){ // se il simbolo sucessivo è | allora restituisco il token del or (||)
                   peek = ' ';
                    return Word.or; // token ||
                }else{
                    System.err.println("Erroneous character" + " after | : "  + peek );
                    return null;
                }

            case '<':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.le; // <=
                }else if(peek == '>'){
                    peek = ' ';
                    return Word.ne; // <>
                }else
                    peek = ' ';
                    return Word.lt;// <

            case '>':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.ge; //>=
                }else
                    peek = ' ';
                    return Word.gt; //>

            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq; //==
                }else
                    peek = ' ';
                    return Token.assign; //=

            case (char)-1:
                return new Token(Tag.EOF);
                                                    // se peek non corrisponde a nessuno dei simboli conosciuti
            default:
                if (Character.isLetter(peek) || peek == '_') {     // se peek è una lettera, potrebbe essere l'inizio di una
                                                                   //parola chiave o di una nuova parola identificatore

        // ... gestire il caso degli identificatori e delle parole chiave //

       /*Definizione di identificatori: un
         identificatore è composto da una sequenza non vuota di lettere,
         numeri, ed il simbolo di ‘underscore’ _  :
         1)NON comincia con un numero 
         2)NON può essere composto solo dal simbolo _*/

                String identificatore = "";
                        
                while(peek == '_'){             //controllo che non siano presenti solo '_' e accumulo in identificatore
                    identificatore += peek;     //altrimenti avrò già accumulato la string in identificatore e proseguirò correttamente
                    readch(br);                 
                }
                if ((!Character.isLetter(peek) && !Character.isDigit(peek))){      //se ho solo underscore seguiti da spazio o caratteri di separazione, errore
                    System.err.println("Errore: utilizzo di underscore non valido"); //Si verifica un'errore dato che l'identificatore presenta solo underscore
                    return null;
                }  
                
                while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){  //continuo a comporre la stringa s finche trovo una lettera
                    identificatore += peek; // identificatore = identificatore + peek
                    readch(br);
                }
                
                switch(identificatore){
                        case "cond": return Word.cond;

                        case "when": return Word.when;

                        case "then": return Word.then;

                        case "else": return Word.elsetok;

                        case "while": return Word.whiletok;

                        case "do": return Word.dotok;

                        case "seq": return Word.seq;

                        case "print": return Word.print;

                        case "read": return Word.read;

                        default: return new Word(Tag.ID,identificatore);
                    }

            } else if (Character.isDigit(peek)) {

                // ... gestire il caso dei numeri ... //
                //un numere non puo' apparire all'inizio
                String Numero = "";
        
                while(Character.isDigit(peek)){ //inizio l'analisi di valori numerici. In seguito controllerò se il token è composto solamente da numeri o no
                    Numero += peek;
                    readch(br);
                }
                
                //controllo l'ultimo carattere letto, che può essere l'ultimo numero oppure la prima occorrenza di un carattere non numerico
                
                if (Character.isLetter(peek) || peek == '_'){   //se il carattere non è numerico, segnalo un errore   
                    System.err.println("Non puoi mettere un numero in testa ad un identificatore!");
                    return null;
                } else {
                    return new NumberTok(Integer.parseInt(Numero));
                }
            } else {
                System.err.println("Erroneous character");
                return null;
            }   
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Test.txt"; // il percorso del file da leggere
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
