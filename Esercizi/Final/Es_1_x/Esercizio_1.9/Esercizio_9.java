 
//Il DFA definito sull’alfabeto {/,*,a} che riconosca il linguaggio di “commenti” delimitati da
//* (all’inizio) e */ //(alla fine)
//L’automa accetta stringhe sull’alfabeto che contengono almeno 4 caratteri
//che iniziano con /*, che finiscono con */
//che contengono una sola occorrenza della stringa */, quellafinale (dove l’asterisco della stringa */*/ non deve essere in comunecon quello della stringa /* all’inizio, )

public class Esercizio_9 {
    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);
            switch(state){
                case 0:
                    if(ch == '/')
                        state = 1;
                    else
                        state = -1;
                    break;
        
                case 1:
                    if(ch == '*')
                        state = 2;
                    else
                        state = -1;
                    break;
        
                case 2:
                    if(ch == 'a' || ch == '/')
                        state = 2;
                    else if(ch == '*')
                         state = 3;
                    else
                        state = -1;
                    break;
        
                case 3:
                    if( ch == '*')
                        state = 3;
                    else if (ch == 'a')
                         state = 2;
                    else if(ch == '/')
                         state = 4;
                    else 
                         state = -1;
                    break;
        
                case 4:
                break;  
            }
        }
        return state == 4;
    }

//Stringhe accettate:“/****/”, “/*a*a*/”, “/*a/**/”, “/**a///a/a**/”, “/**/” e “/*/*/”
//Stringhe non accettate:“/*/”, oppure “/**/***/”
    public static void main(String args[]){
        // System.out.println(scan("/****/") ? "OK" : "NOPE");
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    } 
}
