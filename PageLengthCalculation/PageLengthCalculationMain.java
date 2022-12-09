
public class PageLengthCalculationMain {
	public static void main(String[] args)
	{
		LengthCalculator lengthCalculator = new LengthCalculator();
		lengthCalculator.Process();
		String s = "hello1234343-hi";
		s = s.replaceAll("[=\\-+#()|\"'.,;:\\*\\?!_\\$]", " "); ;
		//System.out.println(s);
		//System.out.println(t+" "+u);
		//CreateInput();
		//PageSelector.Select();
		//Helper.CountDocsParsed();
	}

}
