package searchers;

import indexers.Indexer;

public class Main {
	public static void main(String args[]){
//		try{
//			Indexer.startIndex(7);
//		}catch(Exception ex){ex.printStackTrace();}
		VisualSearcher.buscar();
		try{
			TextualSearcher.buscar();
			(new CategoryRanker()).buscar();
		}catch(Exception ex){ex.printStackTrace();}
		
	}
	
}
