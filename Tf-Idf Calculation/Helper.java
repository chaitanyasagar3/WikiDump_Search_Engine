import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;


public class Helper {
	public static void GetGraphPoints()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("/home/sri/sjsu docs/Search engine/assignment 3/docLength"));
			String line = reader.readLine();
			int i = 1;
			while(line != null)
			{
				String[] words = line.split(" ");
				System.out.println(i+" "+words[2]);
				i++;
				line = reader.readLine();
			}
		}
		catch(Exception ex)
		{

		}
	}
	
	public static void SortDocIds()
	{
		try
		{
			BufferedReader skippedReader = new BufferedReader(new FileReader("/home/sri/sjsu docs/Search engine/assignment 3/Source Files/skipDocIds"));
			//int[] skippedDocs = new int[1581175];
			HashSet<Integer> skippedDocs = new HashSet<Integer>();
			String line = skippedReader.readLine();
			int i = 0;
			while(line != null)
			{
				//skippedDocs[i] = Integer.parseInt(line);
				skippedDocs.add(Integer.parseInt(line));
				line = skippedReader.readLine();
			}
			skippedReader.close();

			String[] docNames = new String[4540303];
			BufferedReader docs = new BufferedReader(new FileReader("/home/sri/sjsu docs/Search engine/assignment 3/Source Files/docIdName"));
			line = docs.readLine();
			while(line != null)
			{
				String[] info = line.split(" ");
				docNames[Integer.parseInt(info[0])] = info[1];
				line = docs.readLine();
			}
			docs.close();
			System.out.println("hi");
			BufferedWriter sortedDocs = new BufferedWriter(new FileWriter("/home/sri/sjsu docs/Search engine/assignment 3/Source Files/docIdNameSorted"));
			i = 0;
			while(i<4540303)
			{
				if(!skippedDocs.contains(i))
				{
					sortedDocs.write(i+" "+docNames[i]+"\n");
				}
				i++;
			}
			sortedDocs.close();
			System.out.println("done");			
		}
		catch(Exception ex)
		{

		}
	}
	public static void CountDocsInParsedFile()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("/home/sri/sjsu docs/Search engine/assignment 3/parsedFile"));
			String line = reader.readLine();
			int docs = 0;
			while(line!=null)
			{
				if(line.startsWith("<$id$>"))
				{
					docs++;
				}
				line = reader.readLine();
			}
			System.out.println(docs);
			reader.close();
		}
		catch(Exception ex)
		{

		}
	}
	public static void PopulateFileSeekPositions()
	{
		long[] fileSeekPositions = new long[27];
		String ag = "D:/PS3/indexFiles/a-g.txt";
		String hn = "D:/PS3/indexFiles/h-n.txt";
		String ou = "D:/PS3/indexFiles/o-u.txt";
		String vz = "D:/PS3/indexFiles/v-z.txt";
		try
		{
			RandomAccessFile file = new RandomAccessFile(ag, "r");
			char ch = 'a';
			int ind = ch-97;
			fileSeekPositions[ind] = 0;
			ch = 'h';
			ind = ch-97;
			fileSeekPositions[ind] = 0;
			ch = 'o';
			ind = ch-97;
			fileSeekPositions[ind] = 0;
			ch='v';
			ind = ch-97;
			fileSeekPositions[ind] = 0;
			fileSeekPositions[26] = 0;
			long endPos = file.length();

			for(ch = 'b'; ch <= 'g'; ch++)
			{
				ind = ch-97;
				fileSeekPositions[ind] = FindStart(ch, fileSeekPositions[ind-1], endPos, file);
			}

			file.close();
			file = new RandomAccessFile(hn, "r");
			endPos = file.length();
			for(ch = 'i'; ch<='n'; ch++)
			{
				ind = ch-97;
				fileSeekPositions[ind] = FindStart(ch, fileSeekPositions[ind-1], endPos, file);
			}
			file.close();
			file = new RandomAccessFile(ou, "r");
			endPos = file.length();
			for(ch = 'p'; ch<='u'; ch++)
			{
				ind = ch-97;
				fileSeekPositions[ind] = FindStart(ch, fileSeekPositions[ind-1], endPos, file);
			}
			file.close();
			file = new RandomAccessFile(vz, "r");
			endPos = file.length();
			for(ch = 'w'; ch<='z'; ch++)
			{
				ind = ch-97;
				fileSeekPositions[ind] = FindStart(ch, fileSeekPositions[ind-1], endPos, file);
			}
			file.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter("D:/PS3/Assignment3_Srividya/fileSeekPos"));
			for(int i = 0; i<27; i++)
			{
				if(i!=0)
				{
					writer.write("\n");
				}
				writer.write(fileSeekPositions[i]+"");
			}
			writer.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}
	public static long FindStart(char ch,long startInd, long endInd, RandomAccessFile file)
	{
		long start = startInd;
		long end = endInd;
		try
		{			
			while(start <= end)
			{
				file.seek(start);
				char currCh = (char)file.readByte();
				if(currCh == ch)
				{
					return start;
				}
				else
				{
					String s = file.readLine();
					start += s.length()+2;
				}
			}	
		}
		catch(Exception ex)
		{

		}
		return start;
	}
	public static void Test()
	{
		try
		{
			String ag = "D:/PS3/indexFiles/a-g.txt";
			String hn = "D:/PS3/indexFiles/h-n.txt";
			String ou = "D:/PS3/indexFiles/o-u.txt";
			String vz = "D:/PS3/indexFiles/v-z.txt";
			RandomAccessFile file = new RandomAccessFile(ag,"r");
			file.seek(2141806267);
			//file.readLine();
			System.out.println(file.readLine());
			file.close();
		}
		catch(Exception ex)
		{

		}
	}
}
