/*Il DFA che riconosce le stringhe che contengono
un numero di matricola seguito (subito) da un cognome, del turno T2 o T3
*/

public class Esercizio_3{
    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else 
                        state = -1;
                    break;
                case 1:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else if(ch >= 'L' && ch <= 'Z')
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 2:
                    if(ch >= 'a' && ch <= 'z')
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 3:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 3;
                    else if(ch >= 'A' && ch <= 'K')
                        state = 2;
                    else 
                        state = -1;
                    break;
            }
        }
    return state == 2;
    }

/* 
Stringhe accettate: “2Bianchi ” e “122B ”
Stringhe non accettate: “654321 ” e “Rossi ”
*/    
    public static void main(String args[]){
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }    
}
