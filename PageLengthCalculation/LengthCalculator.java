import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;


public class LengthCalculator {
int totalWords[] = new int[Constants.N];
int numNonStopWords[] = new int[Constants.N];
HashSet<String> stopWords = new HashSet<String>();
BufferedReader reader;
BufferedWriter docIdName, parsedContent;
boolean debugFlag = false;
int[] skippedDocs;
private void PopulateSkippedDocs()
{
	try
	{
		BufferedReader skippedDocsReader = new BufferedReader(new FileReader(FileNames.docIdsToSkip));
		String skippedId = skippedDocsReader.readLine();
		int i = 0;
		this.skippedDocs = new int[Constants.numSkippedDocs];
		while(skippedId != null)
		{
			int id = Integer.parseInt(skippedId);
			this.skippedDocs[i] = id;
			skippedId = skippedDocsReader.readLine();
			i++;
		}
		skippedDocsReader.close();
	}
	catch(Exception ex)
	{
		
	}
}
public void Initialize()
{
	try
	{
		reader = new BufferedReader(new FileReader(FileNames.srcFile));
		docIdName = new BufferedWriter(new FileWriter(FileNames.docIdNameFile));
		parsedContent = new BufferedWriter(new FileWriter(FileNames.parsedFileContents));
		stopWords.add("the");
		stopWords.add("a");
		stopWords.add("in");
		stopWords.add("on");
		stopWords.add("to");
		stopWords.add("of");
		stopWords.add("are");
		stopWords.add("is");
		stopWords.add("or");
		stopWords.add("and");
		stopWords.add("etc");
		stopWords.add("with");
		stopWords.add("most");
		stopWords.add("this");
		stopWords.add("that");
		stopWords.add("www");
		stopWords.add("http");
		stopWords.add("if");
		stopWords.add("into");
		stopWords.add("there");
		PopulateSkippedDocs();
	}
	catch(Exception ex)
	{
		Logger.logMessages("Error in Initialize " +ex.getMessage());
	}
}
private boolean ProcessNextPage()
{
	String docIdStr ="";
	String body="";
	try
	{
		docIdStr = reader.readLine();
		if(docIdStr == null)
		{
			reader.close();
			return false;
		}
		if(docIdStr.contains("4540302"))
		{
			debugFlag = true;
		}
		while(docIdStr.equals(""))
		{
			docIdStr = reader.readLine();
		}
		docIdStr = reader.readLine();
		System.out.println(docIdStr);
		int ind = docIdStr.indexOf("</$id>");
		String title = docIdStr.substring(ind+14, docIdStr.length()-9);
		docIdStr = docIdStr.substring(5, ind);
		int docId = Integer.parseInt(docIdStr);
		
		StringBuffer bodyContents = new StringBuffer();
		
		body = reader.readLine();
		while(body != null)
		{
			bodyContents.append(body+" ");
			if(!body.endsWith("</$text>"))
			{
				body = reader.readLine();
			}
			else
			{
				break;
			}
		}
		body = bodyContents.toString();
		
		body = body.substring(7, body.length()-8);
		
		this.docIdName.write(docId+" "+title+"\n");
		if(!ShouldSkipDoc(docId))
		{
			body = this.ProcessPageContent(body);
			this.parsedContent.write("<$id$>"+docId+"</$id$>\n<$text$>\n"+body+"\n</$text$>\n");
			
		}
		reader.readLine();
		int numNonStopWords = 0;
		String[] words = body.toString().split(" ");
		for(int i = 0; i<words.length; i++)
		{
			if(!this.stopWords.contains(words[i]))
			{
				numNonStopWords++;
			}
		}
		this.totalWords[docId] = words.length;
		this.numNonStopWords[docId] = numNonStopWords;
	}
	catch(Exception ex)
	{ 
		Logger.logMessages("Error in process next page "+ex.getMessage()+" :------------"+docIdStr+" -----------"+body);
		return false;
	}
	return true;
}
private boolean SearchForDocId(int start, int end, int docId)
{
	if(start == end)
	{
		return skippedDocs[start] == docId;
	}
	if(skippedDocs[start]>docId || skippedDocs[end]<docId)
	{
		return false;
	}
	int mid = (start+end)/2;
	if(skippedDocs[mid] == docId)
	{
		return true;
	}
	if(skippedDocs[mid] > docId)
	{
		return SearchForDocId(start, mid-1, docId);
	}
	return SearchForDocId(mid+1, end, docId);
}
private boolean ShouldSkipDoc(int docId)
{
	return SearchForDocId(0,Constants.numSkippedDocs-1, docId);
}
private void CloseFiles()
{
	try
	{
	this.docIdName.close();
	this.parsedContent.close();
	}
	catch(Exception ex)
	{
		Logger.logMessages("Exception in closing files "+ex.getMessage());
	}
}
private void WriteDocLength()
{
	int i = 0;
	try
	{
		StringBuffer s = new StringBuffer();
		BufferedWriter writer = new BufferedWriter(new FileWriter(FileNames.outputFile));
		for(i = 0; i<Constants.N; i++)
		{
			float ratio = this.numNonStopWords[i] != 0?this.totalWords[i]/this.numNonStopWords[i]:0;
			writer.write(i+ " "+this.totalWords[i]+" "+this.numNonStopWords[i]+" "+ratio+"\n");
		}
		writer.close();
	}
	catch(Exception ex)
	{
		Logger.logMessages("Error in WriteDocLength "+ex.getMessage());
	}
}
public void Process() 
{
	Logger.logMessages("Starting process");
	this.Initialize();
	while(this.ProcessNextPage())
	{
		;
	}
	this.WriteDocLength();
	this.CloseFiles();
	Logger.logMessages("Process completed");
}

private String ProcessPageContent(String text)
{
	text = text.replaceAll("&ndash;", " ");
	text = text.replaceAll("[=][=][=].*?[=][=][=]", ""); 
    text = text.replaceAll("[=][=].*?[=][=]", ""); 
    text = text.replaceAll("[=\\-+#()|\"'.,;:\\*\\?!_\\$]", " "); 
    
	text = Process(text);
	
	text = text.replaceAll("(?s)[&][l][t][;][r][e][f].*?[&][g][t][;].*?[&][l][t][;][/][r][e][f].*?[&][g][t][;]", ""); 
    text = text.replaceAll("(?s)[<][r][e][f].*?[>].*?[<][/][r][e][f].*?[>]", ""); 
    text = text.replaceAll("(?s)[&][l][t][;][r][e][f].*?[/][&][g][t][;]", ""); 
    text = text.replaceAll("(?s)[<][r][e][f].*?[>].*?[<][/][>]", ""); 
    text = text.replaceAll("[\\d]+", " ");
    text = text.replaceAll("-", " ");
	
    text = text.replaceAll("\\/", ""); 
    text = text.replaceAll("([\\[][\\[])|([\\]][\\]])|([{][{])|([}][}])", " "); 
    text = text.replaceAll("[\\[].*?[\\]]", ""); 
    text = text.replaceAll("&nbsp", ""); 
    text = text.replaceAll("[<>!]",""); 
    text = text.replaceAll("&lt", " ");
    text = text.replaceAll("&gt", " ");
    
    text = text.replaceAll("[^a-z0-9]", " ");
    text = text.replaceAll("[ ]+", " ");
	return text.toLowerCase();
}
public String Process(String text)
{
	StringBuffer buf = new StringBuffer();
	int i = 0;
	boolean skip = false;
	int length = text.length();
	int nesting = 0;
	while(i<length)
	{
		if(text.charAt(i) == '{' && i+1 < length && text.charAt(i+1) == '{' )
		{
			nesting++;
		}
		if(text.charAt(i) == '}' && i+1 < length && text.charAt(i+1) == '}')
		{
			nesting--;
		}
		if(!skip)
		{
			if(text.charAt(i) == '{' && i+1 < length && text.charAt(i+1) == '{' )
			{
				skip = true;
				i +=2;
			}
			else
			{
				buf.append(text.charAt(i));
				i++;
			}
			
		}
		else
		{
		    if(text.charAt(i) == '}' && i+1 < length && text.charAt(i+1) == '}')
		    {
		    	if(nesting == 0)
		    	{
			    	skip = false;
		    	}
			    i = i+2;
		    	
		    }
		    else
		    {
		    	i++;
		    }
		}	
	}
	return buf.toString();
}

}
