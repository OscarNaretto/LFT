public class NumberTok extends Token {
	public int number = 0;
	public NumberTok(int number){
		super(Tag.NUM);
		this.number = number;
	}

	public String toString(){
		return "<" + tag + ", " + number + ">";
	}
}
