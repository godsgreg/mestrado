package searchers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ModeloVetorial {
	
	/**
	 * Implementação do modelo vetorial
	 */
	
	Map<String, Map<Integer, Integer> > hashTermos = new HashMap<String, Map<Integer, Integer> >(); //termo > lista
	Map<Integer, Double> hashNormas = new HashMap<Integer, Double>(); //docID > norma
	Map<Integer, String> imagePaths = new HashMap<Integer, String>();
	public Map<String, String> descricao = new HashMap<String, String>();
	int numeroDocs = 0;

	String removeAcentos(String s){
		s.replace("é","e");
		s.replace("è","e");
		s.replace("ê","o");
		s.replace("á","a");
		s.replace("à","a");
		s.replace("ã","a");
		s.replace("í","i");
		s.replace("ì","i");
		s.replace("ó","o");
		s.replace("õ","o");
		s.replace("ô","o");
		s.replace("ú","u");
		s.replace("ü","u");
		s.replace("ç","c");
		return s;
	}
	
	String cleanString(String s){
		s = s.replace(".", "");
		s = s.replace(",", "");
		s = s.replace("!", "");
		s = s.replace("\"", "");
		s = s.replace("\'", "");
		s = s.replace("(", "");
		s = s.replace(")", "");
		s = s.replace("/", "");
		s = s.replace("+", "");
		s = s.replace("-", "");
		s = s.toLowerCase();
		//removeAcentos(s);
		s = removeAcentos(s);
		return s;
	}

	void lerDados(String fileName) throws IOException{
		//lê arquivo txt com dados da base e transforma em listas invertidas
		String line;
		int imageId = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		while ((line = br.readLine()) != null) {
			numeroDocs++;

			String dados[] = line.split(" ");

			//register image path as an integer
			imagePaths.put(++imageId,dados[0]);
			descricao.put(dados[0], dados[1]);

			for(int i = 1; i < dados.length; i++){
				String termo = cleanString(dados[i]);
				Map<Integer, Integer> listaInvertida = hashTermos.get(termo);
				if(listaInvertida != null){
					Integer freq = listaInvertida.get(imageId);
					if(freq != null){
						freq++;
						listaInvertida.put(imageId, freq);
						hashTermos.put(termo, listaInvertida);
					}else{
						listaInvertida.put(imageId, 1);
						hashTermos.put(termo, listaInvertida);
					}
				}else{
					listaInvertida = new HashMap<Integer, Integer>();
					listaInvertida.put(imageId, 1);
					hashTermos.put(termo,listaInvertida);
				}
			}
		}
		br.close();
	}	          


	double peso(String termo, int imageId){
		//tdxidf
		double peso;
		double idf = Math.log(numeroDocs / hashTermos.get(termo).size());
		Integer freq = hashTermos.get(termo).get(imageId);
		if(freq != null){
			peso = hashTermos.get(termo).get(imageId) * idf;
		}else{
			peso = 0;
		}
		return peso;
	}

	void calcularNormas(){
		// somatorio dos pesos ao quadrado
		for(String key : hashTermos.keySet()){
			for(Integer i : hashTermos.get(key).keySet()){ 	
    			// acumulador	
				Double norma = hashNormas.get(i);
				if(norma != null){
					norma += Math.pow(peso(key,i),2.0f);				
				}else{
					norma = Math.pow(peso(key,i),2.0f);
				}
				hashNormas.put(i , norma);
    		}   		
    	}
	    	
    	for (Integer i : hashNormas.keySet()){
    		hashNormas.put(i, Math.sqrt(hashNormas.get(i)));

    	}
	}

	Map<String,Float> similaridade(String query){
		double somatorio = 0, norma = 0;
		String termoQ;

		Map<String,Float> rank; //lista de imagens e suas similaridades
		Map<String,Integer> palavrasChave = new HashMap<String, Integer>(); //lista de frequencias de termos na query
		
		String[] dados = query.split(" "); 
		// separa os termos contidos na query e joga na lista de frequencias
		for(int i = 0; i < dados.length; i++){
			termoQ = cleanString(dados[i]);

			if(termoQ.length() > 0){
				if(hashTermos.get(termoQ) != null){
					if(palavrasChave.get(termoQ) != null){
						int freq = palavrasChave.get(termoQ);
						palavrasChave.put(termoQ, freq++);
					}else{
						palavrasChave.put(termoQ,1);
					}
				}
			}		
		}
		
		if(palavrasChave.size() == 0){
			Map<String, Float> vazio = new HashMap<String, Float>();
			return vazio;
		}

		//calcula norma para a query
		for(String key : palavrasChave.keySet()){
			//idf do termo da query dentro da base
			double idf = Math.log(numeroDocs / hashTermos.get(key).size());
			//peso tf x idf
			double pesoQ = palavrasChave.get(key) * idf;
			//somatorio das potencias		
			norma += Math.pow(pesoQ,2.0);		
    	}
    	norma = Math.sqrt(norma);
    	
    	rank = new HashMap<String, Float>();
    	//Calculo de similaridade entre a query e todos os documentos da base de dados
    	for(Integer i : imagePaths.keySet()){
    		//função de similaridade: cos angulo = (sum(peso(doc i,termo j) x peso(query, termo j))/ norma x normaQ)
    		somatorio = 0;
    		for(String key : palavrasChave.keySet()){
				double idf = Math.log(numeroDocs / hashTermos.get(key).size());
				double pesoQ = palavrasChave.get(key) * idf;		
				somatorio += pesoQ * peso(key,i);	
    		}
    		double similaridade = somatorio / (norma * hashNormas.get(i));
    		//System.out.println("Similaridade entre " + imagePaths.get(i) + " e \"" + query + "\" é "+ similaridade);
    		//similaridade é invertida para trabalhar em conjunto com as distancias normalizadas usadas nas buscas visuais
    		rank.put(imagePaths.get(i), 1 - (float)similaridade);
    	}
    	
    	return rank;
    	
	}
	
	static String[][] ordena(String[] paths, float[] distancias){
		//ordena o rank
		int menor;
		for(int i = 0; i < distancias.length - 1; i++){
			menor = i;
			for(int j = i+1; j < distancias.length; j++){
				if(distancias[j] < distancias[menor]){
					menor = j;
				}
			}
			if(menor != i){
				float temp;
				String tempS;
				
				temp = distancias[i];
				distancias[i] = distancias[menor];
				distancias[menor] = temp;
				
				tempS = paths[i];
				paths[i] = paths[menor];
				paths[menor] = tempS;
			}
		}
		String[][] results = new String[2][distancias.length];
		results[0] = paths;
		for(int i = 0; i < distancias.length - 1; i++){
			results[1][i] = String.valueOf(distancias[i]);
			
		}
		return results;
	}
	
	public ModeloVetorial(){
		try {
			//gera as listas invertidas
			lerDados(utilidades.ProjectSetup.databaseTxtPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calcularNormas();

	}
}
