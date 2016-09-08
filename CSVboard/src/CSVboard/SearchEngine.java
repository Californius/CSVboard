package CSVboard;

/**
 * TexAn 
 * Version 1.0 (prefinal beta) 
 * Yusuf Yurdagel, 2015
 *  
 * The Search engine is based on a boolean logic with 'AND', 'OR' and 'NOT' connectives. 
 * AND connectives search words are designated with '+' symbols, 'NOT' connectives with '-' symbols and 
 * 'OR' connectives without any symbols. Further, I implemented an 'exact word search' algorithm to find 
 * exact words that is queried by the user, e.g. typing '+"hello world"' (including the quotation marks) 
 * searches that exact sentence through the hashmap notes. But I deactivated this function to keep my 
 * application more compact and easy to maintain.
 *    
**/
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;

public class SearchEngine {
	
	ArrayList<String> cleanedSearchItems;
	
	//put each found and-connective value to a hashmap until all search queries in 'list' are checked 
	private LinkedHashMap<Integer, String> getAndResults(LinkedHashMap<Integer, String> hm, LinkedHashMap<Integer, String> list) {
		LinkedHashMap<Integer, String> result = new LinkedHashMap<>();
                hm.keySet().stream().filter((key) -> (andConnectiveCheck(hm.get(key), list))).forEach((key) -> {
                    result.put(key, hm.get(key));
            });		
		return result;
	}

	//check each hashmap value with the search query. If hashmap value contains the queried value then set flag=true	
	private boolean orConnectiveSearch(String text, LinkedHashMap<Integer, String> searchList) {		
            text = text.toLowerCase();
            //OR connective		
            for(int k=0;k<searchList.size();k++) {     
                Iterator<Map.Entry<Integer, String>> counter = searchList.entrySet().iterator();
                while(counter.hasNext()) {
                    Map.Entry<Integer, String> entry = counter.next();
                    String value = entry.getValue();	
                    if (text.contains(value)) return true; 
                }						
            }		
            return false;
	}

        //check each hashmap value with the search query. If hashmap value contains the queried value then set flag=true	
	private boolean andConnectiveCheck(String searchQuery, LinkedHashMap<Integer, String> searchList) {					
            boolean flag=true;		
            searchQuery = searchQuery.toLowerCase();
            for(int k=0;k<searchList.size();k++) {     
                Iterator<Map.Entry<Integer, String>> counter = searchList.entrySet().iterator();
                while(counter.hasNext()) {
                    Map.Entry<Integer, String> entry = counter.next();
                    String value = entry.getValue().toLowerCase();		
                    if(!searchQuery.contains(value)) return false; else flag=true; 
                }
            } 
            return flag;
	}
        
	//put each found or-connective value to a hashmap until all search queries in 'list' are checked 
	private LinkedHashMap<Integer, String> getOrResults(LinkedHashMap<Integer, String> hm1, LinkedHashMap<Integer, String> list, LinkedHashMap<Integer, String> hm) {		
		LinkedHashMap<Integer, String> results = new LinkedHashMap<>();
		Iterator<Map.Entry<Integer, String>> iterator = hm.entrySet().iterator();
                hm1.keySet().stream().forEach((key1) -> {
                    Map.Entry<Integer, String> entry = iterator.next();
                    if (orConnectiveSearch(hm1.get(key1), list)) {
                        String value = entry.getValue();
                        int key = entry.getKey();
                        results.put(key, value);
                    }
                });
            return results;
	}
	
	private LinkedHashMap<Integer, String> getOrResults(LinkedHashMap<Integer, String> hm, LinkedHashMap<Integer, String> list) {		
		LinkedHashMap<Integer, String> results = new LinkedHashMap<>();
                hm.keySet().stream().filter((key) -> (orConnectiveSearch(hm.get(key), list))).forEach((key) -> {
                    results.put(key, hm.get(key));
            });		
		return results;
	}

