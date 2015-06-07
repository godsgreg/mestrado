package searchers;

public class Main {
	public static void main(String args[]){
		//VisualSearcher.buscar();
		try{
			TextualSearcher.buscar();
		}catch(Exception ex){ex.printStackTrace();}
		
	}
	
}
