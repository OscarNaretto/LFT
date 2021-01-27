//Il DFA rinoscelinguaggio di stringhe (sull’alfabeto {/, *, a} ) che contengono “commenti”
//delimitati da /* e */ , ma con la possibilità di avere stringhe prima e dopo
public class Esercizio_10{
    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if(ch == 'a' || ch == '*')
                        state = 0;
                    else if(ch == '/')
                        state = 1;
                    else 
                        state = -1;
                    break;
                case 1:
                    if(ch == '/')
                        state = 1;
                    else if(ch == 'a')
                        state = 0;
                    else if(ch == '*')
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 2:
                    if(ch == 'a' || ch == 'l')
                        state = 2;
                    else if(ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if(ch == '*')
                        state = 3;
                    else if(ch == '/')
                        state = 0;
                    else if(ch == 'a')
                        state = 2;
                    else 
                        state = -1;
                    break;    
            }
        }
        return state == 0 || state == 1;
    }

//Stringhe accettate:“aaa/****/aa ”, “aa/*a*a*/ ”,“aaaa ”, “///““/****/ ”, “/*aa*/ ”, “*/a ”, “a/**/***a ”, “a/**/***/a ” e “a/**/aa/***/a
//Stringhe non accettate:“aaa/*/aa ” oppure “aa/*aa ”.
    public static void main(String args[]){
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    } 
}
