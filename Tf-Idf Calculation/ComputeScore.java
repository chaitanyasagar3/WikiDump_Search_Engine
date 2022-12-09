import java.io.IOException;
import java.util.HashMap;


public class ComputeScore {

	public HashMap<String,Double> computeTFIDF(String postingList,Double N) throws IOException{
		CustomSearchEngine objCustom=new CustomSearchEngine(N);
		String documentIndexFile="D://PS3//docIdName.txt";
		int startIndex=postingList.indexOf('#');
		String term=postingList.substring(0,startIndex);
		int endIndex=postingList.indexOf('#',startIndex+1);	
		int docFreq=Integer.parseInt(postingList.substring(startIndex+1,endIndex));
		Double InverseDocFreq=Math.log10(N/docFreq);
		String substring=postingList.substring(endIndex+1,postingList.length());
		String[] docIds=substring.split(",");
		HashMap<String,Double> tfIDF=new HashMap<String,Double>(docIds.length);
		String []outp=new String[2];
		for(int i=0;i<docIds.length;i++){
			String []subdocIds=docIds[i].split("-");
			String output="";
			if(Double.parseDouble(subdocIds[1])>0){
				double val=Double.parseDouble(subdocIds[1]);
				val=1+Math.log10(val);
				val=val*InverseDocFreq;
				output=objCustom.SearchInFile(subdocIds[0], documentIndexFile,"#");
				if(output!=""){
					outp=output.split("#");
					if(output.contains(term)){
						val=val+0.8;    //present in title
					}
					else{
						val=val+0.2;    //present in body
					}

					//tfIDF.put(subdocIds[0], val);
					tfIDF.put(outp[1],val);
				}
				else{
					tfIDF.put(outp[1], Double.parseDouble("0"));
					//tfIDF.put(subdocIds[0], Double.parseDouble("0"));
				}
			}
		}

		return tfIDF;
	}
	public HashMap<String,Double> computeWeight(HashMap<String,Double> hash0,HashMap<String,Double> hash1){

		for(String key:hash1.keySet()){
			if(hash0.containsKey(key)){
				Double value=hash0.get(key);
				Double value1=hash1.get(key);
				Double val=value+value1;
				hash0.put(key, val);
			}
			else{
				hash0.put(key, hash1.get(key));
			}
		}
		return hash0;
	}

}
