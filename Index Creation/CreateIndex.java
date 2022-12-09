import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class CreateIndex {

	BufferedReader reader;
	HashMap<String, ArrayList<Integer>> currentInvertedIndexContents = new HashMap<String, ArrayList<Integer>>();
	long currentIndexSize = 0;
	int currentIndexFileNumber = 1;
	HashSet<String> stopWords = new HashSet<String>();
	private void Initialize()
	{		
		stopWords.add("are");		
		stopWords.add("and");
		stopWords.add("etc");
		stopWords.add("has");
		stopWords.add("however");
		stopWords.add("http");
		stopWords.add("if");
		stopWords.add("image");
		stopWords.add("in");
		stopWords.add("instead");
		stopWords.add("into");
		stopWords.add("is");
		stopWords.add("it");
		stopWords.add("most");
		stopWords.add("mostly");
		stopWords.add("on");
		stopWords.add("only");
		stopWords.add("of");
		stopWords.add("off");
		stopWords.add("or");
		stopWords.add("over");
		stopWords.add("so");
		stopWords.add("that");
		stopWords.add("the");
		stopWords.add("this");
		stopWords.add("there");
		stopWords.add("to");
		stopWords.add("with");
		stopWords.add("www");
	}
	
	private void UpdateIndex(String[] terms, int docId)
	{
		int numberOfTermsIndexed = 0;
		for(int i = 0; i<terms.length; i++)
		{
			String currentTerm = terms[i].trim();
			if(numberOfTermsIndexed > 8000)
			{
				break;
			}			
			if(currentTerm.length()<2 || currentTerm.length()>20 || stopWords.contains(currentTerm))
			{
				continue;
			}
			ArrayList<Integer> postingList;
			numberOfTermsIndexed++;
			this.currentIndexSize += (docId+"").length();
			if(!this.currentInvertedIndexContents.containsKey(currentTerm))
			{
				postingList = new ArrayList<Integer>();
				postingList.add(docId);
			}
			else
			{
				postingList = this.currentInvertedIndexContents.get(currentTerm);
				postingList.add(docId);
				int j = postingList.size()-1;
				while(j>=0 && postingList.get(j) > docId)
				{
					postingList.set(j+1,  postingList.get(j));
				}
				postingList.set(j, docId);
			}
			this.currentInvertedIndexContents.put(currentTerm, postingList);
		}
		if((this.currentIndexSize/(1024*1024)) > 170 )
		{
			WriteIndexToFile();
			this.currentInvertedIndexContents = new HashMap<String, ArrayList<Integer>>();
			this.currentIndexSize = 0;
		}
	}
	private void WriteIndexToFile()
	{
		try
		{
			String fileName = FileNames.indexFileName+this.currentIndexFileNumber+".txt";
			File indexFile = new File(fileName);
			if(!indexFile.exists())
			{
				indexFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile));
			GetIndexContentsAndWrite(writer);
			writer.close();
			this.currentIndexFileNumber++;
		}
		catch(Exception ex)
		{
			Logger.logMessages(ex.getMessage());
		}
	}
	private void GetIndexContentsAndWrite(BufferedWriter writer)
	{
		String term = "";
		try
		{		
			boolean addNewLine = false;
			String[] terms = this.currentInvertedIndexContents.keySet().toArray(new String[0]);
			Arrays.sort(terms);
			for(int i = 0; i < terms.length; i++)
			{
				term = terms[i];
				if(addNewLine)
				{
					writer.write("\n");
				}
				else
				{
					addNewLine = true;
				}
				
				ArrayList<Integer> postingList = this.currentInvertedIndexContents.get(term);
				writer.write(term+"#");
				boolean isFirstTerm = true;
				long currentDocId = postingList.get(0);
				int currentFreq = 1;
				long df = 0;
				StringBuffer currentList = new StringBuffer();
				for(int j = 0; j < postingList.size(); j++)
				{
					if(!isFirstTerm)
					{
						if(currentDocId == postingList.get(j))
						{
							currentFreq++;
						}
						else
						{
							df++;
							if(currentList.length() != 0)
							{
								currentList.append(",");
							}
							currentList.append(currentDocId+"-"+currentFreq);
							currentDocId = postingList.get(j);
							currentFreq = 1;
						}
						
					}
					else
					{
						df++;
						currentDocId = postingList.get(j);
						isFirstTerm = false;
					}
				}
				if(df > 1)
				{
				currentList.append(","+currentDocId+"-"+currentFreq);
				}
				else
				{
					currentList.append(currentDocId+"-"+currentFreq);
				}
				writer.write(df+"#"+currentList.toString());
			}	
		}
		catch(Exception ex)
		{
			Logger.logMessages("Create index content: term - "+term+" "+ex.getMessage());
		}
		
	}

	public void Process()
	{
		String docIdStr="";		
		try
		{
			Initialize();
			
			reader = new BufferedReader(new FileReader(FileNames.srcFile));
			
			String line = reader.readLine();
			while(line != null)
			{
				docIdStr = line.substring(6, line.length()-7);
				int docId = Integer.parseInt(docIdStr);
				line = reader.readLine();
				String contents = reader.readLine();
				line = reader.readLine();
				line = reader.readLine();
				
				contents = contents.replaceAll("[ ]+", " ");
				String[] terms = contents.split(" ");
				Arrays.sort(terms, String.CASE_INSENSITIVE_ORDER);
				UpdateIndex(terms, docId);
				
			}
			this.WriteIndexToFile();
			CloseFiles();
		}
		catch(Exception ex)
		{
			Logger.logMessages("Error in Process method "+docIdStr+" "+ex.getMessage());
		}
	}
	private void CloseFiles()
	{
		try
		{
			reader.close();
		}
		catch(Exception ex)
		{
			
		}
	}
	public static void main(String[] args)
	{
		Logger.logMessages("Starting index creation ");
		CreateIndex indexCreator = new CreateIndex();
	    indexCreator.Process();
		Logger.logMessages("Index creation complete ");
	}
}