	//check each hashmap value with the search query. If hashmap value doesn't contain the queried value then set flag=true	
	private boolean notConnectiveSearch(String text, LinkedHashMap<Integer, String> searchList) {
		boolean flag=false;		
		text = text.toLowerCase();
		//NOT connective		
		for(int k=0;k<searchList.size();k++) {     
			Iterator<Map.Entry<Integer, String>> counter = searchList.entrySet().iterator();
			while(counter.hasNext()) {
				Map.Entry<Integer, String> entry = counter.next();
				String value = entry.getValue().toLowerCase();	
				if (!text.contains(value.toLowerCase()))  {
					flag=true;
					break;
				} else flag=false;			
			}
		}
		return flag;
	}

	//put each found not-connective value to a hashmap until all search queries in 'list' are checked 
	private LinkedHashMap<Integer, String> getNotResults(LinkedHashMap<Integer, String> hm, LinkedHashMap<Integer, String> list) {		
			LinkedHashMap<Integer, String> results = new LinkedHashMap<>();
                        hm.keySet().stream().filter((key) -> (notConnectiveSearch(hm.get(key), list))).forEach((key) -> {
                            results.put(key, hm.get(key));
            });		
			return results;
		}

	
	/* this method is important if you handle with more one and-connective search item, for example:
	 * +man +society +people
	 * It puts only those values that contains all of the queried and-connective words 'man', 'society' and 'people' 
	 */
	private LinkedHashMap<Integer, String> getFinalANDResults(LinkedHashMap<Integer, String> hm, String [] searchItems) {		
		LinkedHashMap<Integer, String> tmp = new LinkedHashMap<>();				

			Iterator<Map.Entry<Integer, String>> counter = hm.entrySet().iterator();			
			while(counter.hasNext()) {
				boolean flag=true;
				int key=0;
				String text="";
				Map.Entry<Integer, String> entry = counter.next();				
				for(int i=0;i<searchItems.length;i++) {
					searchItems[i] = searchItems[i].replaceAll("\\+", "");
					String pattern = "\\b"+searchItems[i]+"\\b"; //regex: pattern for whole word search 									
					text = entry.getValue();
					key = entry.getKey();
					flag=flag&&text.matches("(.*)"+pattern+"(.*)"); //by using the &&, result will be true only if text matches all patterns.
				} if (flag) tmp.put(key, text); 
 	
			}	
		return tmp;
	}
	
	/* to get final not results, first of all, all elements in 'invertedNotResults' have to be filtered out. 
	 * After, the results will be putted into
	 * the hashmap of notresults
	 */
	private LinkedHashMap<Integer, String> filterInvertedNOTsesults(LinkedHashMap<Integer, String> hm, String [] searchItems) {		
		LinkedHashMap<Integer, String> tmp = new LinkedHashMap<>();		 

		//initialization
		int key=0;
		String text="";
		boolean flag=true;
		ArrayList<Boolean> deleteFlag = new ArrayList<>();
		
		Iterator<Map.Entry<Integer, String>> counter1 = hm.entrySet().iterator();
		while(counter1.hasNext()) {
			flag=true;
			Map.Entry<Integer, String> entry = counter1.next();
			
			for(int i=0;i<searchItems.length;i++) {					
				searchItems[i] = searchItems[i].replaceAll("\\-", "");
				key = entry.getKey();
				String pattern = "\\b"+searchItems[i]+"\\b"; //regex: pattern for whole word search
				text = entry.getValue();
				flag=flag&&text.matches("(.*)"+pattern+"(.*)"); //by using the &&, result will be true only if text matches all patterns.				
			}		
			
			if (flag) {
				deleteFlag.add(true);
				tmp.put(key, text);
			} 
			
		} 	
                deleteFlag.stream().filter((deleteFlag1) -> (deleteFlag1)).map((_item) -> tmp.entrySet().iterator()).forEach((counter2) -> {
                    while(counter2.hasNext()) {
                        Map.Entry<Integer, String> entry = counter2.next();
                        hm.remove(entry.getKey());
                    }
            });
	return hm;
	}

//	delete whitespaces between operator and word
	private LinkedHashMap<Integer, String> cleanString(String text) {
		
		//delete all white spaces between +,- and " characters and der search items 
		while(text.contains("+ ") || text.contains("- ") || text.contains("~ ")) {			
			text = text.replaceAll("\\+ ", "+");			
			text = text.replaceAll("- ", "-");
			text = text.replaceAll("~ ", "~");
			text = text.replaceAll("\"  ", "\"");
			text = text.replaceAll("\\++", "+");
			text = text.replaceAll("\\--", "-");
		}			
			text = text.trim();
			text = text.replaceAll("\\+", "#+");
			text = text.replaceAll("\\-", "#-");
			text = text.replaceAll(" ", "#");
			text = text.replaceAll("\"  ", "#\"");		
			text = text.replaceAll("[\u0600-\u061f\u064B-\u06EF]", "");
			text = RemoveDuplicateFromString.removeDuplicates(text);
                    Pattern p = Pattern.compile("(\\+[äöüÄÖÜßµ€$@.,:*\"\\w]++|\\-[äöüÄÖÜßµ€$@.,:*\"\\w]++|\\[äöüÄÖÜßµ€$@.,:*\"\\w]++|[äöüÄÖÜßµ€$@.,:*\"\\w]++)");
                    Matcher m = p.matcher(text);

        List<String> lst = new ArrayList<>();
        while(m.find()) {
            lst.add(m.group(1));
        }

        LinkedHashMap<Integer, String> results= new LinkedHashMap<>();
        for(int i=0;i<lst.size();i++) {
        	results.put(i, lst.get(i));   
        }
            return results;		
	}
	
