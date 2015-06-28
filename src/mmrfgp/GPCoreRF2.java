package mmrfgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
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
import org.jgap.util.PersistableObject;

import utilidades.FileHandler;
import utilidades.Sqrt;

import com.thoughtworks.xstream.XStream;

/**
 * Classe principal do experimento
 */

public class GPCoreRF2 extends GPProblem {
	
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
	static String basePath = utilidades.ProjectSetup.ranksBasePath;
	static String queryPath = utilidades.ProjectSetup.ranksConsultasPath;//"consultasAvaliadas/colecaoAvulso_semParticao";
	static String relevantPath = utilidades.ProjectSetup.relevantesPath;//"Relevantes/avulso";
//	static ModeloVetorial MV = new ModeloVetorial();

	public GPCoreRF2(GPConfiguration a_conf)
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
	
	public static void runSavedProgram(String fileName) throws Exception{
		folds = utilidades.FoldGenerator.breakSetSelected();	
		System.out.println("loading files...");
		dataController = new FileHandler();
		dataController.loadData(queryPath);
		dataController.loadRelevants(relevantPath);
		
		GPConfiguration config = new GPConfiguration();
		config.reset();

		int numeroGeracoes = 3;//30;
		config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
		config.setMaxInitDepth(10);
		config.setPopulationSize(10);//(300);
		config.setMaxCrossoverDepth(6);
		config.setMinInitDepth(2);
		config.setMutationProb(0.05f);
		config.setCrossoverProb(0.9f);
		config.setReproductionProb(0.05f);
		config.setFitnessFunction(new GPCoreRF2.FormulaFitnessFunction());
		config.setStrictProgramCreation(true);
		GPProblem problem = new GPCoreRF2(config);

		GPGenotype gp = problem.create();
		
		
//		org.jgap.util.PersistableObject po = new PersistableObject("teste!");
//		IGPProgram program = loadProgram();
		
		for(currentFold = 0; currentFold < 5; currentFold++){
			currentState = 0; //teste
			double result = FormulaFitnessFunction.computeRawFitness(null);		
			System.out.println("resultado: "+ result );
		}
	}
	
