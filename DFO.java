

import java.util.Random;


/**
 * Dispersive Flies Optimisation (Sequentiel)
 * @author Alexy
 * 
 */
public class DFO extends binMeta
{	
	//Outil d'operation pour les data
	DataOperator op;
	
	//Number of flies
	int NP;
	
	//Stockage des datas et de leur valeur
	Data [] tabData = new Data[NP];
	double [] tabValue = new double[NP];
	int size;
	
	//Stockage du meilleur data avec sa valeur
	Data dataBest;
	double valueBest;

	
	//Seuil de perturbation
	double dt = 0.001;

	
	/**
	 * Initialise tabData et tabValue
	 * @param size taille de bit des Data
	 */
	private void initFlies(int size) {
		for(int i = 0;i<NP;i++) {
			tabData[i] = new Data(size,0.5);
			tabValue[i] = obj.value(tabData[i]);
		}
	}
	/**
	 * Calcul de la valeur de chaque Data
	 */
	private void fitness() {
		for(int i = 0;i<NP;i++) {
			tabValue[i] = obj.value(tabData[i]); 
		}
	}
	
	/**
	 * Met à jour la meilleur Fly.
	 */
	private void findBest() {	
		for(int i = 0;i<NP;i++) {
			if(tabValue[i] < valueBest) {
				valueBest = tabValue[i];
				dataBest = tabData[i];
			}
		}
	}
	
	// DFO constructor
	public DFO(Data startPoint,Objective obj,long maxTime,int np)
	{
			
		try
		{
			String msg = "Impossible to create DFO object: ";
			if (maxTime <= 0) throw new Exception(msg + "the maximum execution time is 0 or even negative");
			this.maxTime = maxTime;
			if (startPoint == null) throw new Exception(msg + "the reference to the starting point is null");
			this.solution = startPoint;
			if (obj == null) throw new Exception(msg + "the reference to the objective is null");
			this.obj = obj;
			this.objValue = this.obj.value(this.solution);
			this.metaName = "DFO";
			this.NP = np;
			this.size = startPoint.numberOfBits();
			dataBest = startPoint;
			valueBest = obj.value(dataBest);
			tabValue = new double[NP];
			tabData = new Data[NP];
			initFlies(this.size);
			op = new DataOperator();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	@Override
	public void optimize()  // by Dispersive Flies Optimisation
	{
		
		long startime = System.currentTimeMillis();
		
		// main loop
		while (System.currentTimeMillis() - startime < this.maxTime)
		{
			//Calcul la valeur de chaque Fly
			//fitness();
			
			//MAJ Fly avec la meilleur valeur
			findBest();

			//Pour chaque Fly
			for(int i = 0;i< NP;i++) {
							
				int left;
				int right;
				
				Data dNeighbour;
				
				//Trouver voisins (en simulant du mouvement)
				if(i-1 <= 0) left = NP-1;
				else left = i-1;
				
				if(i+1 >= NP) right = 0;
				else right = i+1;
			
				//Trouve le meilleur voisin
				if(tabValue[left]< tabValue[right]) {
					dNeighbour = tabData[right];
				}else {
					dNeighbour = tabData[left];
				}
				
				Random r = new Random();
				
				if(r.nextFloat()<dt) {
					//Reset de Fly et MAJ de sa valeur
					tabData[i] = new Data(size,0.5);
					tabValue[i] = obj.value(tabData[i]);
				}else {
					Data random = new Data(size,r.nextDouble());
					
					Data calcul = op.add3(dataBest, tabData[i]);
					
					Data calcul2 = op.mul3(random, calcul,r.nextDouble());
					
					Data calcul3 = op.sub3(dNeighbour, calcul2);
					
					tabData[i] = calcul3;
					tabValue[i] = obj.value(calcul3);
					
				}
			}
			
		}
		
		fitness();
		findBest();
		
		this.solution = dataBest;
		this.objValue = valueBest;
	}

	// main
	public static void main(String[] args)
	{
		int ITMAX = 10000;  // number of iterations
		int np = 100;
		// BitCounter
		
		int n = 50;
		Objective obj = new BitCounter(n);
		Data D = obj.solutionSample();
		DFO rw = new DFO(D,obj,ITMAX,np);
		System.out.println(rw);
		System.out.println("starting point : " + rw.getSolution());
		System.out.println("optimizing ...");
		rw.optimize();
		System.out.println(rw);
		System.out.println("solution : " + rw.getSolution());
		System.out.println();
		
		
		// Fermat
		int exp = 2;
		int ndigits = 10;
		Objective obj1 = new Fermat(exp,ndigits);
		Data D1 = obj1.solutionSample();
		DFO rw1 = new DFO(D1,obj1,ITMAX,np);
		System.out.println(rw1);
		System.out.println("starting point : " + rw1.getSolution());
		System.out.println("optimizing ...");
		rw1.optimize();
		System.out.println(rw1);
		System.out.println("solution : " + rw1.getSolution());
		Data x = new Data(rw1.solution,0,ndigits-1);
		Data y = new Data(rw1.solution,ndigits,2*ndigits-1);
		Data z = new Data(rw1.solution,2*ndigits,3*ndigits-1);
		System.out.print("equivalent to the equation : " + x.posLongValue() + "^" + exp + " + " + y.posLongValue() + "^" + exp);
		if (rw1.objValue == 0.0)
			System.out.print(" == ");
		else
			System.out.print(" ?= ");
		System.out.println(z.posLongValue() + "^" + exp);
		System.out.println();
		
		
		// ColorPartition
		int n2 = 4;  int m = 15;
		ColorPartition cp = new ColorPartition(n2,m);
		Data D2 = cp.solutionSample();
		DFO rw2 = new DFO(D2,cp,ITMAX,np);
		System.out.println(rw2);
		System.out.println("starting point : " + rw2.getSolution());
		System.out.println("optimizing ...");
		rw2.optimize();
		System.out.println(rw2);
		System.out.println("solution : " + rw2.getSolution());
		cp.value(rw2.solution);
		System.out.println();
		System.out.println("corresponding to the matrix :\n" + cp.show());
		
	}
}

