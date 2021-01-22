//Es2.3

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
        
        cleaner(br);// funzioni generata per evitare ripetizione del codice poiché usata successivamente

        if (peek == '/'){
            readch(br);
            if (peek == '/') { // --> //
                while (peek != (char)-1 && peek != '\n') { // ciclo fino a quando non vado a capo(new line) 
                   readch(br); 
                }
                if (peek != (char)-1) { //esco dal ciclo è controllo se sono arrivato alla fine
                    readch(br); // se non  sono arrivato alla fine allora concludo il commento e leggo da br
                }
            } else if (peek == '*') { // --> /*(inizio commenti)
                boolean flag = true;
                while (flag) {
                    readch(br);//legge a vuoto finche non trova *
                    if (peek == '*') {
                        readch(br);//legge a vuoto
                        if (peek == '/'){//se ricosce / vuol dire che sono alla fine del coomento ( */)
                            flag = false; // imposto la variabeli a false cosi da uscire dal ciclo
                        }
                    }
                }
                readch(br);
                cleaner(br);
            } else {
                peek = ' '; // se non vengono rispettate le condizioni al di sopra di questa riga
                return Token.div; // so che / è un Token
            }
        }

        switch (peek) {

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

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '&':
                readch(br);// leggo il simbolo sucessivo
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
                readch(br);//leggo il simbolo sucessivo
                if(peek == '|'){// se il simbolo sucessivo è | allora restituisco il token del or (||)
                   peek = ' ';
                    return Word.or;// token ||
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
                    return Word.ne;// <>
                }else
                    peek = ' ';
                    return Word.lt;// <

            case '>':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.ge; // >=
                }else
                    peek = ' ';
                    return Word.gt;// >

            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq; // ==
                } else {
                    peek = ' ';
                    return Token.assign; // =
                }

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
                while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){  //continuo a comporre la stringa s finche trovo una lettera
                    identificatore += peek;// identificatore = identificatore + peek
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

                    case "_":
                        System.err.println("Errore: solo underscore");//Si verifica un'errore dato che l'identificatore presenta solo underscore
                        return null;

                    default: return new Word(Tag.ID,identificatore);
                }


            } else if (Character.isDigit(peek)) {

        // ... gestire il caso dei numeri ... //
        //un numere non puo' apparire all'inizio
                String Numero = "";

                while(Character.isDigit(peek)){
                    Numero += peek;
                    readch(br);
                }

                if (Character.isDigit(peek)){
                    return new NumberTok(Integer.parseInt(Numero));

                } else {
                    System.err.println("Non puoi mettere un numero in testa");
                    return null;
                }
            } else {
                System.err.println("Erroneous character");
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
