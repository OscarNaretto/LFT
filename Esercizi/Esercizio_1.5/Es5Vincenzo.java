public class Es5Vincenzo{

    public static boolean scan(String s) 
    {
    int state = 0;
    int i = 0;

	while (state >= 0 && i < s.length()) {
        final char ch = s.charAt(i++);
        
    switch (state) {

        case 0:
        if(ch >= 'A' && ch <= 'Z')
            state = 1;
        else if(ch >= 'L' && ch <= 'K')
            state = 2;
        else 
            state = -1;
        break;

        case 1:
        if(ch >= 'a' && ch <= 'z')
         state = 1;
        else if((char)ch % 2 == 0)
         state = 4;
        else if((char)ch % 2 == 1)
         state = 3;
        else 
         state = -1;
        break;

        case 2:
        if(ch >= 'a' && ch <= 'z')
         state = 2;
        else if((char)ch % 2 == 0)
         state = 6;
        else if((char)ch % 2 == 1)
         state = 5;
        else 
         state = -1;
        break;

        case 3:
        if((char)ch % 2 == 1)
            state = 3;
        else if((char)ch % 2 == 0)
            state = 4;
        else 
            state = -1;
        break;

        case 4:
        if((char)ch % 2 == 0)
            state = 4;
        else if((char)ch % 2 == 1)
            state = 3;
        else 
            state = -1;
        break;

        case 5:
        if((char)ch % 2 == 1)
            state = 5;
        else if((char)ch % 2 == 0)
          state = 6;
        else 
            state = -1;
        break;

        case 6:
        if((char)ch % 2 == 0)
            state = 6;
        else if((char)ch % 2 == 1)
            state = 5;
        else 
            state = -1;
        break;   
        }

    }
    return state == 3 || state == 6;
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
