package mmrfgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Log;
import org.jgap.gp.function.Max;
import org.jgap.gp.function.Min;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

import utilidades.FileHandler;
import utilidades.Sqrt;

/**
 * Classe principal do experimento
 */

public class GPCore extends GPProblem {
	
	int geracao = 0, individuosCont = 0;
	static int currentFold = 0, currentState = 1;
	static int folds[][][];
	static FileHandler dataController;

	protected static Variable ACC_sim, CEDD_sim, CLD_sim, FCTH_sim, GCH_sim, JCD_sim, PHOG_sim, SIFT_sim, BIC_sim;
	protected static Variable ACC_txt1, CEDD_txt1, CLD_txt1, FCTH_txt1, GCH_txt1, JCD_txt1, PHOG_txt1, SIFT_txt1, BIC_txt1;
	protected static Variable ACC_txt5, CEDD_txt5, CLD_txt5, FCTH_txt5, GCH_txt5, JCD_txt5, PHOG_txt5, SIFT_txt5, BIC_txt5;
	protected static Variable ACC_txt10, CEDD_txt10, CLD_txt10, FCTH_txt10, GCH_txt10, JCD_txt10, PHOG_txt10, SIFT_txt10, BIC_txt10;
	protected static Variable ACC_txt20, CEDD_txt20, CLD_txt20, FCTH_txt20, GCH_txt20, JCD_txt20, PHOG_txt20, SIFT_txt20, BIC_txt20;
	
	protected static Variable ACC_cat1, CEDD_cat1, CLD_cat1, FCTH_cat1, GCH_cat1, JCD_cat1, PHOG_cat1, SIFT_cat1, BIC_cat1;
	protected static Variable ACC_cat5, CEDD_cat5, CLD_cat5, FCTH_cat5, GCH_cat5, JCD_cat5, PHOG_cat5, SIFT_cat5, BIC_cat5;
	protected static Variable ACC_cat10, CEDD_cat10, CLD_cat10, FCTH_cat10, GCH_cat10, JCD_cat10, PHOG_cat10, SIFT_cat10, BIC_cat10;
	protected static Variable ACC_cat20, CEDD_cat20, CLD_cat20, FCTH_cat20, GCH_cat20, JCD_cat20, PHOG_cat20, SIFT_cat20, BIC_cat20;
	
	protected static Variable ACC_min, CEDD_min, CLD_min, FCTH_min, GCH_min, JCD_min, PHOG_min, SIFT_min, BIC_min;
	protected static Variable ACC_max, CEDD_max, CLD_max, FCTH_max, GCH_max, JCD_max, PHOG_max, SIFT_max, BIC_max;
	
	static int numberOfQueries = 20;
	static float similaridadeMinima = 0.0f;
	static String queryPath = utilidades.ProjectSetup.ranksConsultasPath;//"consultasAvaliadas/colecaoAvulso_semParticao";
	static String relevantPath = utilidades.ProjectSetup.relevantesPath;//"Relevantes/avulso";
//	static ModeloVetorial MV = new ModeloVetorial();

	public GPCore(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
	}