	public static void runExperiment() throws Exception{
		GPConfiguration config = new GPConfiguration();

		int numeroGeracoes = 1;//30;
		config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
		config.setMaxInitDepth(10);
		config.setPopulationSize(4);//(300);
		config.setMaxCrossoverDepth(6);
		config.setMinInitDepth(2);
		config.setMutationProb(0.05f);
		config.setCrossoverProb(0.9f);
		config.setReproductionProb(0.05f);
		config.setFitnessFunction(new GPCoreRF2.FormulaFitnessFunction());
		config.setStrictProgramCreation(true);
		GPProblem problem = new GPCoreRF2(config);

		GPGenotype gp;// = problem.create();

		folds = utilidades.FoldGenerator.breakSetSelected();
		
		System.out.println("loading files...");
		dataController = new FileHandler();
		dataController.loadData(queryPath, basePath);
		dataController.loadRelevants(relevantPath);
		
		
	
//		
		for(currentFold = 0; currentFold < 5; currentFold++){
//			
			System.out.println("==================================================\nCriando populaÁ„o inicial para a parti√ß√£o " + String.valueOf(currentFold + 1));
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
				currentState = 0; //teste
				validationPrec = FormulaFitnessFunction.computeRawFitness(gp.getFittestProgram());
				System.out.println("GeraÁ„o " + i +" -- Treino: " + trainingPrec + " - ValidaÁ„o: " + validationPrec);
				//if( i > 5 && validationPrec < trainingPrec)
					//break;
				i++;
			}
			gp.outputSolution(gp.getFittestProgram());
		}
	}

	public static void main(String[] args)
	{
		try{
			runSavedProgram("teste!");
			//runExperiment();
		}catch(Exception ex){
			ex.printStackTrace();
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
			 * Esta fun√ß√£o recebe uma pasta e lista todos os arquivos contidos nela.
			 * Lista dos arquivos referentes as consultas.
			 * Para cada consulta, retorna o rank gerado para cada descritor.
			 * Combina os ranks em um novo atrav√©s do cromossomo.
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
			
			numberOfQueries = imagensSelecionadas.length;
			
			
			for(int i = 0; i < numberOfQueries; i++){
				ArrayList<String> imagensConsulta = new ArrayList<String>();
				int currentQuery = imagensSelecionadas[i];
				
				List<Map<String, Float>> relevantRanks = new ArrayList<Map<String,Float>>();
				
				for(int j =0 ; j < 5; j++){
					imagensConsulta.add(String.valueOf(currentQuery));
					for(String consulta : imagensConsulta){
						//para cada imagem da lista de queries
						ranks = new HashMap<String, Map<String, Float> >();
						menoresMaiores = new HashMap<String, float[]>();
						for (String key : descritores) {
							//ranks["CEDD"] -> resultados de uma busca usando a currentQuery como consulta
							ranks.put(key, dataController.getRank(key ,consulta.replace(".jpg", "") + ".txt"));
							float[] f = minMax(ranks.get(key));
							menoresMaiores.put(key, f);
						}
						//dado os k ranks, combina eles usando o cromossomo atual
						ranks = normalizaRank(ranks);
		
						novoRank = combinaRank(ranks, menoresMaiores, ind);
						relevantRanks.add(novoRank);
					
					}
					//combina os ranks TODO
					Map<String, Float> rankCombinado = RF.combinaRanks(relevantRanks);
					
					vetorFitness[i] = avaliaRank(rankCombinado, imagensSelecionadas[i], imagensConsulta);
					if(vetorFitness[i] < 1){
						imagensConsulta = (ArrayList) RF.selecionaRelevantesSimulado(rankCombinado, String.valueOf(imagensSelecionadas[i])); //selecionaRelevantesSimulado(novoRank, String.valueOf(imagensSelecionadas[i]));
						
					}else{
						break;
					}
					
					
					//avalia a precis√£o do rank usando MAP
				}
				//System.out.println(vetorFitness[i]);
			}
			
			float fitness = 0.0f;
			for(int i = 0; i <numberOfQueries; i++){
				fitness += vetorFitness[i];
			}
			fitness /= (float)numberOfQueries;
			//System.out.println("Valor do fitness: " + fitness);
			return fitness;
		}
		
		static Map<String, Float> combinaRank(Map<String, Map<String, Float>> relevantRanks){
			return new HashMap<String, Float>();
		}
		
		static Map<String, Float> combinaRank(Map<String, Map<String, Float> > ranks, Map<String, float[]> menoresMaiores, final IGPProgram ind){
			Map<String, Float> n_rank = new HashMap<String, Float>();
			Object[] noargs = new Object[0];
			// TO-DO
			//System.out.println("Combinando rank para avaliar arvore...");
			for (String key : ranks.keySet()) {
				//para cada descritor
				
				for (String key2 : ranks.get(key).keySet()) {
					//para cada imagem recuperada pelo descritor usando a currentQuery como consulta
					
					//ACC_sim, CEDD_sim, CLD_sim, FCTH_sim, GCH_sim, JCD_sim, PHOG_sim
					if(n_rank.get(key2) == null){
						//se ja nao estiver contida no novo rank
						// ADICIONAR FLOAT 'RANK.GET(KEY).GET(KEY2)' AO CROMOSSOMO PARA O ALELO 'KEY'
						try{
						
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
						}catch(NullPointerException ex){
							System.out.println("key: "+ key + "key2: " + key2);
						}
						
						for (String key3 : ranks.keySet()) {
							if(!key3.equals(key)){
								//para cada ocorrencia da mesma imagem nos demais ranks
								//ADICIONAR FLOAT 'RANK.GET(KEY3).GET(KEY2)' AO CROMOSSOMO PARA O ALELO 'KEY3'
								//System.out.println("Agora tribuindo valor para " + key3);
								
								try{
								
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
								
								}catch(NullPointerException ex){
									System.out.println("key3: "+ key3 + "key2: " + key2);
								}
							}
						}
						//inserir imagem rankeada no novo rank
						//System.out.println("Executando arvore...");
						float score;
						
						if(ind != null){
							score = ind.execute_float(0, noargs);
						}else{
							score = executeProgram();
						}
						//System.out.println("Score da arvore: " + score);
						n_rank.put(key2, score);
						//System.out.println("Inseriu rank");
					}
					
				}
			}
			
			return n_rank;
		}
		
		static float avaliaRank(Map<String, Float> rank, int fileIndex, List<String> consultas){

			Map<String,Float> novoRank = new HashMap<String,Float>();
			List<String> relevantes = dataController.getRelevant(fileIndex + ".txt");
			boolean isFromQuery = false;
			for(String resposta : rank.keySet()){
				isFromQuery = false;
				for(String consulta: consultas){
					if(resposta.equals(consulta)){
						isFromQuery = true;
						break;
					}
				}
				if(!isFromQuery)
					novoRank.put(resposta,rank.get(resposta));
			}

			String[] rankStr = new String[novoRank.size()];
			float[] rankSim = new float[novoRank.size()];
			//System.out.println(rank.size());
			//float acc = 0.0f;
			int i = 0;
			for(String key : novoRank.keySet()){
			    rankStr[i] = key;
			    rankSim[i] = novoRank.get(key);
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
			// transforma em similaridade
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
			int maior;
			// decrescente
			for(int i = 0; i < distancias.length - 1; i++){
				maior = i;
				for(int j = i+1; j < distancias.length; j++){
					if(distancias[j] > distancias[maior]){
						maior = j;
					}
				}
				if(maior != i){
					float temp;
					String tempS;
					
					temp = distancias[i];
					distancias[i] = distancias[maior];
					distancias[maior] = temp;
					
					tempS = paths[i];
					paths[i] = paths[maior];
					paths[maior] = tempS;
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
		
		//((((GCH * ((GCH * JCD_cat1) * ((GCH * JCD_cat1) * JCD_cat1))) * (GCH * JCD_cat1)) * JCD_cat1) * JCD_cat1) * (GCH * JCD_cat1)
		
		public static float executeProgram(){
			float acc_sim, cedd_sim, cld_sim, fcth_sim, gch_sim, jcd_sim, phog_sim, sift_sim, bic_sim;
			float acc_txt1, cedd_txt1, cld_txt1, fcth_txt1, gch_txt1, jcd_txt1, phog_txt1, sift_txt1, bic_txt1;
			float acc_txt5, cedd_txt5, cld_txt5, fcth_txt5, gch_txt5, jcd_txt5, phog_txt5, sift_txt5, bic_txt5;
			float acc_txt10, cedd_txt10, cld_txt10, fcth_txt10, gch_txt10, jcd_txt10, phog_txt10, sift_txt10, bic_txt10;
			float acc_txt20, cedd_txt20, cld_txt20, fcth_txt20, gch_txt20, jcd_txt20, phog_txt20, sift_txt20, bic_txt20;
			
			float acc_cat1, cedd_cat1, cld_cat1, fcth_cat1, gch_cat1, jcd_cat1, phog_cat1, sift_cat1, bic_cat1;
			float acc_cat5, cedd_cat5, cld_cat5, fcth_cat5, gch_cat5, jcd_cat5, phog_cat5, sift_cat5, bic_cat5;
			float acc_cat10, cedd_cat10, cld_cat10, fcth_cat10, gch_cat10, jcd_cat10, phog_cat10, sift_cat10, bic_cat10;
			float acc_cat20, cedd_cat20, cld_cat20, fcth_cat20, gch_cat20, jcd_cat20, phog_cat20, sift_cat20, bic_cat20;
			
			float acc_min, cedd_min, cld_min, fcth_min, gch_min, jcd_min, phog_min, sift_min, bic_min;
			float acc_max, cedd_max, cld_max, fcth_max, gch_max, jcd_max, phog_max, sift_max, bic_max;
			
			////
			acc_sim = (float) ACC_sim.getValue(); 
			cedd_sim = (float) CEDD_sim.getValue(); 
			cld_sim= (float) CLD_sim.getValue(); 
			fcth_sim= (float) FCTH_sim.getValue(); 
			gch_sim= (float) GCH_sim.getValue(); 
			jcd_sim= (float) JCD_sim.getValue(); 
			phog_sim= (float) PHOG_sim.getValue(); 
			sift_sim= (float) SIFT_sim.getValue(); 
			bic_sim= (float) BIC_sim.getValue(); 
			acc_txt1= (float) ACC_txt1.getValue(); 
			cedd_txt1= (float) CEDD_txt1.getValue(); 
			cld_txt1= (float) CLD_txt1.getValue(); 
			fcth_txt1= (float) FCTH_txt1.getValue(); 
			gch_txt1= (float) GCH_txt1.getValue(); 
			jcd_txt1= (float) JCD_txt1.getValue(); 
			phog_txt1= (float) PHOG_txt1.getValue(); 
			sift_txt1= (float) SIFT_txt1.getValue(); 
			bic_txt1= (float) BIC_txt1.getValue(); 
			acc_txt5= (float) ACC_txt5.getValue(); 
			cedd_txt5= (float) CEDD_txt5.getValue(); 
			cld_txt5= (float) CLD_txt5.getValue(); 
			fcth_txt5= (float) FCTH_txt5.getValue(); 
			gch_txt5= (float) GCH_txt5.getValue(); 
			jcd_txt5= (float) JCD_txt5.getValue(); 
			phog_txt5= (float) PHOG_txt5.getValue(); 
			sift_txt5= (float) SIFT_txt5.getValue(); 
			bic_txt5= (float) BIC_txt5.getValue(); 
			acc_txt10= (float) ACC_txt10.getValue(); 
			cedd_txt10= (float) CEDD_txt10.getValue(); 
			cld_txt10= (float) CLD_txt10.getValue(); 
			fcth_txt10= (float) FCTH_txt10.getValue(); 
			gch_txt10= (float) GCH_txt10.getValue(); 
			jcd_txt10= (float) JCD_txt10.getValue(); 
			phog_txt10= (float) PHOG_txt10.getValue(); 
			sift_txt10= (float) SIFT_txt10.getValue(); 
			bic_txt10= (float) BIC_txt10.getValue(); 
			acc_txt20= (float) ACC_txt20.getValue(); 
			cedd_txt20= (float) CEDD_txt20.getValue(); 
			cld_txt20= (float) CLD_txt20.getValue(); 
			fcth_txt20= (float) FCTH_txt20.getValue(); 
			gch_txt20= (float) GCH_txt20.getValue(); 
			jcd_txt20= (float) JCD_txt20.getValue(); 
			phog_txt20= (float) PHOG_txt20.getValue(); 
			sift_txt20= (float) SIFT_txt20.getValue(); 
			bic_txt20= (float) BIC_txt20.getValue(); 
			
			acc_cat1= (float) ACC_cat1.getValue(); 
			cedd_cat1= (float) CEDD_cat1.getValue(); 
			cld_cat1= (float) CLD_cat1.getValue();
			fcth_cat1= (float) FCTH_cat1.getValue(); 
			gch_cat1= (float) GCH_cat1.getValue(); 
			jcd_cat1= (float) JCD_cat1.getValue(); 
			phog_cat1= (float) PHOG_cat1.getValue(); 
			sift_cat1= (float) SIFT_cat1.getValue(); 
			bic_cat1= (float) BIC_cat1.getValue(); 
			acc_cat5= (float) ACC_cat5.getValue(); 
			cedd_cat5= (float) CEDD_cat5.getValue(); 
			cld_cat5= (float) CLD_cat5.getValue(); 
			fcth_cat5= (float) FCTH_cat5.getValue(); 
			gch_cat5= (float) GCH_cat5.getValue(); 
			jcd_cat5= (float) JCD_cat5.getValue(); 
			phog_cat5= (float) PHOG_cat5.getValue(); 
			sift_cat5= (float) SIFT_cat5.getValue(); 
			bic_cat5= (float) BIC_cat5.getValue(); 
			acc_cat10= (float) ACC_cat10.getValue(); 
			cedd_cat10= (float) CEDD_cat10.getValue(); 
			cld_cat10= (float) CLD_cat10.getValue(); 
			fcth_cat10= (float) FCTH_cat10.getValue(); 
			gch_cat10= (float) GCH_cat10.getValue(); 
			jcd_cat10= (float) JCD_cat10.getValue(); 
			phog_cat10= (float) PHOG_cat10.getValue(); 
			sift_cat10= (float) SIFT_cat10.getValue(); 
			bic_cat10= (float) BIC_cat10.getValue(); 
			acc_cat20= (float) ACC_cat20.getValue(); 
			cedd_cat20= (float) CEDD_cat20.getValue(); 
			cld_cat20= (float) CLD_cat20.getValue(); 
			fcth_cat20= (float) FCTH_cat20.getValue(); 
			gch_cat20= (float) GCH_cat20.getValue(); 
			jcd_cat20= (float) JCD_cat20.getValue(); 
			phog_cat20= (float) PHOG_cat20.getValue(); 
			sift_cat20= (float) SIFT_cat20.getValue(); 
			bic_cat20= (float) BIC_cat20.getValue(); 
			
			acc_min= (float) ACC_min.getValue(); 
			cedd_min= (float) CEDD_min.getValue(); 
			cld_min= (float) CLD_min.getValue(); 
			fcth_min= (float) FCTH_min.getValue(); 
			gch_min= (float) GCH_min.getValue(); 
			jcd_min= (float) JCD_min.getValue(); 
			phog_min= (float) PHOG_min.getValue(); 
			sift_min= (float) SIFT_min.getValue(); 
			bic_min= (float) BIC_min.getValue(); 
			acc_max= (float) ACC_max.getValue(); 
			cedd_max= (float) CEDD_max.getValue(); 
			cld_max= (float) CLD_max.getValue(); 
			fcth_max= (float) FCTH_max.getValue(); 
			gch_max= (float) GCH_max.getValue(); 
			jcd_max= (float) JCD_max.getValue(); 
			phog_max= (float) PHOG_max.getValue(); 
			sift_max= (float) SIFT_max.getValue(); 
			bic_max= (float) BIC_max.getValue();


			return ((((gch_sim * ((gch_sim * jcd_cat1) * ((gch_sim * jcd_cat1) * jcd_cat1))) * (gch_sim * jcd_cat1)) * jcd_cat1) * jcd_cat1) * (gch_sim * jcd_cat1);
		}
	}
}
