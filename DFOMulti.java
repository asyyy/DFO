

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Dispersive Flies Optimisation (Multithreading)
 * @author Alexy
 * 
 */
public class DFOMulti extends binMeta
{	
	//Outil d'operation pour les data
	private DataOperator op;

	//Number of flies
	private int NP;

	//Stockage des datas et de leur valeur
	private Data [] tabData;
	private double [] tabValue;
	private Semaphore [] tabSem;
	private int size;

	//Stockage du meilleur data avec sa valeur
	private Semaphore best;


	private Data dataBest;
	private double valueBest;

	//Seuil de perturbation
	private double dt = 0.01;


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
	 * Initialise les sémaphores
	 */
	private void initSem() {
		for(int i=0;i<tabSem.length;i++) {
			tabSem[i] = new Semaphore(1);
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
	public DFOMulti(Data startPoint,Objective obj,long maxTime,int np)
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
			tabSem = new Semaphore[NP];
			best = new Semaphore(1);
			initFlies(this.size);
			initSem();
			
			op = new DataOperator();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}
	/**
	 * Cherche un voisin par la gauche
	 * @param i index 
	 * @return un index dans tabSem de libre
	 */
	public int trouverVoisinGauche(int i) {
		if(!tabSem[i].tryAcquire()) {
			if(i-- <= 0) {
				return trouverVoisinGauche(NP-1);
			}
			else {
				return trouverVoisinGauche(i-1);
			}

		}else {
			return i;
		}

	}
	/**
	 * Cherche un voisin par la droite
	 * @param i index 
	 * @return un index dans tabSem de libre
	 */
	public int trouverVoisinDroite(int i) {
		if(!tabSem[i].tryAcquire()) {
			if(i++ >= NP-1) {
				return trouverVoisinDroite(0);
			}
			else {
				return trouverVoisinDroite(i+1);
			}

		}else {
			return i;
		}

	}
	
	/**
	 * 
	 * @author Alexy
	 * Thread parcourant data par le debut pour effectuer l'optimisation
	 */
	public class parcourtCroissant extends Thread{
		private long starTime;
		private long maxTime2;
		public parcourtCroissant(long startime,long maxTime) {
			this.starTime = startime;
			this.maxTime2 = maxTime;
		}

		@Override
		public void run() {
			int i = 0;

			while (System.currentTimeMillis() - this.starTime < this.maxTime2) {

				if(tabSem[i].tryAcquire()) {
					int left = trouverVoisinGauche(i);
					int right  = trouverVoisinDroite(i);
					Data dNeighbour;
					
					//Trouve le meilleur voisin
					if(tabValue[left]< tabValue[right]) {
						dNeighbour = tabData[right];

					}else {
						dNeighbour = tabData[left];
					}
					tabSem[left].release();
					tabSem[right].release();

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
						//System.out.println(obj.value(calcul3));;
						tabValue[i] = obj.value(calcul3);

					}					
					tabSem[i].release();
				}
				if(i == NP-1) i = 0 ;
				else i++;

			}

		}
	}
	/**
	 * 
	 * @author Alexy
	 * Thread parcourant data par la fin pour effectuer l'optimisation
	 */
	public class parcourtDecroissant extends Thread{
		private long starTime;
		private long maxTime2;
		public parcourtDecroissant(long startime,long maxTime) {
			this.starTime = startime;
			this.maxTime2 = maxTime;
		}

		@Override
		public void run() {
			int i = NP-1;

			while (System.currentTimeMillis() - this.starTime < this.maxTime2) {

				if(tabSem[i].tryAcquire()) {
					int left = trouverVoisinGauche(i);
					int right  = trouverVoisinDroite(i);
					Data dNeighbour;

					if(tabValue[left]< tabValue[right]) {
						dNeighbour = tabData[right];

					}else {
						dNeighbour = tabData[left];
					}
					tabSem[left].release();
					tabSem[right].release();

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
					tabSem[i].release();
				}
				if(i == 0) i = NP-1 ;
				else i--;

			}

		}
	}
	
	/**
	 * 
	 * @author Alexy
	 * Thread parcourant tabData par le début pour trouver le meilleur
	 */
	public class searchBestCroissant extends Thread {
		private long starTime;
		private long maxTime2;
		public searchBestCroissant(long startime,long maxTime) {
			this.starTime = startime;
			this.maxTime2 = maxTime;
		}

		@Override
		public void run() {
			int i = 0;
			while (System.currentTimeMillis() - this.starTime < this.maxTime2) {
				if(tabValue[i] < valueBest) {
					while(!best.tryAcquire())
					valueBest = tabValue[i]; 
					dataBest = tabData[i];
					best.release();
				}
				if(i == NP-1) i = 0 ;
				else i++;
			}
		}
	}
	/**
	 * 
	 * @author Alexy
	 * Thread parcourant tabData par la fin pour trouver le meilleur
	 */
	public class searchBestDecroissant extends Thread {
		private long starTime;
		private long maxTime2;

		public searchBestDecroissant(long startime,long maxTime) {
			this.starTime = startime;
			this.maxTime2 = maxTime;
		}

		@Override
		public void run() {
			int i = NP-1;
			while (System.currentTimeMillis() - this.starTime < this.maxTime2) {
				if(tabValue[i] < valueBest) {
					while(!best.tryAcquire())
					valueBest = tabValue[i]; 
					dataBest = tabData[i];
					best.release();
				}
				if(i == 0) i = NP-1 ;
				else i--;
			}
		}
	}

	@Override
	public void optimize()  // by Dispersive Flies Optimisation
	{


		findBest();
		long startime = System.currentTimeMillis();
		Thread t1 = new parcourtCroissant(startime,this.maxTime);
		Thread t2 = new parcourtDecroissant(startime,this.maxTime);

		Thread th  = new searchBestCroissant(startime,this.maxTime);
		Thread th2 = new searchBestDecroissant(startime,this.maxTime);

		// loop are in thread
		
		t1.start();
		t2.start();
		th.start();
		th2.start();


		while (System.currentTimeMillis() - startime < this.maxTime)
		this.solution = dataBest;
		this.objValue = valueBest;
	}

	// main
	public static void main(String[] args)
	{
		int ITMAX = 10000;  // number of iterations
		int np = 1000;
		// BitCounter
		
		int n = 50;
		Objective obj = new BitCounter(n);
		Data D = obj.solutionSample();
		DFOMulti rw = new DFOMulti(D,obj,ITMAX,np);
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
		DFOMulti rw1 = new DFOMulti(D1,obj1,ITMAX,np);
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
		DFOMulti rw2 = new DFOMulti(D2,cp,ITMAX,np);
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

