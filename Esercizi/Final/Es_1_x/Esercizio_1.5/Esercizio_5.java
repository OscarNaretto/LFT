/*L'automa riconosce le stringhe che contengono
matricola e cognome di studenti del turno 2 o del turno 3 del
laboratorio, ma in cui il cognome precede il numero di matricola
*/
public class Esercizio_5{
    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if(ch >= 'L' && ch <= 'Z')
                        state = 1;
                    else if(ch >= 'A' && ch <= 'K')
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 1:
                    if(ch >= 'a' && ch <= 'z')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 7;
                    else 
                        state = -1;
                    break;
                case 2:
                    if(ch >= 'a' && ch <= 'z')
                        state = 2;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 8;
                    else if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6;
                    else 
                        state = -1;
                    break;
                case 3:
                    if(ch >= 'A' && ch <= 'Z')
                        state = 2;
                    else 
                        state = -1;
                    break;
                case 4:
                    if(ch >= 'A' && ch <= 'Z')
                        state = 1;
                    else 
                        state = -1;
                    break;
                case 5:
                    if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 7;
                    else 
                        state = -1;
                    break;
                case 6:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 8;
                    else 
                        state = -1;
                    break;
                case 7:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 7;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 5;
                    else 
                        state = -1;
                    break;
                case 8:
                    if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 8;
                    else if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 6;
                    else 
                        state = -1;
                    break;
            }
        }
        return state == 7 || state == 8;
    }
/* 
Stringhe accettate: “Bianchi2 ” e “B122”
Stringhe non accettate: “654321 ” e “Rossi ”
*/ 
    public static void main(String args[]){
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    } 
    
}
