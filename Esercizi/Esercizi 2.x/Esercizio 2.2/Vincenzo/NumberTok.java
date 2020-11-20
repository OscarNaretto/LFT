public class NumberTok extends Token {
	public int num;
	
	public NumberTok(int num){
		super(Tag.NUM); 
		this.num = num;
	}

	public String toString() { return "<" + tag + ", " + num + ">"; }

}
