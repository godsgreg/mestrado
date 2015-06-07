package indexers;

public class Main {
	public static void main(String args[]){	
		String databasePath = utilidades.ProjectSetup.databasePath;//"colecaoDafitiPosthaus";
		
		try{
			BIC.index(databasePath, "indexBIC");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 1; i < 8; i++){
			try{
				Indexer.startIndex(i);
			}catch(Exception ex){ex.printStackTrace();}
		}
    }
}
