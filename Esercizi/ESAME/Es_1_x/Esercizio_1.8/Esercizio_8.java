import javax.lang.model.util.ElementScanner6;

/*Il DFA che riconosca il linguaggio di stringhe che contengono il tuo nome e tutte le
stringhe ottenute dopo la sostituzione di un carattere del nome con un altro qualsiasi.
*/

public class Esercizio_8{
    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if(ch == 'P' )
                        state = 1;
                    else if(ch != 'P')
                        state = 6;
                    else
                        state = -1;
                    break;
                        
                case 1:
                    if(ch == 'a')
                        state = 2;
                    else if(ch != 'a')
                        state = 7;
                    else
                        state = -1;
                    break;

                case 2:
                    if(ch == 'o')
                        state = 3;
                    else if(ch != 'o')
                        state = 8;
                    else
                        state = -1;
                    break;  
                    
                case 3:
                    if(ch == 'l')
                         state = 4;
                    else if(ch != 'l')
                          state = 9;
                    else
                        state = -1;
                    break;

                case 4:
                    if(ch != (char)-1)
                        state = 5;
                    else
                        state = -1;
                    break;

                case 5:
                    break;
                    
                case 6:
                    if(ch == 'a')
                        state = 7;
                    else
                        state = 1;
                    break;

                case 7:
                    if(ch == 'o')
                        state = 8;
                    else
                        state = 1;
                    break;

                case 8:
                    if(ch == 'l')
                        state = 9;
                    else
                        state = 1;
                    break;
    
                case 9:
                    if(ch == 'o')
                        state = 4;
                    else
                        state = 1;
                    break;                   
            }
        }
        return state == 4;
    }

/*
 Ad esempio, nel caso di uno studente che si chiama Paolo, il DFA
 accetta la stringa: “Paolo”(cioè il nome scritto correttamente),“Pjolo”, “caolo”,"“Pa%lo”, “Paola” e “Parl"
 non accetta la stringa:“Eva”, “Peola”, “Pietro” oppure “P*o* 

*/
    public static void main(String args[]){
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    } 
}
