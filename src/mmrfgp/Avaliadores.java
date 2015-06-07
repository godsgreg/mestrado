package mmrfgp;

import java.util.List;

public class Avaliadores {
	public static float mapValue(String[] rank, List<String> relevantes){
		//MAP não interpolado
		float acc = 0.0f, mapV = 0.0f;
		int c = 0;
		
		/*System.out.println("Relevantes:");
		for(String str: relevantes) {
			System.out.println(str);

		}
		System.out.println("\nRank: ");
		*/
		for(int i = 0; i < 10; i++){ 
			//System.out.print(rank[i] + " : ");
			for(String str: relevantes) {
				if(str.equals(rank[i])){
					//System.out.print("relevante");
					acc = 0.0f;
					for(int j = 0; j <= i; j++){
						for(String str2: relevantes) {
							if(str2.equals(rank[j])){
								acc = acc + 1.0f;
								break;
							}
							
						}
					}
					acc = acc / (float)(i+1);
					//System.out.print("\n Precisão no ponto " + i + " : " + acc + "\n" );
					mapV += acc;
					c++;
					break;
				}
			}
			//System.out.println();
		}
		//if(mapV > 0)
			//System.exit(0);
		if(c == 0)
			return 0.0f;
		else
			return mapV / (float)c;
	}
	
	public static float PAt10(String[] rank, List<String> relevantes){
		float acc = 0.0f;
		/*System.out.println("Relevantes:");
		for(String str: relevantes) {
			System.out.println(str);

		}
		System.out.println("\nRank: ");*/
		for(int i = 0; i < 10; i++){
			//System.out.print(rank[i] + " : ");
			for(String str: relevantes) {
				if(str.equals(rank[i])){
					acc = acc + 1.0f;
					//System.out.print("é relevante");
					break;
				}
			}
			//System.out.println();
		}
		//System.exit(0);
		return acc / 10.0f;
	}
	
	public static float PAt5(String[] rank, List<String> relevantes){
		float acc = 0.0f;
		for(int i = 0; i < 5; i++){
			for(String str: relevantes) {
				if(str.equals(rank[i])){
					acc = acc + 1.0f;
					break;
				}
			}
		}
		return acc / 5.0f;
	}
}