	//compares two hashmaps for euqal values and puts them to a new hashmap 'hm3' 
	private LinkedHashMap<Integer, String> getDuplicates(LinkedHashMap<Integer, String> hm1, LinkedHashMap<Integer, String> hm2) {
		LinkedHashMap<Integer, String> hm3 = new LinkedHashMap<>();

		Iterator<Map.Entry<Integer, String>> counter1 = hm1.entrySet().iterator();
		while(counter1.hasNext()) {
			Map.Entry<Integer, String> entry1 = counter1.next();
			Iterator<Map.Entry<Integer, String>> counter2 = hm2.entrySet().iterator();
			while(counter2.hasNext()) {
				Map.Entry<Integer, String> entry2 = counter2.next();
				if (entry1.equals(entry2)) {
					hm3.put(entry1.getKey(), entry1.getValue());
				}
			}
		}				
		return hm3;
	}	
	
	//similar to getDuplicates: compares two hashmaps and deletes duplicated entries
	private LinkedHashMap<Integer, String> removeEqualHashMapEntries(LinkedHashMap<Integer, String> hm1, LinkedHashMap<Integer, String> hm2) {
		LinkedHashMap<Integer, String> hm3 = hm1;
		ArrayList<Integer> index2BeRemoved = new ArrayList<>();

		Iterator<Map.Entry<Integer, String>> counter1 = hm1.entrySet().iterator();
		while(counter1.hasNext()) {
			Map.Entry<Integer, String> entry1 = counter1.next();
			Iterator<Map.Entry<Integer, String>> counter2 = hm2.entrySet().iterator();
			while(counter2.hasNext()) {
				Map.Entry<Integer, String> entry2 = counter2.next();
				if (entry1.equals(entry2)) {
					index2BeRemoved.add(entry1.getKey());
				} 
			}
		}				
                index2BeRemoved.stream().forEach((index2BeRemoved1) -> {
                    hm3.remove(index2BeRemoved1);
            });
		return hm3;
	}

