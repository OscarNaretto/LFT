/*L’automa riconosce le combinazioni di matricola e cognome di studenti del turno 2 o del turno 3 del laboratorio,
dove il numero di matricola e il cognome:
possono essere separati da una sequenza di spazi
possono essere precedute e/o seguite da sequenze eventualmente vuote di spazi. 
*/
public class Esercizio_4{
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
                        state = 2;
                    else if (ch == ' ')
                        state = 0;
                    else 
                        state = -1;
                    break;
                case 1:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 2;
                    else if(ch == ' ')
                        state = 3;
                    else if(ch >= 'L' && ch <= 'Z')
                        state = 5;
                    else 
                        state = -1;
                    break;
                case 2:
                    if(ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                        state = 1;
                    else if(ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                        state = 2;
                    else if(ch == ' ')
                        state = 4;
                    else if(ch >= 'A' && ch <= 'K')
                        state = 5;
                    else 
                        state = -1;
                    break;
                case 3:
                    if(ch >= 'L' && ch <= 'Z')
                        state = 5;
                    else if(ch == ' ')
                        state = 3;
                    else 
                        state = -1;
                    break;
                case 4:
                    if(ch >= 'A' && ch <= 'K')
                        state = 5;
                    else if(ch == ' ')
                        state = 4;
                    else 
                        state = -1;
                    break;
                case 5:
                    if(ch == ' ')
                        state = 6;
                    else if(ch >= 'a' && ch <= 'z')
                        state = 5;
                    else 
                        state = -1;
                    break;
                case 6:
                    if(ch == ' ')
                        state = 6;
                    else 
                        state = -1;
                    break;
            }
        }
        return state == 5 || state == 6;
    }

/*
Stringhe accettate: “654321 Rossi ” e “ 123456 Bianchi ” (dove, nel secondo esempio, ci sono spazi prima del primo carattere e dopo l’ultimo carattere)
Stringhe non accettate: “1234 56Bianchi ” e “123456Bia nchi ”. 
*/
    public static void main(String args[]){
        System.out.println(scan("1234 56Bianchi ") ? "OK" : "NOPE");
        //System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }    
}