	public GPGenotype create()
			throws InvalidConfigurationException {
		GPConfiguration conf = getGPConfiguration();
		// At first, we define the return type of the GP program.
		// ------------------------------------------------------
		Class[] types = {
				// Return type of result-producing chromosome
				CommandGene.FloatClass};/*,
				// ADF-relevant:
				// Return type of ADF 1
				CommandGene.FloatClass};*/
		// Then, we define the arguments of the GP parts. Normally, only for ADF's
		// there is a specification here, otherwise it is empty as in first case.
		// -----------------------------------------------------------------------
		Class[][] argTypes = {
				// Arguments of result-producing chromosome: none
				{}};
		CommandGene[][] nodeSets = { {
			// We use a variable that can be set in the fitness function.
			// ----------------------------------------------------------
			ACC_sim = Variable.create(conf, "ACC", CommandGene.FloatClass),
			CEDD_sim = Variable.create(conf, "CEDD", CommandGene.FloatClass),
			CLD_sim = Variable.create(conf, "CLD", CommandGene.FloatClass),
			FCTH_sim = Variable.create(conf, "FCTH", CommandGene.FloatClass),
			GCH_sim = Variable.create(conf, "GCH", CommandGene.FloatClass),
			JCD_sim = Variable.create(conf, "JCD", CommandGene.FloatClass),
			PHOG_sim = Variable.create(conf, "PHOG", CommandGene.FloatClass),
			SIFT_sim = Variable.create(conf, "SIFT", CommandGene.FloatClass),
			BIC_sim = Variable.create(conf, "BIC", CommandGene.FloatClass),
			
			ACC_txt1 = Variable.create(conf, "ACC_text1", CommandGene.FloatClass),
			CEDD_txt1 = Variable.create(conf, "CEDD_text1", CommandGene.FloatClass),
			CLD_txt1 = Variable.create(conf, "CLD_text1", CommandGene.FloatClass),
			FCTH_txt1 = Variable.create(conf, "FCTH_text1", CommandGene.FloatClass),
			GCH_txt1 = Variable.create(conf, "GCH_text1", CommandGene.FloatClass),
			JCD_txt1 = Variable.create(conf, "JCD_text1", CommandGene.FloatClass),
			PHOG_txt1 = Variable.create(conf, "PHOG_text1", CommandGene.FloatClass),
			SIFT_txt1 = Variable.create(conf, "SIFT_text1", CommandGene.FloatClass),
			BIC_txt1 = Variable.create(conf, "BIC_text1", CommandGene.FloatClass),
			
			ACC_txt5 = Variable.create(conf, "ACC_text5", CommandGene.FloatClass),
			CEDD_txt5 = Variable.create(conf, "CEDD_text5", CommandGene.FloatClass),
			CLD_txt5 = Variable.create(conf, "CLD_text5", CommandGene.FloatClass),
			FCTH_txt5 = Variable.create(conf, "FCTH_text5", CommandGene.FloatClass),
			GCH_txt5 = Variable.create(conf, "GCH_text5", CommandGene.FloatClass),
			JCD_txt5 = Variable.create(conf, "JCD_text5", CommandGene.FloatClass),
			PHOG_txt5 = Variable.create(conf, "PHOG_text5", CommandGene.FloatClass),
			SIFT_txt5 = Variable.create(conf, "SIFT_text5", CommandGene.FloatClass),
			BIC_txt5 = Variable.create(conf, "BIC_text5", CommandGene.FloatClass),
					
			ACC_txt10 = Variable.create(conf, "ACC_text10", CommandGene.FloatClass),
			CEDD_txt10 = Variable.create(conf, "CEDD_text10", CommandGene.FloatClass),
			CLD_txt10 = Variable.create(conf, "CLD_text10", CommandGene.FloatClass),
			FCTH_txt10 = Variable.create(conf, "FCTH_text10", CommandGene.FloatClass),
			GCH_txt10 = Variable.create(conf, "GCH_text10", CommandGene.FloatClass),
			JCD_txt10 = Variable.create(conf, "JCD_text10", CommandGene.FloatClass),
			PHOG_txt10 = Variable.create(conf, "PHOG_text10", CommandGene.FloatClass),
			SIFT_txt10 = Variable.create(conf, "SIFT_text10", CommandGene.FloatClass),
			BIC_txt10 = Variable.create(conf, "BIC_text10", CommandGene.FloatClass),
							
			ACC_txt20 = Variable.create(conf, "ACC_text20", CommandGene.FloatClass),
			CEDD_txt20 = Variable.create(conf, "CEDD_text20", CommandGene.FloatClass),
			CLD_txt20 = Variable.create(conf, "CLD_text20", CommandGene.FloatClass),
			FCTH_txt20 = Variable.create(conf, "FCTH_text20", CommandGene.FloatClass),
			GCH_txt20 = Variable.create(conf, "GCH_text20", CommandGene.FloatClass),
			JCD_txt20 = Variable.create(conf, "JCD_text20", CommandGene.FloatClass),
			PHOG_txt20 = Variable.create(conf, "PHOG_text20", CommandGene.FloatClass),
			SIFT_txt20 = Variable.create(conf, "SIFT_text20", CommandGene.FloatClass),
			BIC_txt20 = Variable.create(conf, "BIC_text20", CommandGene.FloatClass),
			
			ACC_cat1 = Variable.create(conf, "ACC_cat1", CommandGene.FloatClass),
			CEDD_cat1 = Variable.create(conf, "CEDD_cat1", CommandGene.FloatClass),
			CLD_cat1 = Variable.create(conf, "CLD_cat1", CommandGene.FloatClass),
			FCTH_cat1 = Variable.create(conf, "FCTH_cat1", CommandGene.FloatClass),
			GCH_cat1 = Variable.create(conf, "GCH_cat1", CommandGene.FloatClass),
			JCD_cat1 = Variable.create(conf, "JCD_cat1", CommandGene.FloatClass),
			PHOG_cat1 = Variable.create(conf, "PHOG_cat1", CommandGene.FloatClass),
			SIFT_cat1 = Variable.create(conf, "SIFT_cat1", CommandGene.FloatClass),
			BIC_cat1 = Variable.create(conf, "BIC_cat1", CommandGene.FloatClass),
			
			ACC_cat5 = Variable.create(conf, "ACC_cat5", CommandGene.FloatClass),
			CEDD_cat5 = Variable.create(conf, "CEDD_cat5", CommandGene.FloatClass),
			CLD_cat5 = Variable.create(conf, "CLD_cat5", CommandGene.FloatClass),
			FCTH_cat5 = Variable.create(conf, "FCTH_cat5", CommandGene.FloatClass),
			GCH_cat5 = Variable.create(conf, "GCH_cat5", CommandGene.FloatClass),
			JCD_cat5 = Variable.create(conf, "JCD_cat5", CommandGene.FloatClass),
			PHOG_cat5 = Variable.create(conf, "PHOG_cat5", CommandGene.FloatClass),
			SIFT_cat5 = Variable.create(conf, "SIFT_cat5", CommandGene.FloatClass),
			BIC_cat5 = Variable.create(conf, "BIC_cat5", CommandGene.FloatClass),
					
			ACC_cat10 = Variable.create(conf, "ACC_cat10", CommandGene.FloatClass),
			CEDD_cat10 = Variable.create(conf, "CEDD_cat10", CommandGene.FloatClass),
			CLD_cat10 = Variable.create(conf, "CLD_cat10", CommandGene.FloatClass),
			FCTH_cat10 = Variable.create(conf, "FCTH_cat10", CommandGene.FloatClass),
			GCH_cat10 = Variable.create(conf, "GCH_cat10", CommandGene.FloatClass),
			JCD_cat10 = Variable.create(conf, "JCD_cat10", CommandGene.FloatClass),
			PHOG_cat10 = Variable.create(conf, "PHOG_cat10", CommandGene.FloatClass),
			SIFT_cat10 = Variable.create(conf, "SIFT_cat10", CommandGene.FloatClass),
			BIC_cat10 = Variable.create(conf, "BIC_cat10", CommandGene.FloatClass),
							
			ACC_cat20 = Variable.create(conf, "ACC_cat20", CommandGene.FloatClass),
			CEDD_cat20 = Variable.create(conf, "CEDD_cat20", CommandGene.FloatClass),
			CLD_cat20 = Variable.create(conf, "CLD_cat20", CommandGene.FloatClass),
			FCTH_cat20 = Variable.create(conf, "FCTH_cat20", CommandGene.FloatClass),
			GCH_cat20 = Variable.create(conf, "GCH_cat20", CommandGene.FloatClass),
			JCD_cat20 = Variable.create(conf, "JCD_cat20", CommandGene.FloatClass),
			PHOG_cat20 = Variable.create(conf, "PHOG_cat20", CommandGene.FloatClass),
			SIFT_cat20 = Variable.create(conf, "SIFT_cat20", CommandGene.FloatClass),
			BIC_cat20 = Variable.create(conf, "BIC_cat20", CommandGene.FloatClass),
			
			ACC_min = Variable.create(conf, "ACC_min", CommandGene.FloatClass),
			CEDD_min = Variable.create(conf, "CEDD_min", CommandGene.FloatClass),
			CLD_min = Variable.create(conf, "CLD_min", CommandGene.FloatClass),
			FCTH_min = Variable.create(conf, "FCTH_min", CommandGene.FloatClass),
			GCH_min = Variable.create(conf, "GCH_min", CommandGene.FloatClass),
			JCD_min = Variable.create(conf, "JCD_min", CommandGene.FloatClass),
			PHOG_min = Variable.create(conf, "PHOG_min", CommandGene.FloatClass),
			SIFT_min = Variable.create(conf, "SIFT_min", CommandGene.FloatClass),
			BIC_min = Variable.create(conf, "BIC_min", CommandGene.FloatClass),
							
			ACC_max = Variable.create(conf, "ACC_max", CommandGene.FloatClass),
			CEDD_max = Variable.create(conf, "CEDD_max", CommandGene.FloatClass),
			CLD_max = Variable.create(conf, "CLD_max", CommandGene.FloatClass),
			FCTH_max = Variable.create(conf, "FCTH_max", CommandGene.FloatClass),
			GCH_max = Variable.create(conf, "GCH_max", CommandGene.FloatClass),
			JCD_max = Variable.create(conf, "JCD_max", CommandGene.FloatClass),
			PHOG_max = Variable.create(conf, "PHOG_max", CommandGene.FloatClass),
			SIFT_max = Variable.create(conf, "SIFT_max", CommandGene.FloatClass),
			BIC_max = Variable.create(conf, "BIC_max", CommandGene.FloatClass),
									

			new Multiply(conf, CommandGene.FloatClass),
			//new Multiply3(conf, CommandGene.FloatClass),
			new Divide(conf, CommandGene.FloatClass),
			//new Sine(conf, CommandGene.FloatClass),
			//new Exp(conf, CommandGene.FloatClass),
			new Subtract(conf, CommandGene.FloatClass),
			new Add(conf, CommandGene.FloatClass),
			//new Pow(conf, CommandGene.FloatClass),
			new Terminal(conf, CommandGene.FloatClass, 0, 100, true),
			// ADF-relevant:
				// Construct a reference to the ADF defined in the second nodeset
			// which has index 1 (second parameter of ADF-constructor).
			// Furthermore, the ADF expects three input parameters (see argTypes[1])
			new Sqrt(conf, CommandGene.FloatClass),
			new Log(conf, CommandGene.FloatClass),
			new Max(conf, CommandGene.FloatClass),
			new Min(conf, CommandGene.FloatClass)
		}};

		
		
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets, 50, true);
	}

	public static void main(String[] args)
			throws Exception {

		GPConfiguration config = new GPConfiguration();

		int numeroGeracoes = 30;//30
		config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
		config.setMaxInitDepth(10);
		config.setPopulationSize(100);//(300);
		config.setMaxCrossoverDepth(6);
		config.setMinInitDepth(2);
		config.setMutationProb(0.05f);
		config.setCrossoverProb(0.9f);
		config.setReproductionProb(0.05f);
		config.setFitnessFunction(new GPCore.FormulaFitnessFunction());
		config.setStrictProgramCreation(true);
		GPProblem problem = new GPCore(config);

		GPGenotype gp;// = problem.create();
		//gp.setVerboseOutput(true);
		//gp.outputSolution(gp.getAllTimeBest());
		List<Double> solucoes = new ArrayList<Double>();
		folds = utilidades.FoldGenerator.breakSetSelected();
		
		System.out.println("loading files...");
		dataController = new FileHandler();
		dataController.loadData(queryPath);
		dataController.loadRelevants(relevantPath);
		for(currentFold = 0; currentFold < 5; currentFold++){
			System.out.println("==================================================\nCriando população inicial para a partição " + String.valueOf(currentFold + 1));
			gp = problem.create();
			gp.setVerboseOutput(true);
			currentState = 1; //treino
			System.out.println("Evoluindo...");
			int i = 0;
			while(i < numeroGeracoes){
				double trainingPrec, validationPrec;
				currentState = 1;
				gp.evolve();
				trainingPrec = gp.getFittestProgram().getFitnessValue();
				currentState = 0;
				validationPrec = FormulaFitnessFunction.computeRawFitness(gp.getFittestProgram());
				System.out.println("Geração " + i +" -- Treino: " + trainingPrec + " - Validação: " + validationPrec);
				//if( i > 5 && validationPrec < trainingPrec)
					//break;
				i++;
			}
			gp.outputSolution(gp.getFittestProgram());
			//gp.evolve(numeroGeracoes);

			//currentFold++;
		}

	}


	public static class FormulaFitnessFunction
	extends GPFitnessFunction {
		protected double evaluate(final IGPProgram a_subject) {
			return computeRawFitness(a_subject);
		}

		public static double computeRawFitness(final IGPProgram ind) {
			float[] vetorFitness = new float[100];

			List<String> descritores;
			Map<String, Map<String, Float> > ranks;
			Map<String, Float> novoRank;
			Map<String, float[]> menoresMaiores;
			/**
			 * Esta função recebe uma pasta e lista todos os arquivos contidos nela.
			 * Lista dos arquivos referentes as consultas.
			 * Para cada consulta, retorna o rank gerado para cada descritor.
			 * Combina os ranks em um novo através do cromossomo.
			 * Avalia o rank usando MAP e retorna o fitness
			 *  */
			
			/**
			 * Estrutura dos arquivos
			 * 
			 * Consultas:
			 * 0.3232 2412_Shorts_numseiqla.jpg
			 * 1.2345 2413_shorts_paenumseioq.jpg
			 * 
			 * Relevantes:
			 * nomes somente
			 */
			
			
			File folder = new File(queryPath);
			File[] listOfFolders = folder.listFiles(); //retorna lista de paths para cada descritor
			descritores = new ArrayList<String>();
			for (int i = 0; i < listOfFolders.length; i++) {
				// Mapeia as 100 consultas de acordo com o nome do descritor
				descritores.add(listOfFolders[i].getName());
			}
			
			int[] imagensSelecionadas = selectImages();
			
			numberOfQueries = folds[currentFold][currentState].length;
			
			for(int i = 0; i < numberOfQueries; i++){
				int currentQuery = imagensSelecionadas[i];
				//System.out.println(i);
				//para cada imagem da lista de queries
				ranks = new HashMap<String, Map<String, Float> >();
				menoresMaiores = new HashMap<String, float[]>();
				for (String key : descritores) {
					//System.out.println("Gerando rank para " + key);
					ranks.put(key, dataController.getRank(key ,String.valueOf(currentQuery)+".txt"));
					float[] f = minMax(ranks.get(key));
					menoresMaiores.put(key, f);
				}
				//dado os k ranks, combina eles usando o cromossomo atual
				ranks = normalizaRank(ranks);
				//System.out.println("Combinando ranks");
				novoRank = combinaRank(ranks, menoresMaiores, ind);
				
				//avalia a precisão do rank usando MAP
				vetorFitness[i] = avaliaRank(novoRank, currentQuery);
				
				//System.out.println(vetorFitness[i]);
			}
			
			float fitness = 0.0f;
			for(int i = 0; i <numberOfQueries; i++){
				fitness += vetorFitness[i];
			}
			fitness /= numberOfQueries;
			System.out.println("Valor do fitness: " + fitness);
			return fitness;
		}
		
//		static Map<String, Float> recuperaRank(File file){
//			Map<String, Float> result = new HashMap<String, Float>();
//			try{
//				BufferedReader br = new BufferedReader(new FileReader(file));
//				String line;
//				while ((line = br.readLine()) != null) {
//					//System.out.println(line);
//					String[] dados = line.split(" ");
//					result.put(dados[1], Float.parseFloat(dados[0]) );
//				}
//				br.close();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//
//			return result;
//		}
		
	/*	Map<String, Float> expandeRank(Map<String, Float> rank, int k){
			String[] rankStr = new String[rank.size()];
			float[] rankSim = new float[rank.size()];
			int i = 0;
			
			//gera query com base nas k primeiras imagens do rank e a primeira palavra de suas descrições
			for(String key : rank.keySet()){
			    rankStr[i] = key;
			    rankSim[i] = rank.get(key);
			    i++;
			}
			rankStr = ordena(rankStr, rankSim)[0];
			String query = "";
			for(i = 0; i < k; i++){
				query += MV.descricao.get(rankStr[i]) + " ";
			}
			
			Map<String, Float> rankText = MV.similaridade(query);
			
			//normaliza rank textural
			float menor = 10000.0f, maior = 0.0f;
			for(String key: rankText.keySet()){
				if(rankText.get(key) > maior){
					maior = rankText.get(key);
				}
				if(rankText.get(key) < menor){
					menor = rankText.get(key);
				}
			}
			for(String key: rankText.keySet()){

				float newSim;
				newSim = (rankText.get(key) - menor) / (maior - menor);
				if(newSim == 0.0f){
					newSim = 0.001f;
				}
				rankText.put(key, newSim);
				
			}
			//Insere novas imagens no rank, e atualiza as antigas		
			for(String key: rankText.keySet()){
				rank.put(key, rankText.get(key));
			}
			return rank;
		}
		*/
		static Map<String, Float> combinaRank(Map<String, Map<String, Float> > ranks, Map<String, float[]> menoresMaiores, final IGPProgram ind){
			Map<String, Float> n_rank = new HashMap<String, Float>();
			Object[] noargs = new Object[0];
			// TO-DO
			//System.out.println("Combinando rank para avaliar arvore...");
			for (String key : ranks.keySet()) {
				//para cada descritor
				
				for (String key2 : ranks.get(key).keySet()) {
					//para cada imagem rankeada pelo descritor
					
					//ACC_sim, CEDD_sim, CLD_sim, FCTH_sim, GCH_sim, JCD_sim, PHOG_sim
					if(n_rank.get(key2) == null){
						//se ja nao estiver contida no novo rank
						// ADICIONAR FLOAT 'RANK.GET(KEY).GET(KEY2)' AO CROMOSSOMO PARA O ALELO 'KEY'
						//System.out.println("Atribuindo valor para " + key);
						
						if(key.equals("ACC")){
							ACC_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							ACC_min.set(f[0]);
							ACC_max.set(f[1]);
							//System.out.println(key + " recebe " + ACC_sim.getValue());
						}else if(key.equals("CEDD")){
							CEDD_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							CEDD_min.set(f[0]);
							CEDD_max.set(f[1]);
						}else if(key.equals("CLD")){
							CLD_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							CLD_min.set(f[0]);
							CLD_max.set(f[1]);
						}else if(key.equals("FCTH")){
							FCTH_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							FCTH_min.set(f[0]);
							FCTH_max.set(f[1]);
						}else if(key.equals("GCH")){
							GCH_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							GCH_min.set(f[0]);
							GCH_max.set(f[1]);
						}else if(key.equals("JCD")){
							JCD_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							JCD_min.set(f[0]);
							JCD_max.set(f[1]);
						}else if(key.equals("PHOG")){
							PHOG_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							PHOG_min.set(f[0]);
							PHOG_max.set(f[1]);
						}else if(key.equals("SIFT")){
							SIFT_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							SIFT_min.set(f[0]);
							SIFT_max.set(f[1]);
						}else if(key.equals("BIC")){
							BIC_sim.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
							float[] f = menoresMaiores.get(key);
							BIC_min.set(f[0]);
							BIC_max.set(f[1]);
						}//ExpansaoGPI txt 1 a seguir
						else if(key.equals("ACC_text1")){
							ACC_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_text1")){
							CEDD_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_text1")){
							CLD_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_text1")){
							FCTH_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_text1")){
							GCH_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_text1")){
							JCD_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_text1")){
							PHOG_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_text1")){
							SIFT_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_text1")){
							BIC_txt1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI txt 5 a seguir
						else if(key.equals("ACC_text5")){
							ACC_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_text5")){
							CEDD_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_text5")){
							CLD_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_text5")){
							FCTH_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_text5")){
							GCH_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_text5")){
							JCD_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_text5")){
							PHOG_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_text5")){
							SIFT_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_text5")){
							BIC_txt5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI txt 10 a seguir
						else if(key.equals("ACC_text10")){
							ACC_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_text10")){
							CEDD_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_text10")){
							CLD_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_text10")){
							FCTH_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_text10")){
							GCH_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_text10")){
							JCD_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_text10")){
							PHOG_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_text10")){
							SIFT_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_text10")){
							BIC_txt10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI txt 10 a seguir
						else if(key.equals("ACC_text20")){
							ACC_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_text20")){
							CEDD_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_text20")){
							CLD_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_text20")){
							FCTH_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_text20")){
							GCH_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_text20")){
							JCD_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_text20")){
							PHOG_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_text20")){
							SIFT_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_text20")){
							BIC_txt20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("ACC_cat1")){
							ACC_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_cat1")){
							CEDD_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_cat1")){
							CLD_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_cat1")){
							FCTH_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_cat1")){
							GCH_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_cat1")){
							JCD_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_cat1")){
							PHOG_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_cat1")){
							SIFT_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_cat1")){
							BIC_cat1.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI cat 5 a seguir
						else if(key.equals("ACC_cat5")){
							ACC_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_cat5")){
							CEDD_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_cat5")){
							CLD_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_cat5")){
							FCTH_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_cat5")){
							GCH_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_cat5")){
							JCD_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_cat5")){
							PHOG_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_cat5")){
							SIFT_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_cat5")){
							BIC_cat5.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI cat 10 a seguir
						else if(key.equals("ACC_cat10")){
							ACC_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_cat10")){
							CEDD_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_cat10")){
							CLD_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_cat10")){
							FCTH_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_cat10")){
							GCH_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_cat10")){
							JCD_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_cat10")){
							PHOG_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_cat10")){
							SIFT_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_cat10")){
							BIC_cat10.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}//ExpansaoGPI cat 10 a seguir
						else if(key.equals("ACC_cat20")){
							ACC_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CEDD_cat20")){
							CEDD_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("CLD_cat20")){
							CLD_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("FCTH_cat20")){
							FCTH_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("GCH_cat20")){
							GCH_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("JCD_cat20")){
							JCD_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("PHOG_cat20")){
							PHOG_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("SIFT_cat20")){
							SIFT_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}else if(key.equals("BIC_cat20")){
							BIC_cat20.set(ranks.get(key).get(key2) == null ? similaridadeMinima : ranks.get(key).get(key2));
						}
						
						
						for (String key3 : ranks.keySet()) {
							if(!key3.equals(key)){
								//para cada ocorrencia da mesma imagem nos demais ranks
								//ADICIONAR FLOAT 'RANK.GET(KEY3).GET(KEY2)' AO CROMOSSOMO PARA O ALELO 'KEY3'
								//System.out.println("Agora tribuindo valor para " + key3);
								
								if(key3.equals("ACC")){
									ACC_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									ACC_min.set(f[0]);
									ACC_max.set(f[1]);
									//System.out.println(key3 + " recebe " + ACC_sim.getValue());
								}else if(key3.equals("CEDD")){
									CEDD_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									CEDD_min.set(f[0]);
									CEDD_max.set(f[1]);
								}else if(key3.equals("CLD")){
									CLD_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									CLD_min.set(f[0]);
									CLD_max.set(f[1]);
								}else if(key3.equals("FCTH")){
									FCTH_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									FCTH_min.set(f[0]);
									FCTH_max.set(f[1]);
								}else if(key3.equals("GCH")){
									GCH_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									GCH_min.set(f[0]);
									GCH_max.set(f[1]);
								}else if(key3.equals("JCD")){
									JCD_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									JCD_min.set(f[0]);
									JCD_max.set(f[1]);
								}else if(key3.equals("PHOG")){
									PHOG_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									PHOG_min.set(f[0]);
									PHOG_max.set(f[1]);
								}else if(key3.equals("SIFT")){
									SIFT_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									SIFT_min.set(f[0]);
									SIFT_max.set(f[1]);
								}else if(key3.equals("BIC")){
									BIC_sim.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
									float[] f = menoresMaiores.get(key3);
									BIC_min.set(f[0]);
									BIC_max.set(f[1]);
								}//ExpansaoGPI txt 1 a seguir
								else if(key3.equals("ACC_text1")){
									ACC_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_text1")){
									CEDD_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_text1")){
									CLD_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_text1")){
									FCTH_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_text1")){
									GCH_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_text1")){
									JCD_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_text1")){
									PHOG_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_text1")){
									SIFT_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_text1")){
									BIC_txt1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI txt 5 a seguir
								else if(key3.equals("ACC_text5")){
									ACC_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_text5")){
									CEDD_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_text5")){
									CLD_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_text5")){
									FCTH_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_text5")){
									GCH_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_text5")){
									JCD_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_text5")){
									PHOG_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_text5")){
									SIFT_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_text5")){
									BIC_txt5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI txt 10 a seguir
								else if(key3.equals("ACC_text10")){
									ACC_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_text10")){
									CEDD_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_text10")){
									CLD_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_text10")){
									FCTH_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_text10")){
									GCH_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_text10")){
									JCD_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_text10")){
									PHOG_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_text10")){
									SIFT_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_text10")){
									BIC_txt10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI txt 10 a seguir
								else if(key3.equals("ACC_text20")){
									ACC_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_text20")){
									CEDD_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_text20")){
									CLD_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_text20")){
									FCTH_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_text20")){
									GCH_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_text20")){
									JCD_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_text20")){
									PHOG_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_text20")){
									SIFT_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_text20")){
									BIC_txt20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//categorias
								else if(key3.equals("ACC_cat1")){
									ACC_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_cat1")){
									CEDD_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_cat1")){
									CLD_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_cat1")){
									FCTH_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_cat1")){
									GCH_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_cat1")){
									JCD_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_cat1")){
									PHOG_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_cat1")){
									SIFT_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_cat1")){
									BIC_cat1.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI cat 5 a seguir
								else if(key3.equals("ACC_cat5")){
									ACC_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_cat5")){
									CEDD_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_cat5")){
									CLD_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_cat5")){
									FCTH_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_cat5")){
									GCH_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_cat5")){
									JCD_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_cat5")){
									PHOG_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_cat5")){
									SIFT_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_cat5")){
									BIC_cat5.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI cat 10 a seguir
								else if(key3.equals("ACC_cat10")){
									ACC_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_cat10")){
									CEDD_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_cat10")){
									CLD_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_cat10")){
									FCTH_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_cat10")){
									GCH_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_cat10")){
									JCD_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_cat10")){
									PHOG_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_cat10")){
									SIFT_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_cat10")){
									BIC_cat10.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}//ExpansaoGPI cat 10 a seguir
								else if(key3.equals("ACC_cat20")){
									ACC_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CEDD_cat20")){
									CEDD_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("CLD_cat20")){
									CLD_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("FCTH_cat20")){
									FCTH_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("GCH_cat20")){
									GCH_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("JCD_cat20")){
									JCD_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("PHOG_cat20")){
									PHOG_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("SIFT_cat20")){
									SIFT_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}else if(key3.equals("BIC_cat20")){
									BIC_cat20.set(ranks.get(key3).get(key2) == null ? similaridadeMinima : ranks.get(key3).get(key2));
								}
							}
						}
						//inserir imagem rankeada no novo rank
						//System.out.println("Executando arvore...");

						float score = ind.execute_float(0, noargs);
						//System.out.println("Score da arvore: " + score);
						n_rank.put(key2, score);
						//System.out.println("Inseriu rank");
					}
					
				}
			}
			
			return n_rank;
		}
		
		static float avaliaRank(Map<String, Float> rank, int fileIndex){
			//File folder = new File(relevantPath);
			List<String> relevantes = dataController.getRelevant(fileIndex + ".txt");


			String[] rankStr = new String[rank.size()];
			float[] rankSim = new float[rank.size()];
			//System.out.println(rank.size());
			//float acc = 0.0f;
			int i = 0;
			for(String key : rank.keySet()){
			    rankStr[i] = key;
			    rankSim[i] = rank.get(key);
			    i++;
			}
			rankStr = ordena(rankStr, rankSim)[0];
			//float pat5 = Avaliadores.PAt5(rankStr, relevantes);
			float pat10 = Avaliadores.PAt10(rankStr, relevantes);
			//float acc = Avaliadores.mapValue(rankStr,relevantes);
			
			//float acc = (pat5 + pat10)/2.0f;
			//System.out.println(pat10);
			return pat10;//acc;
		}
		
		static Map<String, Map<String, Float> > normalizaRank(Map<String, Map<String, Float>> ranks){
			Map<String, Float> Nrank;
			Map<String, Map<String, Float>> Nranks = new HashMap<String, Map<String,Float>>();
			for(String key: ranks.keySet()){
				if(!key.contains("cat")){
					Nrank = new HashMap<String,Float>();
					float menor = 10000.0f, maior = 0.0f;
					for(String key2: ranks.get(key).keySet()){
						if(ranks.get(key).get(key2) > maior){
							maior = ranks.get(key).get(key2);
						}
						if(ranks.get(key).get(key2) < menor){
							menor = ranks.get(key).get(key2);
						}
					}
					//System.out.println(maior + " " + menor);
					for(String key2: ranks.get(key).keySet()){
						//System.out.println((ranks.get(key).get(key2) - menor) / (maior - menor));
						float newSim;
						//try{
						//newSim = (ranks.get(key).get(key2) - menor) / (maior - menor);
						newSim = ranks.get(key).get(key2) / maior;
						newSim = 1 - newSim;
						//}catch(Exception ex){
						//	newSim = 
						//}
						if(newSim == 0.0f){
							newSim = 0.001f;
						}
						//ranks.get(key).put(key2, newSim);
						Nrank.put(key2, newSim);
					}
					Nranks.put(key, Nrank);
				}else{
					Nranks.put(key, ranks.get(key));
				}
			}
			return Nranks;
		}
		
		
		static String[][] ordena(String[] paths, float[] distancias){
			int menor; // maior
			for(int i = 0; i < distancias.length - 1; i++){
				menor = i;
				for(int j = i+1; j < distancias.length; j++){
					if(distancias[j] > distancias[menor]){
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
		
		static float[] minMax(Map<String, Float> rank){
			float[] mM = { 1000000, 0};
			for(String key: rank.keySet()){
				Float f = rank.get(key);
				if(f > mM[1]){
					mM[1] = f;
				}else if (f < mM[0]){
					mM[0] = f;
				}
			}
			return mM;
		}
		
		static int[] selectImages(){
			int[] queries1 = {39,22,11,1,3,34,14,40,49,9,45,2,48,4,18,8,5,7,37,20};
			int[] queries2 = {17,23,44,29,20,46,43,47,15,26,39,22,11,1,3,34,14,40,49,9};
			int[] queries3 = {33,50,21,36,10,13,19,12,38,35,17,23,44,29,30,46,43,47,15,26};
			int[] queries4 = {42,31,16,27,25,32,6,24,41,28,33,50,21,36,10,13,19,12,38,35};
			int[] queries5 = {45,2,48,4,18,8,5,7,37,20,42,31,16,27,25,32,6,24,41,28};
			return folds[currentFold][currentState];
		}
	}
}
