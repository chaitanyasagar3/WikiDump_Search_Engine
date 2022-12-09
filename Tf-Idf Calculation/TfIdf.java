
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TfIdf {
	double totalDoc;
	Logger objLog=new Logger();
	long[] fileSeekPositions;
	ComputeScore objCompute=new ComputeScore();
	HashSet<String> stopWords = new HashSet<String>();
	public TfIdf(double N){
		totalDoc=N;
		fileSeekPositions = new long[27];
		PopulateFileSeekPositions();
		PopulateStopWords();
	}
		private void PopulateStopWords()
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
	private void PopulateFileSeekPositions()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("D:/PS3/Assignment3_Srividya/fileSeekPos"));
			String line = reader.readLine();
			int i = 0;
			while(line != null)
			{
				fileSeekPositions[i] = Long.parseLong(line);
				line = reader.readLine();
				i++;
			}
			reader.close();
		}
		catch(Exception ex)
		{

			//objLog.logMessages(ex.getMessage());
		}
	}
	private String[] GetQueryTerms(String userQuery)
	{
		userQuery.replaceAll("[^a-z0-9]"," ");
		userQuery.replaceAll("[ ]+", " ");
		String[] terms = userQuery.split(" ");
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i<terms.length; i++)
		{
			if(!stopWords.contains(terms[i]))
			{
				result.add(terms[i]);
			}
		}
		return result.toArray(new String[0]);
	}
	public String SearchInFile(String term, String fileName)
	{
		String postingListInfo = "";
		
		try
		{
			objLog.logMessages("loading posting list for term: "+term);
			int seekInd = term.charAt(0)-97;
			long start = fileSeekPositions[seekInd];
			long end = fileSeekPositions[seekInd+1];
			int chunkSize = 102400000;//100 mb
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			while(start <= end)
			{
				int bytesToRead = (int) (end-start);
				if(chunkSize < bytesToRead)
				{
					bytesToRead = chunkSize;					
				}
				byte[] buffer = new  byte[bytesToRead];
				file.seek(start);
				file.read(buffer);
				String text = new String(buffer,"UTF-8");
				text = text.concat(file.readLine());
				postingListInfo = ExtractPostingList(text, term);
				if(postingListInfo!= "")
				{
					objLog.logMessages("loading posting list for term: "+term+" complete");
					return postingListInfo;
				}
				start += text.length();
			}
		file.close();
		}
		catch(Exception ex)
		{
			
		}
		return postingListInfo;
	}
	
	
	public String SearchInFile(String term, String fileName,String delimiter) throws IOException
	{
		String postingList = "";
		try
		{
			objLog.logMessages("Loading posting list for '"+term+"'");
			RandomAccessFile indexFile = new RandomAccessFile(fileName,"r");
			int seekInd = term.charAt(0) - 97;
			long start = fileSeekPositions[seekInd];
			long end = fileSeekPositions[seekInd+1];

			indexFile.seek(start);
			String line = indexFile.readLine();
			String currentTerm="";
			int index = line.indexOf(delimiter);
			currentTerm = line.substring(0,index);
			if(currentTerm!= "")
			{

				if(currentTerm.compareTo(term)==0)
				{				
					objLog.logMessages("Loading posting list for '"+term+"' complete");

					indexFile.close();
					return line;
				}
				if(currentTerm.compareTo(term)>0)
				{
					indexFile.close();
					return "";
				}
				while(start<=end)
				{
					currentTerm = "";
					long mid = (start/2 +end/2);
					indexFile.seek(mid);
					indexFile.readLine();
					line = indexFile.readLine();
					int ind = line.indexOf(delimiter);
					currentTerm = line.substring(0,ind);
					if(currentTerm != "")
					{
						if(currentTerm.compareTo(term) == 0)
						{
							objLog.logMessages("Loading posting list for '"+term+"' complete");
							indexFile.close();
							return line;

						}
						if(currentTerm.compareTo(term)>0)
						{
							end = mid-1;
						}
						if(currentTerm.compareTo(term)<0)
						{
							start = mid+1;
						}
					}
					else
					{
						indexFile.close();
						return "";
					}
				}
			}
			indexFile.close();
		}
		catch(Exception ex)
		{
			objLog.logMessages(ex.getMessage());
		}

		return postingList;
	}
	private String ExtractPostingList(String text, String term)
	{
		int index= text.indexOf("\n"+term+"#");
		if(index == -1)
		{
			return "";
		}
		int endIndex = text.indexOf("\n",index+2);
		return text.substring(index+1, endIndex);
	}
	public HashMap<String, Double> getQueryResults(String input) throws IOException{
		input=input.toLowerCase();
		String[] terms=input.split(" ");
		HashMap<String,String> termFile=new HashMap<String,String>(terms.length);
		HashMap<String,HashMap<String,Double>> TFIDF=new HashMap<String,HashMap<String,Double>>();
		for(int i=0;i<terms.length;i++){
			char val=terms[i].charAt(0);
			int num=(int)val;
			termFile.put(terms[i], findFilesNames(num));
		}
		for(String key:termFile.keySet()){
			//String List=SearchInFile(key,termFile.get(key),"#");
			String List = SearchInFile(key,termFile.get(key));
			if(List!=""){
				TFIDF.put(key, objCompute.computeTFIDF(List, totalDoc));
			}	
		}
		HashMap<String,Double> hash0=TFIDF.get(terms[0]);
		HashMap<String,Double> Collectivehash=new HashMap<String,Double>();
		if(terms.length==1){
			return hash0;
		}
		else{
			
			for(int l=1;l<terms.length;l++){
				if(TFIDF.containsKey(terms[l])){
					Collectivehash.putAll(objCompute.computeWeight(hash0,TFIDF.get(terms[l])));

				}
			}
		}

		return Collectivehash;
	}


	public String findFilesNames(int val) throws IOException{
		String filename="";

		try{
			if(val>=97 && val<=103  ){

				filename="D:/PS3/indexFiles/a-g.txt";
			}
			if(val>=104 && val<=110){
				filename="D:/PS3/indexFiles/h-n.txt";

			}
			if(val>=111 && val<=117){
				filename="D:/PS3/indexFiles/o-u.txt";

			}
			if(val>=118 && val<=122){
				filename="D:/PS3/indexFiles/v-z.txt";

			}
			if((val>=32 && val<=64) ||(val>=123 && val<=126) ){
				filename="D:/PS3/indexFiles/SpecialChar.txt";
			}
		}
		catch(Exception ex){
			objLog.logMessages(ex.getMessage());
		}
		return filename;
	}


}

