/*Il traduttore si occupa di generare e accumulare nella propria variabile code il codice intermedio necessario 
a jasmin per la generazione di Output.class*/

import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();               //mappa usata per caricarvi le variabili presenti nel codice da tradurre
    CodeGenerator code = new CodeGenerator();         //attributo nel quale si accumula il codice da scrivere poi nell'output
    int count=0;                                      //all'interno di una lista di oggetti Instruction

    public Translator(Lexer l, BufferedReader br) {
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
	    } else error("syntax error during match() execution");
    }

    public void prog() {  
        switch(look.tag){
            case '=':                                 //P--> SL EOF --- Guida = =,print,read,cond,while,{
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                int next_label = code.newLabel();     //creazione dell'etichetta
                statlist(next_label);                 //viene passata l'etichetta per derivazione a stat() utilizzata in caso di salti
                code.emitLabel(next_label);           //stampa l'ultima label (L0)
                match(Tag.EOF);
                try {
                    code.toJasmin();                  //Richima il metodo toJasmin di CodeGeneretor che, grazie alla classe di supporto Instruction,genera il codice intermedio(IJVM)
                } catch(java.io.IOException e) {
                    System.out.println("IO error\n");
                };
                break;
            default:                                  //se non troviamo il tag previsto dall'insieme guida segnaliamo un'errore nel codice 
                error("syntax error in grammar (prog): token " + look + " can't be accepted");
                break;
        }
    }

    private void statlist(int next_label) {         //viene passata l'etichetta a stat() utilizzata in caso di salti
        switch(look.tag){
            case '=':                               //SL-->S SL' --- Guida = =,print,read,cond,while,{
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat(next_label);   
                statlistp(next_label);
                break;
            default:
                error("syntax error in grammar (statlist): token " + look + " can't be accepted");
                break;
        }
    }

    private void statlistp(int next_label) {          //viene passata l'etichetta a stat() utilizzata in caso di salti
        switch (look.tag) {
            case ';':                                 //SL'--> ;S SL' --- Guida = ;
                match(Token.semicolon.tag);
                stat(next_label);           
                statlistp(next_label);
                break;
            case Tag.EOF:                             //SL'--> 3 --- Guida = EOF,}
            case '}':           
                break;
            default:
                error("syntax error in grammar (statlistp): token " + look + " can't be accepted");
                break;
        }
    }

    public void stat(int next_label) {
        switch(look.tag) {
            case '=':                                                      //S--> =ID(E)--- Guida = =
                match(Token.assign.tag);
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);   //cerchiamo l'id della variabile x e lo carichiamo nella variabile id_addr
                    if (id_addr==-1) {                                     //se la condizione è vera allora la variabile x non è presente
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);            //allora carichiamo la variabile nella Mappa
                    }
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore, id_addr);      //stampo l'istruzione istore per la  variabile x
                } else {
                    error("Error in grammar (stat) after read(): token " + look + " can't be accepted");
                }
                break;
            case Tag.PRINT:                                 //S--> print(EL) --- Guida = print
                match(Tag.PRINT);
				match(Token.lpt.tag);
				exprlist(Tag.PRINT);
				match(Token.rpt.tag);
                break;
            case Tag.READ:                                  //S--> read(ID) --- Guida = read
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);       //stampo l'istruzione read generata da invokstatic con parametro 0
                    code.emit(OpCode.istore,id_addr);
                } else {
                    error("Error in grammar (stat) after read(): token " + look + " can't be accepted");
                }
                break;
            case Tag.COND:                                  //S--> cond WL else S  --- Guida = cond
                match(Tag.COND);
                int false_label_cond = code.newLabel();     //creiamo una nuova etichetta usata per saltare alla condizione falsa
                whenlist(false_label_cond);
                next_label = code.newLabel();               //creiamo una nuova etichetta usata per saltare alla condizione vera
                code.emit(OpCode.GOto, next_label);         //stampa il goto dell'etichetta x  
                match(Tag.ELSE);
                code.emitLabel(false_label_cond);           //stampiamo l'etichetta con condizione falsa
                stat(next_label);
                code.emitLabel(next_label);             
                break;
            case Tag.WHILE:                                 //S--> while(B)S --- Guida = while
                match(Tag.WHILE);
                match(Token.lpt.tag);
                int loop_label_while = code.newLabel();     //etichetta del loop
                int continue_label_while = code.newLabel(); //etichetta per entrare nel corpo del while
                int next_instruction = code.newLabel();     //etichetta della prossima istruzione dopo il while
                code.emitLabel(loop_label_while);
                bexpr(continue_label_while, next_instruction);          //gestisce la condizione del loop
                match(Token.rpt.tag);
                code.emitLabel(continue_label_while);
                stat(next_instruction);
                code.emit(OpCode.GOto, loop_label_while);
                code.emitLabel(next_instruction);
                break;
            case '{':                                       //S--> {SL} --- Guida = {
                match(Token.lpg.tag);
                statlist(next_label);
                match(Token.rpg.tag);
                break;
            default:
                error("syntax error in grammar (stat): token " + look + " can't be accepted");
                break;
        }
     }

    private void whenlist(int false_label_cond) { //lista delle condizioni 
        switch(look.tag){                         //passa la label a whenitem e whenlist che a loro volta la passeranno a bexpr
            case Tag.WHEN:                        //WL --> WI WL' --- Guida = when
                whenitem(false_label_cond);       
                whenlistp(false_label_cond);
                break;
            default:
                error("syntax error in grammar (whenlist): token " + look + " can't be accepted");
                break;
        }
    }

    private void whenlistp(int false_label_cond) {
        switch(look.tag){
            case Tag.WHEN:                              //WL' --> WI WL' --- Guida = when
                whenitem(false_label_cond);           
                whenlistp(false_label_cond);
                break;
            case Tag.ELSE:                              //WL --> 3 --- Guida = else
                break;
            default:
                error("syntax error in grammar (whenlistp): token " + look + " can't be accepted");
                break;
        }
    }

    private void whenitem(int false_label_cond) {           //when do
        switch(look.tag){
            case Tag.WHEN:                                  //WI --> when(B)doS --- Guida = when
                int true_label_cond = code.newLabel();
                match(Tag.WHEN);            
                match(Token.lpt.tag);
                bexpr(true_label_cond, false_label_cond);
                match(Token.rpt.tag);
                match(Tag.DO);
                code.emitLabel(true_label_cond);            //etichetta per espressione true
                stat(true_label_cond);
                break;
            default:
                error("syntax error in grammar (whenitem): token " + look + " can't be accepted");
                break;
        }
    }

    private void bexpr(int true_label_cond, int false_label_cond) {  // metodo usato per verificare le condioni e stampare i salti 
        switch(((Word)look).lexeme){
            case "<":                                                //B -- > RELOP E E --- Guida = RELOP
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmplt, true_label_cond);        //dopo aver richiamato le procedure expr, emettiamo il codice inerente al confronto e saltiamo alla label relativa al caso True
			    code.emit(OpCode.GOto, false_label_cond);            //saltiamo alla label inerente al caso False
                break;                                               //questo ragionamento vale ovviamente per tutti i tipi di comparazione di bexpr
            case ">":
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmpgt, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;
            case "==":
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmpeq, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;
            case "<=":
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmple, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;
            case "<>":
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmpne, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;
            case ">=":
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(OpCode.if_icmpge, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;
            default:
                error("syntax error in grammar (bexpr): token " + look + " can't be accepted");
                break;
        }
    }

    private void expr() {
        switch(look.tag) {
            case '+':                             //E --> +(EL) --- Guida = +
                match(Token.plus.tag);
                match(Token.lpt.tag);       
                exprlist('+');                    //passiamo l'attributo "+" in maniera che exprlist possa memorizzare l'operzione da eseguire a catena 
                match(Token.rpt.tag);
                break;
            case '-':                             //E --> -EE --- Guida = -
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);           //Dopo aver richiamato le procedure expr emettiamo il codice dell'operazione 
                break;
            case '*':                             //E --> *(EL) --- Guida = *
                match(Token.mult.tag);
                match(Token.lpt.tag);       
                exprlist('*');                    //passiamo l'attributo "*" in maniera che exprlist possa memorizzare l'operzione da eseguire a catena
                match(Token.rpt.tag);
                break;
            case '/':                                                          //E --> /EE --- Guida = /
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);                                        //Dopo aver richiamato le procedure expr emettiamo il codice dell'operazione 
                break;
            case Tag.NUM:                                                      //E --> NUM --- Guida = NUM
                code.emit(OpCode.ldc, ((NumberTok)look).number);               //accediamo all'attributo number tramite downcast
                match(Tag.NUM);
                break;
            case Tag.ID:                                                       //E --> ID --- Guida = ID
                code.emit(OpCode.iload, st.lookupAddress(((Word)look).lexeme));//accediamo all'attributo lexeme tramite downcast e ne cerchiamo l'indirizzo 
                match(Tag.ID);
                break;
            default:
                error("syntax error in grammar (expr): token " + look + " can't be accepted");
                break;
        }
    }
    
    private void exprlist(int operation) { //EL -->  E EL' --- Guida = +,-,*,/,NUM,ID  
        switch(look.tag){                  //exprlist prende ad argomento l'operazione da eseguire
            case '+':                      //questo serve infatti a tenere l'operazione in memoria
            case '-':                      //nel caso di somme o moltiplicazioni al fine di poterle concatenare
            case '*':
            case '/':
            case Tag.NUM:           
                expr();
                exprlistp(operation);                  
                break;
            case Tag.ID:
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    expr();
                    exprlistp(operation);
                } else {
                    error("Error in grammar (exprlist) after read(): token " + look + " can't be accepted");
                }
                break;
            default:
                error("syntax error in grammar (exprlist): token " + look + " can't be accepted");
                break;
        }
    }

    private void exprlistp(int operation) {
        switch(look.tag){
            case '+':                            //EL' -->  E EL' --- Guida = +,-,*,/,NUM,ID
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
                expr();
                exprlistp(operation);               
                if (operation == '+'){           //verifichiamo il tipo di operazione da effettuare e ne emettiamo l'operando 
                    code.emit(OpCode.iadd);      //questa verifica con conseguente emit è effettuata solo in EL', per evitare codice con ripetizioni errate
                } else if (operation == '*'){
                    code.emit(OpCode.imul);
                } else if (operation == Tag.PRINT){
                    code.emit(OpCode.invokestatic,1);           //stampo l'istruzione print generata da invokstatic con parametro 1
                }                            
                break;
            case Tag.ID:
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    expr();
                    exprlistp(operation);
                    if (operation == '+'){  
                        code.emit(OpCode.iadd);
                    } else if (operation == '*'){
                        code.emit(OpCode.imul);
                    } else if (operation == Tag.PRINT){
                        code.emit(OpCode.invokestatic,1);           //stampo l'istruzione print generata da invokstatic con parametro 1
                    }   
                } else {
                    error("Error in grammar (exprlistp) after read(): token " + look + " can't be accepted");
                }
                break;
            case ')':
                break;
            default:
                error("syntax error in grammar (exprlistp): token " + look + " can't be accepted");
                break;
        }
    }

    public static void main(String[] args) {
		Lexer lex = new Lexer();

		String path = "test.txt"; 
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
				Translator translator = new Translator(lex, br); 
				translator.prog();
				br.close();
		} catch (IOException e) {e.printStackTrace();}    
	}   

}
