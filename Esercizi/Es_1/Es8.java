public class Es8{

    public static boolean scan(String s) 
    {
    int state = 0;
    int i = 0;

	while (state >= 0 && i < s.length()) {
        final char ch = s.charAt(i++);
        
    switch (state) {

        case 0:
        if(ch == 'V')
         state = 1;
        else if(ch == '*')
         state = 5;
        else 
         state = -1;
        break;

        case 1:
        if(ch == 'i')
         state = 2;
        else if(ch == '*')
         state = 6;
        else 
         state = -1;       
        break;

        case 2:
        if(ch == 'v')
         state = 3;
        else if(ch == '*')
         state = 7;
        else 
         state = -1;       
        break;

        case 3:
        if(ch == '*' || ch == 'o')
         state = 4;
        else 
         state = -1;        
        break;

        case 4:
        break;

        case 5:
        if(ch == 'i')
        state = 6;
         else 
        state = -1; 
        break;
        
        case 6:
        if(ch == 'v')
        state = 7;
         else 
        state = -1; 
        break;

        case 7:
        if(ch == 'i')
            state = 4;
        else 
         state = -1;
        break;
        }

    }
    return state == 4;
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}