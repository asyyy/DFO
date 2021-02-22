
/* Fermat class
 *
 * binMeta project
 *
 * last update: Nov 1, 2020
 * 
 * AM
 * 
 * Le dernier théorème de Fermat
 * https://fr.wikipedia.org/wiki/Dernier_th%C3%A9or%C3%A8me_de_Fermat
 * "Il n'existe pas de nombres entiers strictement positifs x,y,z tels que : 
 * 			x^n + y^n = z^n,
 * dès que n est un entier strictement supérieur à 2."
 * 
 * si n = 1 alors infinité solution | exemple : 1^1 + 45^1 = 46^1
 * 
 * si n = 2 alors infinité solution | exemple : 3^2 + 4^2 = 5^2 (9+16=25)
 * 
 * si n>2 alors aucune solution(en entier positif non nul)
 * donc elle n'en a aucun pour multiples de n (x^(n*k) OU x^n^k)
 * 
 * Utilité? 
 * pistes -> Bit représenté en binaire (puissance de 2)
 */

public class Fermat extends Objective
{
   private int exp;  // exponent for objective: | z^exp - x^exp - y^exp |
   private int ndigits;  // expected number of digits for the representation of the integers x, y and z

   // Constructor: it generates a Fermat objective with the specified exponent, and the
   //              specified number of digits for the representation of the integers 
   public Fermat(int exp,int ndigits)
   {
      try
      {
         if (exp <= 0) throw new Exception("Impossible to create Fermat objective: the exponent is 0 or negative");
         this.exp = exp;
         if (ndigits <= 0) throw new Exception("Impossible to create Fermat objective: the specified number of digits is 0 or negative");
         if (ndigits > 52) throw new Exception("Impossible to create Fermat objective: the specified number of digits is too large");
         this.ndigits = ndigits;
         this.name = "Fermat";
         this.lastValue = null;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }
   /**
    * nombre de bit donnée en entre x 3. pourcentage nombre aleatoire
    * si ndigits = 6, random = 0.5
    * alors on aura 18 nombres avec 50% chance de valoir 1 
    */
   @Override
   public Data solutionSample()
   {
      return new Data(3*this.ndigits,0.5);
   }
   /*Decouper une data en 3(x,y,z) de gauche a droite ( lancer main pour comprendre)
    * 
    */
   @Override
   public double value(Data D)
   {
      try
      {
         if (D.numberOfBits() != 3*this.ndigits) 
           throw new Exception("Impossible to evaluate Fermat objective: number of bits in Data object differs from expected value");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }

      // objective evaluation: it computes | z^exp - x^exp - y^exp |
      // 
      Data x = new Data(D,0,this.ndigits - 1);
      Data y = new Data(D,this.ndigits,2*this.ndigits - 1);   
      Data z = new Data(D,2*this.ndigits,3*this.ndigits - 1);
      long lx = x.posLongValue();
      double xx = 1.0;  for (int k = 0; k < this.exp; k++)  xx = lx*xx;
      long ly = y.posLongValue();
      double yy = 1.0;  for (int k = 0; k < this.exp; k++)  yy = ly*yy;
      long lz = z.posLongValue();
      double zz = 1.0;  for (int k = 0; k < this.exp; k++)  zz = lz*zz;

      this.lastValue = Math.abs(zz - xx - yy);
      return this.lastValue;
   }

   // main
   public static void main(String[] args)
   {
      int exp = 2;
      int ndigits = 6;
      Objective obj = new Fermat(exp,ndigits);
      Data D = obj.solutionSample();
      Data x = new Data(D,0,ndigits - 1);
      Data y = new Data(D,ndigits,2*ndigits - 1);
      Data z = new Data(D,2*ndigits,3*ndigits - 1);
      System.out.println(obj);
      System.out.println("sample solution : " + D);
      System.out.print("corresponding to the equation : ");
      System.out.println(x.posLongValue() + "^(" + exp + ") + " + y.posLongValue() + "^(" + exp + ") ?= " + z.posLongValue() + "^(" + exp + ")");
      System.out.println("x = " + x + " to decimal =" + (x.posLongValue()-1));
      System.out.println("y = " + y + " to decimal =" + (y.posLongValue()-1));
      System.out.println("z = " + z + " to decimal =" + (z.posLongValue()-1));
      System.out.println("evaluating the objective function in the sample solution : " + obj.value(D));
      System.out.println(obj);
   }
}

