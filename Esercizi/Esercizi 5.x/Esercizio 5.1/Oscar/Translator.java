import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

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
	    } else error("syntax error");
    }

    public void prog() {  
        int next_label = code.newLabel();
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
            statlist(next_label);
            code.emitLabel(next_label);
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
            break;
            default:
                error("syntax error");
                break;
        }
    }

    private void statlist(int next_label) {
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat(next_label);       //da finire
                statlistp(next_label);
                break;
            default:
                error("syntax error");
                break;
        }
    }

    private void statlistp(int next_label) {
        switch (look.tag) {
            case ';':
                match(Token.semicolon.tag);
                stat(next_label);           //da finire
                statlistp(next_label);
                break;
            case Tag.EOF:
                break;
            default:
                error("syntax error");
                break;
        }
        //code.emitLabel(next_label); va emessa oppure no qui?
    }

    public void stat(int next_label) {
        switch(look.tag) {
            case '=':
                match(Token.assign.tag);
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore, id_addr);
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            
            case Tag.PRINT:
                match(Tag.PRINT);
				match(Token.lpt.tag);
				exprlist(/*codice??*/);
				code.emit(OpCode.invokestatic,1);
				match(Token.rpt.tag);
                break;

            case Tag.READ:
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
                    code.emit(OpCode.invokestatic,0);   //fatto
                    code.emit(OpCode.istore,id_addr); 
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;

            case Tag.COND:
                match(Tag.COND);
                int false_label_cond = code.newLabel();
                whenlist(false_label_cond);
                match(Tag.ELSE);
                code.emit(OpCode.GOto, next_label);
                code.emitLabel(false_label_cond);
                stat(next_label);
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match(Token.lpt.tag);
                int loop_label_while = code.newLabel();
                int continue_label_while = code.newLabel();
                code.emitLabel(loop_label_while);
                bexpr(continue_label_while, next_label);
                match(Token.rpt.tag);
                stat(loop_label_while);
                code.emit(OpCode.GOto, loop_label_while);
                code.emitLabel(next_label);
                break;

            case '{':
                match(Token.lpg.tag);
                statlist(next_label);
                match(Token.rpg.tag);
                break;
        }
     }

    private void whenlist(int false_label_cond) {
        switch(look.tag){
            case Tag.WHEN:
                whenitem(false_label_cond);       
                whenlistp(false_label_cond);
                break;
            default:
                error("syntax error");
                break;
        }
    }

    private void whenlistp(int false_label_cond) {
        switch(look.tag){
            case Tag.WHEN:
                whenitem(false_label_cond);           
                whenlistp(false_label_cond);
                break;
            case Tag.ELSE: break;
            default:
                error("syntax error");
                break;
        }
    }

    private void whenitem(int false_label_cond) {
        switch(look.tag){
            case Tag.WHEN:
                int true_label_cond = code.newLabel();
                match(Tag.WHEN);            
                match(Token.lpt.tag);
                bexpr(true_label_cond, false_label_cond);
                match(Token.rpt.tag);
                match(Tag.DO);
                code.emitLabel(true_label_cond);
                stat(true_label_cond);
                break;
            default:
                error("syntax error");
                break;
        }
    }

    private void bexpr(int true_label_cond, int false_label_cond) {     //FATTO
        switch(((Word)look).lexeme){
            case "<":
                match(Tag.RELOP);   
                expr();
                expr();
                code.emit(OpCode.if_icmplt, true_label_cond);
			    code.emit(OpCode.GOto, false_label_cond);
                break;

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
                error("syntax error");
                break;
        }
    }

    private void expr() {
        switch(look.tag) {
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist();
                code.emit(OpCode.iadd);
                match(Token.rpt.tag);
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist();
                code.emit(OpCode.imul);
                match(Token.rpt.tag);
                break;
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);  //da finire  
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            default:
                error("syntax error");
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
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    expr();
                    exprlistp();  //da finire
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            default:
                error("syntax error");
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
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    expr();
                    exprlistp();  //da finire
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            case ')':
                break;
            default:
                error("syntax error");
                break;
        }
    }
}

