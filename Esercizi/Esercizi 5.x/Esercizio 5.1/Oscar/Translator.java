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
        int lnext_prog = code.newLabel();
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
            statlist(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void statlist(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat(lnext_prog);       //da finire
                statlistp(lnext_prog);
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void statlistp(int operand) {
        int lnext_prog = operand + 1;
        switch (look.tag) {
            case ';':
                match(Token.semicolon.tag);
                stat(lnext_prog);           //da finire
                statlistp(lnext_prog);
                break;
            case Tag.EOF:
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    public void stat(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag) {
                //da finire






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
        }
        code.emitLabel(lnext_prog);
     }

     private void whenlist(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag){
            case Tag.WHEN:
                whenitem(lnext_prog);       //da finire
                whenlistp(lnext_prog);
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void whenlistp(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag){
            case Tag.WHEN:
                whenitem(lnext_prog);           //da finire
                whenlistp(lnext_prog);
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void whenitem(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag){
            case Tag.WHEN:
                match(Tag.WHEN);            //da finire
                match(Token.lpt.tag);
                bexpr(lnext_prog);
                match(Token.rpt.tag);
                match(Tag.DO);
                stat(lnext_prog);
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void bexpr(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag){
            case Tag.RELOP:
                match(Tag.RELOP);   //da finire
                expr(lnext_prog);
                expr(lnext_prog);
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void expr(int operand) {
        int lnext_prog = operand + 1;
        switch(look.tag) {
            case '+':
                match(Token.plus.tag);
                match(Token.lpt.tag);
                exprlist(lnext_prog);
                code.emit(OpCode.iadd);
                match(Token.rpt.tag);
                break;
            case '-':
                match('-');
                expr(lnext_prog);
                expr(lnext_prog);
                code.emit(OpCode.isub);
                break;
            case '*':
                match(Token.mult.tag);
                match(Token.lpt.tag);
                exprlist(lnext_prog);
                code.emit(OpCode.imul);
                match(Token.rpt.tag);
                break;
            case '/':
                match(Token.div.tag);
                expr(lnext_prog);
                expr(lnext_prog);
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
        code.emitLabel(lnext_prog);
    }
    

    private void exprlist(int operand) {
        int lnext_prog = operand + 1;
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
                    expr(lnext_prog);
                    exprlistp(lnext_prog);  //da finire
                } else {
                    error("Error in grammar (stat) after read( with " + look);
                }
                break;
            default:
                error("syntax error");
                break;
        }
        code.emitLabel(lnext_prog);
    }

    private void exprlistp(int operand) {
        int lnext_prog = operand + 1;
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
                    expr(lnext_prog);
                    exprlistp(lnext_prog);  //da finire
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
        code.emitLabel(lnext_prog);
    }
}