	//recognizes wether the search queries are 'OR', 'AND', 'NOT' connectives	
	private LinkedHashMap<Integer, String> getConnectiveWords(HashMap<Integer, String> cleanedString, int operator) {		
			LinkedHashMap<Integer, String> tmp = new LinkedHashMap<>();	
			switch(operator) {
	//		1: AND connective
			case 1: for(int i=0;i<cleanedString.size();i++) {
						if(cleanedString.get(i).contains("+") && !cleanedString.get(i).contains("\"")) {
							String text=cleanedString.get(i).replaceAll("\\+", "");
							tmp.put(i, text);
						}					
					}
					return tmp;
	//		2: NOT connective
			case 2: for(int i=0;i<cleanedString.size();i++) {
						if(cleanedString.get(i).contains("-")  && !cleanedString.get(i).contains("\"")) {
							tmp.put(i, cleanedString.get(i).replaceAll("-", ""));
						}					
					}
					return tmp;
	//		3: exact word search		
			case 3: for(int i=0;i<cleanedString.size();i++) {
						if(cleanedString.get(i).contains("\"")) {
							tmp.put(i, cleanedString.get(i).replaceAll("\"", ""));
						}			
					}					
					return tmp;
	//		default: OR connective
			default: for(int i=0;i<cleanedString.size();i++) {
                                    if(!cleanedString.get(i).contains("+") && !cleanedString.get(i).contains("-") && !cleanedString.get(i).contains("\"")) {						
                                            tmp.put(i, cleanedString.get(i).replaceAll("~", ""));
                                    }
                             }
                            return tmp;		
			}			
		}
	
