package CSVboard;

/**
 * TexAn 
 * Version 1.0 (prefinal beta) 
 * Yusuf Yurdagel, 2015
 *  
 * This is essential for 'Search' class. It removes search queries that are equal or duplicated, e.g.
 * if search query is: "+people -people -men women", then the result after a 'RemovDuplicatesFromString' is
 *  "-men women" because "+people and -people" compensate themselves  
**/
import java.util.ArrayList;
import java.util.HashSet;

public class RemoveDuplicateFromString {    

	public static String removeDuplicates(String string)  {
		String [] splitted = string.split("#");
		String [] andSplitted = new String[splitted.length]; 	    
		String [] notSplitted = new String[splitted.length]; 
		String [] orSplitted = new String[splitted.length];
		ArrayList<String> results = new ArrayList<>();
		
		// +- or -+ characters are invalid -> delete them in string 
		for (int i=0;i<splitted.length;i++) {
		   	if (splitted[i].contains("+-") || splitted[i].contains("-+")) splitted[i]="";
		}
		
		//split all +words in string and save into an array andSplitted 
		for (int i=0;i<splitted.length;i++) {
	    	if (splitted[i].contains("+")) andSplitted[i] = splitted[i].replaceAll("\\+","");
	    }
		
		//split all -words in string and save into an array notSplitted 
		for (int i=0;i<splitted.length;i++) {
		   	if (splitted[i].contains("-")) notSplitted[i] = splitted[i].replaceAll("\\-","");
		}
		
		//split all words in string and save into an array orSplitted 
		for (int i=0;i<splitted.length;i++) {
		   	if (!splitted[i].contains("-") && !splitted[i].contains("+")) orSplitted[i] = splitted[i];
		}
		
		//avoid null string arrays 
		for(int i=0;i<andSplitted.length;i++) if (andSplitted[i]==null) andSplitted[i]="NAV"; //'NAV' = 'Not A Value'
		for(int i=0;i<notSplitted.length;i++) if (notSplitted[i]==null) notSplitted[i]="NAV";
		for(int i=0;i<orSplitted.length;i++) if (orSplitted[i]==null) orSplitted[i]="NAV";

		for(int i=0;i<andSplitted.length;i++) {
			for(int k=0;k<notSplitted.length;k++) {
				String and=andSplitted[i];
				String not=notSplitted[k];
				int compare=and.compareTo(not);
				if  (compare==0) {
					andSplitted[i]="NAV";
					notSplitted[k]="NAV";
				} 
			}
		}
		
            for (String andSplitted1 : andSplitted) { 
                if (!"NAV".equals(andSplitted1)) {
                    results.add("+" + andSplitted1);
                }
            }
            for (String notSplitted1 : notSplitted) {
                if (!"NAV".equals(notSplitted1)) {
                    results.add("-" + notSplitted1);
                }
            }
            for (String orSplitted1 : orSplitted) {
                if (!"NAV".equals(orSplitted1)) {
                    results.add(orSplitted1);
                }
            }

		HashSet<String> tmpResults = new HashSet<>();
		tmpResults.addAll(results);
		results.clear();
		results.addAll(tmpResults);
		
		String text="";
            for (String result : results) {
                text = text+" " + result;
            }
		
		return text;
	}	
}