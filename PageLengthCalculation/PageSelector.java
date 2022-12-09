import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;


public class PageSelector {
	public static void Select()
	{
		String inLine = "";		
		try
		{
			int[] lengths = new int[Constants.N];
			int[] references = new int[Constants.N];
			int[] inLinks = new int[Constants.N];
			BufferedReader reader = new BufferedReader(new FileReader(FileNames.outputFile));
			BufferedReader refReader = new BufferedReader(new FileReader(FileNames.referencesFile));
			BufferedReader inLinksReader = new BufferedReader(new FileReader(FileNames.inLinksFile));
			String line = reader.readLine();
			String refLine = refReader.readLine();
			inLine = inLinksReader.readLine();
			while(line!= null)
			{
				int ind;
				String[] tokens = line.split(" ");
				ind = Integer.parseInt(tokens[0]);
				lengths[ind] = Integer.parseInt(tokens[2]);
			    line = reader.readLine();	
			}
			while(refLine != null)
			{
			    String[] tokens = refLine.split(" ");
			    int ind = Integer.parseInt(tokens[0]);
			    references[ind] = Integer.parseInt(tokens[1]);
			    refLine = refReader.readLine();
			}
			while(inLine != null)
			{
			    String[] tokens = inLine.split(" ");
			    int ind = Integer.parseInt(tokens[0]);
			    if(ind < Constants.N)
			    {
			    	inLinks[ind] = Integer.parseInt(tokens[1]);
			    }
			    inLine = inLinksReader.readLine();
			}
			reader.close();
			inLinksReader.close();
			refReader.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FileNames.docIdsToSkip));
			for(int i = 0; i<Constants.N; i++)
			{
				if(lengths[i] < 300 && references[i] <=3 && (inLinks[i] <= 4 || inLinks[i] >1000))
				{
					writer.write(i+"\n");
				}
			}
			writer.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

}