	/* Depending on the search query 'getSearchOperation' sets the right search operation algorithm
	 * Example: '+london +europe -world' the search query contains AND-connectives and NOT-connectives but not OR-connectives.
	 * Thus, while 'andConnectiveWord' words and 'notConnectiveWords' can't be empty the hashmap 'orConnectiveWords' is empty.
	 * Furthermore, as mentioned before, exactWords are not used in this application so that we get the 13. case (doOperation=13) 
	 */
	private int getSearchOperation(LinkedHashMap<Integer, String> andConnectiveWords, LinkedHashMap<Integer, String> notConnectiveWords, LinkedHashMap<Integer, String> orConnectiveWords, LinkedHashMap<Integer, String> exactWords) {
		int doOperation=0;		
		if (  andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 1;//  
		if (  andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 2;// 
		if (  andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 3;// 
		if (  andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 4; 
		if (  andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 5;
		if (  andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 6;
		if (  andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 7;
		if (  andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 8;		
		if ( !andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 9;  
		if ( !andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 10; 
		if ( !andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 11; 
		if ( !andConnectiveWords.isEmpty() &&   notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 12;
		if ( !andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 13;
		if ( !andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() &&  orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 14;
		if ( !andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() &&  exactWords.isEmpty()) doOperation = 15;
		if ( !andConnectiveWords.isEmpty() &&  !notConnectiveWords.isEmpty() && !orConnectiveWords.isEmpty() && !exactWords.isEmpty()) doOperation = 16;
                return doOperation;
	}
	
	/* after all whitespaces are deleted and invalid queries are moved ('cleaned String') put 
	 * each search query to an arraylist	  
	 */	
	private ArrayList<String> getCleanedStringItems(LinkedHashMap<Integer, String> hm) {
		ArrayList<String> arrayList = new ArrayList<>();
		Iterator<Map.Entry<Integer, String>> counter = hm.entrySet().iterator();
		while(counter.hasNext()) {
			Map.Entry<Integer, String> entry = counter.next();
			String tmp = entry.getValue();
			arrayList.add(tmp);			
		}				
		return arrayList;
	}

        private LinkedHashMap<Integer, String> convert2Hashmap(ObservableList<CsvData> data, int col) {
            LinkedHashMap<Integer, String> hm = new LinkedHashMap<>();
            for(int i=0;i<data.size();i++) {
                hm.put(i, data.get(i).getDataValue(col, i));
            }
            return hm;
        }
	/* this is the main search operator. It uses the methods in this class, evaluates the values in hashmaps
	 * and finally returns the search result as 'searchResults'
	 * 
	 */
	public LinkedHashMap<Integer, String> searchEvaluation(String searchQuery, LinkedHashMap<Integer, String> rawData) {
		int searchOperation = 0;
                LinkedHashMap<Integer, String> hm = rawData; //here
		LinkedHashMap<Integer, String> hm1 = rawData; //here
	 	LinkedHashMap<Integer, String> connectiveWords = cleanString(searchQuery);		
		LinkedHashMap<Integer, String> andConnectiveWords = getConnectiveWords(connectiveWords, 1);
		LinkedHashMap<Integer, String> notConnectiveWords = getConnectiveWords(connectiveWords, 2);
		LinkedHashMap<Integer, String> orConnectiveWords = getConnectiveWords(connectiveWords, 0);
		LinkedHashMap<Integer, String> exactWords = getConnectiveWords(connectiveWords, 3);		
		LinkedHashMap<Integer, String> searchResults = new LinkedHashMap<>();		
		
		searchOperation = getSearchOperation(andConnectiveWords, notConnectiveWords, orConnectiveWords, exactWords);		
                System.out.println("searchoperation >> "+searchOperation);
		switch(searchOperation) {
			
			case 1:  //show all ayahs if search field is empty
                                         System.out.println("Search.searchEvaluation->case=1");
					 searchResults = hm; 
					 break;
			case 2:  //only exact word search items will be searched
        				 System.out.println("Search.searchEvaluation->case=2");
					 LinkedHashMap<Integer, String> tmp2 = evaluateWholeWordSearchItems(hm, exactWords);
					 searchResults = tmp2;
					 break;
			case 3:	 //search for or connective words
				         System.out.println("Search.searchEvaluation->case=3");
					 LinkedHashMap<Integer, String> tmp3 = getOrResults(hm, orConnectiveWords);
					 searchResults = tmp3;
					 break;
			case 4:  //search for or connective words in EXACT word search results
				         System.out.println("Search.searchEvaluation->case=4");
					 LinkedHashMap<Integer, String> tmp4 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> orResults4 = getOrResults(tmp4, orConnectiveWords, hm);
					 orResults4.putAll(tmp4);
					 searchResults = orResults4;
					 break;		
			case 5:  //search for not connective words
                                       System.out.println("Search.searchEvaluation->case=5");
					 LinkedHashMap<Integer, String> tmp5 = getNotResults(hm, notConnectiveWords);
					 searchResults = tmp5;
					 break;
			case 6:  //search for NOT words and EXACT words
				         System.out.println("Search.searchEvaluation->case=6");
					 LinkedHashMap<Integer, String> tmp6 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> notResults6 = getNotResults(tmp6, notConnectiveWords);					 
					 searchResults = notResults6; 
					 break;
			case 7:  //search for NOT and OR words
                                       System.out.println("Search.searchEvaluation->case=7");
					 LinkedHashMap<Integer, String> notResults7 = getNotResults(hm, notConnectiveWords);
					 LinkedHashMap<Integer, String> tmp7 = getOrResults(notResults7, orConnectiveWords);
					 searchResults = tmp7;
					 break;
			case 8:  // search for NOT and OR and EXACT words 
				         System.out.println("Search.searchEvaluation->case=8");
					 LinkedHashMap<Integer, String> exactWords8 = evaluateWholeWordSearchItems(hm, exactWords);					 
					 LinkedHashMap<Integer, String> inverted8 = getInvertedResults(hm, exactWords);					 
					 LinkedHashMap<Integer, String> notResults8 = getNotResults(exactWords8, notConnectiveWords);
					 LinkedHashMap<Integer, String> tmp8 = getNotResults(inverted8, notConnectiveWords);
					 notResults8.putAll(tmp8);
					 LinkedHashMap<Integer, String> orResults8 = getOrResults(notResults8, orConnectiveWords, hm);
					 orResults8.putAll(notResults8);
					 searchResults = orResults8;
					 break; //not well sorted!
			case 9:  //search for AND words
                                       System.out.println("Search.searchEvaluation->case=9");
					 LinkedHashMap<Integer, String> andResults9 = getAndResults(hm, andConnectiveWords);
//					 noteResults.getSearchedPositions(andResults9);
					 searchResults = andResults9;    						 
					 break;
			case 10: //search for AND and EXACT words
                                       System.out.println("Search.searchEvaluation->case=10");
					 LinkedHashMap<Integer, String> exactWords10 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> andResults10 = getAndResults(exactWords10, andConnectiveWords);
					 searchResults = andResults10;
					 break;
			case 11: //search for AND and OR words
                                       System.out.println("Search.searchEvaluation->case=11");
					 LinkedHashMap<Integer, String> andResults11 = getAndResults(hm, andConnectiveWords);
					 LinkedHashMap<Integer, String> orResults11 = getOrResults(andResults11, orConnectiveWords, hm);
					 orResults11.putAll(andResults11);
					 searchResults = andResults11;
					 break;
			case 12: //search for AND and OR and EXACT words
                                       System.out.println("Search.searchEvaluation->case=12");
					 LinkedHashMap<Integer, String> exactWords12 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> andResults12 = getAndResults(exactWords12, andConnectiveWords);
					 LinkedHashMap<Integer, String> orResults12 = getOrResults(andResults12, orConnectiveWords, hm);
					 orResults12.putAll(andResults12);					 
					 searchResults = orResults12;
					 break;					 
			case 13: //AND and NOT words
                                       System.out.println("Search.searchEvaluation->case=13");
					 LinkedHashMap<Integer, String> andResults13 = getAndResults(hm, andConnectiveWords);
					 LinkedHashMap<Integer, String> notResults13 = getNotResults(andResults13, notConnectiveWords);
//					 LinkedHashMap<Integer, String> notResults13a = getDiacrticSigns(hm,notResults13);
					 searchResults = notResults13;
					 break;
			case 14: //search for AND and NOT and EXACT words
                                       System.out.println("Search.searchEvaluation->case=14");
					 LinkedHashMap<Integer, String> exactWords14 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> andResults14 = getAndResults(exactWords14, andConnectiveWords);
					 LinkedHashMap<Integer, String> notResults14 = getNotResults(andResults14, notConnectiveWords);	
					 searchResults = notResults14;
					 break;
			case 15: //search for AND and NOT and OR words
                                       System.out.println("Search.searchEvaluation->case=15");
					 LinkedHashMap<Integer, String> andResults15 = getAndResults(hm, andConnectiveWords);
					 LinkedHashMap<Integer, String> notResults15 = getNotResults(andResults15, notConnectiveWords);
					 LinkedHashMap<Integer, String> orResults15 = getOrResults(notResults15, orConnectiveWords, hm);
					 orResults15.putAll(notResults15);
					 searchResults = notResults15;
					 break;
			case 16: //search for AND and NOT and OR and EXACT words 
                                       System.out.println("Search.searchEvaluation->case=16");
					 LinkedHashMap<Integer, String> exactWords16 = evaluateWholeWordSearchItems(hm, exactWords);
					 LinkedHashMap<Integer, String> andResults16 = getAndResults(exactWords16, andConnectiveWords);
					 LinkedHashMap<Integer, String> notResults16 = getNotResults(andResults16, notConnectiveWords);
					 LinkedHashMap<Integer, String> orResults16 = getOrResults(notResults16, orConnectiveWords, hm);
					 orResults16.putAll(notResults16);					 
					 searchResults = orResults16;
					 break;
		}
		return searchResults;
	}

	/** this part contains method that are not necessary for the application (see abstract) **/
	//not used in this application due the reason mentioned in the abstract
	private boolean exactWordSearch(String text, String [] searchList, int option) {
			boolean flag=false;		
            //exact word search
            for (String searchList1 : searchList) {
                String searchString = searchList1.toLowerCase();
                // Option = 1: search for whole words for or connective type search
                // Option = 2: search for whole words for and connective type search
                // Option = 3: search for whole words for not connective type search
                switch(option) {
                    case 1:	if(searchString.contains("+") || searchString.contains("-")) break;
                    if(searchString.contains("~")) searchString = searchString.replaceAll("\\~", ""); else break;
                    Pattern p1 = Pattern.compile("\\b"+searchString+"\\b");
                    Matcher m1 = p1.matcher(text);
                    while (m1.find()){
                        return true;
                    }
                    break;
                    case 2: if(!searchString.contains("+") && !searchString.contains("-")) break;
                    if(searchString.contains("+")) searchString = searchString.replaceAll("\\+", ""); else break;
                    Pattern p2 = Pattern.compile("\\b"+searchString+"\\b");
                    Matcher m2 = p2.matcher(text);
                    while (m2.find()){
                        return true;
                    }
                    break;
                    case 3:	flag = true;
                    if(!searchString.contains("+") && !searchString.contains("-")) break;
                    if( searchString.contains("+") || !searchString.contains("-")) break;
                    if( searchString.contains("-")) searchString = searchString.replaceAll("\\-", ""); else break;
                    Pattern p3 = Pattern.compile("\\b"+searchString+"\\b");
                    Matcher m3 = p3.matcher(text);
                    while (m3.find()){
                        return false;
                    }
                    break;
                    case 4: if(!searchString.contains("+") && !searchString.contains("-")) break;
                    if( searchString.contains("+") || !searchString.contains("-")) break;
                    if( searchString.contains("-")) searchString = searchString.replaceAll("\\-", ""); else break;
                    Pattern p4 = Pattern.compile("\\b"+searchString+"\\b");
                    Matcher m4 = p4.matcher(text);
                    while (m4.find()){
                        return true;
                    }
                    break;
                }
            }			
	        return flag;
		}

	//not necessary for this application due the reason mentioned in the abstract
	private LinkedHashMap<Integer, String> getExactWordResults(LinkedHashMap<Integer, String> hm, String [] list, int option) {		
		LinkedHashMap<Integer, String> results = new LinkedHashMap<>();
                hm.keySet().stream().filter((key) -> (exactWordSearch(hm.get(key), list, option))).forEach((key) -> {
                    results.put(key, hm.get(key));
            }); 		 
		return results;
	}

	//not used in this application
	private String [] getExactWord(LinkedHashMap<Integer, String> exactWords, String character) {
		ArrayList<String> arrayList = new ArrayList<>();
		String [] tmp = new String[arrayList.size()];
		
		Iterator<Map.Entry<Integer, String>> counter = exactWords.entrySet().iterator();
		while(counter.hasNext()) {
			Map.Entry<Integer, String> entry = counter.next();
			String value = entry.getValue();			
			if (value.contains(character)) {
				arrayList.add(value);
			} 
		}
		tmp=arrayList.toArray(tmp); 
		return tmp; 
	}

        public String [] convert2StringArray(LinkedHashMap<Integer, String> searchResults) {                
            Iterator<Map.Entry<Integer, String>> counter = searchResults.entrySet().iterator();
            int x=0;
            String tmp [];
                tmp = new String[searchResults.size()];
                while(counter.hasNext()) {
                    Map.Entry<Integer, String> entry = counter.next();
                    String value = entry.getValue();			
                    tmp[x]=value;
                    x++;
                }
            return tmp;
        }
        
		//not used in this application (see abstract above)
		private LinkedHashMap<Integer, String> evaluateWholeWordSearchItems(LinkedHashMap<Integer,String> hm, LinkedHashMap<Integer, String> exactWords) {
		 		int doOperation=1;	
				String [] preparedOrResults = getExactWord(exactWords, "~");
				String [] preparedAndResults = getExactWord(exactWords, "+");
				String [] preparedNotResults = getExactWord(exactWords, "-");
							
				LinkedHashMap<Integer, String> orResults = getExactWordResults(hm, preparedOrResults, 1); //get OR connective whole word expressions 
				LinkedHashMap<Integer, String> andResults = getExactWordResults(hm, preparedAndResults, 2); //get AND connective whole word expressions
				LinkedHashMap<Integer, String> notResults = getExactWordResults(hm, preparedNotResults, 3); //get NOT connective whole word expressions
				LinkedHashMap<Integer, String> invertedNotResults = getExactWordResults(hm, preparedNotResults, 4); //get inverted NOT connective whole word expressions
				LinkedHashMap<Integer, String> tmpResults = new LinkedHashMap<>();
				
				LinkedHashMap<Integer, String> allResults = new LinkedHashMap<>();
				allResults.putAll(orResults);
				allResults.putAll(andResults);
				allResults.putAll(notResults);
				allResults.putAll(exactWords);
				cleanedSearchItems = getCleanedStringItems(orResults);		
				
				if ( orResults.isEmpty() &&  andResults.isEmpty() &&  invertedNotResults.isEmpty()) doOperation=1;
				if ( orResults.isEmpty() &&  andResults.isEmpty() && !invertedNotResults.isEmpty()) doOperation=2;
				if ( orResults.isEmpty() && !andResults.isEmpty() &&  invertedNotResults.isEmpty()) doOperation=3;
				if ( orResults.isEmpty() && !andResults.isEmpty() && !invertedNotResults.isEmpty()) doOperation=4;
				if (!orResults.isEmpty() &&  andResults.isEmpty() &&  invertedNotResults.isEmpty()) doOperation=5;
				if (!orResults.isEmpty() &&  andResults.isEmpty() && !invertedNotResults.isEmpty()) doOperation=6;
				if (!orResults.isEmpty() && !andResults.isEmpty() &&  invertedNotResults.isEmpty()) doOperation=7;
				if (!orResults.isEmpty() && !andResults.isEmpty() && !invertedNotResults.isEmpty()) doOperation=8;			
				
				switch(doOperation) {
					case 1: break;
					case 2: LinkedHashMap<Integer, String> filtered=filterInvertedNOTsesults(invertedNotResults, preparedNotResults);
							notResults.putAll(filtered);					
							TreeMap<Integer, String> sortedResults = new TreeMap<>();
							sortedResults.putAll(notResults);
							notResults.clear();
							notResults.putAll(sortedResults);
							return notResults;
					case 3: tmpResults=getFinalANDResults(andResults, preparedAndResults);
							hm.clear();
							hm.putAll(tmpResults);
							break;
					case 4: LinkedHashMap<Integer, String> tmp = getExactWordResults(andResults, preparedNotResults, 3);
							return tmp;
					case 5: return orResults;
					case 6: LinkedHashMap<Integer, String> evaluatedEntries = removeEqualHashMapEntries(orResults, invertedNotResults);
							LinkedHashMap<Integer, String> filtered1=filterInvertedNOTsesults(invertedNotResults, preparedNotResults);
							LinkedHashMap<Integer, String> tmp6 = new LinkedHashMap<>();					
							tmp6.putAll(evaluatedEntries);
							TreeMap<Integer, String> sortedHM6=new TreeMap<>();
							sortedHM6.putAll(filtered1);
							sortedHM6.putAll(notResults);
							tmp6.putAll(sortedHM6);
							break;	
					case 7: andResults=getFinalANDResults(andResults, preparedAndResults);		
							LinkedHashMap<Integer, String> duplicates = getDuplicates(andResults, orResults); //get equal entries from andResults and orresults
							//LinkedHashMap<Integer, String> tmp7 = removeEqualHashMapEntries(andResults, orResults); //delete entries that are equal
							duplicates.putAll(andResults);
							break;
					case 8: LinkedHashMap<Integer, String> tmp8 = getExactWordResults(andResults, preparedNotResults, 3);
							LinkedHashMap<Integer, String> duplicates8 = getDuplicates(tmp8, orResults); //get equal entries from andResults and orresults
							//LinkedHashMap<Integer, String> tmp9 = removeEqualHashMapEntries(tmp8, orResults); //delete entries that are equal (I cant remember why I took this step)
							duplicates8.putAll(tmp8);
							break;									
				}
				return hm;
			}

		private LinkedHashMap<Integer, String> getInvertedResults(LinkedHashMap<Integer, String> hm, LinkedHashMap<Integer, String> exactWords) {
			String [] preparedNotResults = getExactWord(exactWords, "-");
			LinkedHashMap<Integer, String> invertedNotResults = getExactWordResults(hm, preparedNotResults, 4);
			return invertedNotResults;
		}
}










