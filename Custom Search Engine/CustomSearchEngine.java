import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class CustomSearchEngine {
	Logger objLog=new Logger();
	private Scanner scanner;
	private String documentIndexFile;
	private double N;
	private TfIdf TfIdfObj;
	public CustomSearchEngine(double totalDocs) throws IOException
	{
		try
		{
			scanner = new Scanner(System.in);
			documentIndexFile = "D://PS3//docIdName.txt";
			N=totalDocs;
			TfIdfObj = new TfIdf(N);
		}
		catch(Exception ex)
		{
			objLog.logMessages(ex.getMessage());
		}
	}
	public HashMap<String,String> titleIndexMap() throws IOException{

		BufferedReader reader=new BufferedReader(new FileReader(new File(documentIndexFile)));
		HashMap<String,String> titleIndex=new HashMap<String,String>();
		try{
			String readLine="";
			while((readLine=reader.readLine())!=null){
				String[] 	arr=readLine.split("#");
				titleIndex.put(arr[1], arr[0]);
			}
			reader.close();
		}
		catch(Exception ex){
			objLog.logMessages(ex.getMessage());
		}
		return titleIndex;
	}
	public void StartSearchEngine() throws IOException
	{
		String userQuery;
		String doAnotherSearch;

		do
		{
			System.out.println("Please enter the search query");

			userQuery = scanner.nextLine();
			objLog.logMessages("Starting search");
			HashMap<String, Double> resultHash=TfIdfObj.getQueryResults(userQuery);
			ArrayList<String> searchResults = sortByComparator(resultHash);
			objLog.logMessages("Search complete");
			DisplaySearchResults(searchResults);

			System.out.println("Do you want to give a new query? (yes/no)");
			doAnotherSearch = scanner.nextLine();
		}while(doAnotherSearch.equalsIgnoreCase("yes"));
	}

	public ArrayList sortByComparator(Map<String,Double> unSortedHash){
		List list=new LinkedList(unSortedHash.entrySet());
		Collections.sort(list,new Comparator(){
			public int compare(Object o1,Object o2){
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		ArrayList sortedOutput=new ArrayList<>(sortedMap.keySet());
		for(Object key:sortedMap.keySet()){
			sortedOutput.add(key);
		}
			return sortedOutput;
			


	}
	public ArrayList<String> GetDocumentNamesFromIds(ArrayList<String> searchResults) throws IOException
	{
		ArrayList<String>ParsedDocument=new ArrayList<String>();
		for(int i=0;i<searchResults.size();i++){
			String output=SearchInFile(searchResults.get(i), documentIndexFile,"#");
			String[] arr=output.split(" ");
			if(arr.length==2){
				//System.out.println(arr[0]+","+arr[1]);
				if(arr[1]!=null || arr[1]!=""){
					ParsedDocument.add(arr[1]);
				}
			}

		}
		
		return ParsedDocument;
	}

	
	public String SearchInFile(String term,String fileName,String delimiter) throws IOException
	{
		String postingList = "";
		try
		{
			RandomAccessFile indexFile = new RandomAccessFile(fileName,"r");
			indexFile.seek(0);
			String line = indexFile.readLine();
			String currentTerm="";
			int index = line.indexOf(delimiter);
			currentTerm = line.substring(0,index);
			if(currentTerm!= "")
			{
				int cterm=    Integer.parseInt(currentTerm);
				int iterm=Integer.parseInt(term);
				if(cterm==iterm)
				{				
					//					objLog.logMessages("Loading posting list for '"+term+"' complete");

					indexFile.close();
					return line;
				}
				if(iterm<cterm)
				{
					indexFile.close();
					return "";
				}
				long start = 0;
				long end = indexFile.length();
				while(start<=end)
				{
					currentTerm = "";
					long mid = (start+end)/2;
					indexFile.seek(mid);
					indexFile.readLine();
					line = indexFile.readLine();
					int ind = line.indexOf(delimiter);
					currentTerm = line.substring(0,ind);
					cterm=Integer.parseInt(currentTerm);
					if(currentTerm != "")
					{
						if(iterm==cterm)
						{
							//objLog.logMessages("Loading posting list for '"+term+"' complete");
							indexFile.close();
							return line;

						}
						if(iterm<cterm)
						{
							end = mid-1;
						}
						if(iterm>cterm)
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
		}

		return postingList;
	}

	private void DisplaySearchResults(ArrayList<String> searchResults) throws IOException
	{
		String doYouWantToContinue;
		for(int i = 0; i < searchResults.size(); i++){
			String result = searchResults.get(i);
			System.out.println(result);
			if((i+1)%10 == 0)
			{
				System.out.println("");
				System.out.println("Do you want to see the next 10 results? (yes/no)");
				doYouWantToContinue = scanner.nextLine();
				if(doYouWantToContinue.compareToIgnoreCase("yes") != 0)
				{
					return;
				}
			}
		}
	}

}
