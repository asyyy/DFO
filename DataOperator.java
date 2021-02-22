import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Class pour faire des opérations sur des Data
 * @author Alexy
 *
 */
public class DataOperator {

	public DataOperator() {}
	/**
	 * Note :
	 * 
	 *  - Opérateur >>> 
	 * Décale les bits vers la droite(divise par 2 à chaque décalage).
	 *
	 *	- (d1.data.get(i)&0xff) (+/-/*) (d2.data.get(i)&0xff)+overflow;
	 *   On fait l'operation & entre un byte et 255[1111 1111] pour gerer les nombre
	 *   négatifs.
	 *   Overflow pour gerer l'overflow d'un byte sur le prochain
	 */
	
	/**
	 * Reprend le principe de la distance de Hamming, regarde les bits différents et effectue une action si bit différent
	 * @param d1 Data
	 * @param d2 Data
	 * @return Data avec des 1 ou 0 sur les bits différents entre d1 et d2 selon 2 doubles randoms
	 */
	public Data mul3(Data d1, Data d2, double r) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			List<Data> res = new ArrayList<Data>();


			Random r2 = new Random();
			int i =0;
			int max = d1.numberOfBits();
			while(i<max ) {
				if((d1.getCurrentBit() != d2.getCurrentBit()) && r2.nextFloat() < r) {
					res.add(new Data(1,1));
				}else {
					res.add(new Data(1,0));
				}
				d1.moveToNextBit();
				d2.moveToNextBit();
				i++;
			}


			return new Data(res);
		}
		return null;
	}
	/**
	 * Reprend le principe de la distance de Hamming, regarde les bits différents et effectue une action si bit différent
	 * @param d1 Data
	 * @param d2 Data
	 * @return Data avec des 0 sur les bits différents entre d1 et d2
	 */
	public Data sub3(Data d1, Data d2) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			List<Data> res = new ArrayList<Data>();
			int i =0;
			int max = d1.numberOfBits();
			while(i<max ) {
				if((d1.getCurrentBit() != d2.getCurrentBit()) && (d1.getCurrentBit() == 0 ||  d2.getCurrentBit() == 0)) {
					res.add(new Data(1,0));
				}else {
					res.add(new Data(1,1));
				}
				d1.moveToNextBit();
				d2.moveToNextBit();
				i++;
			}

			return new Data(res);
		}
		return null;
	}
	/**
	 * Reprend le principe de la distance de Hamming, regarde les bits différents et effectue une action si bit différent
	 * @param d1 Data
	 * @param d2 Data
	 * @return Data avec des 1 sur les bits différents entre d1 et d2
	 */
	public Data add3(Data d1, Data d2) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			List<Data> res = new ArrayList<Data>();
			int i =0;
			int max = d1.numberOfBits();
			while(i<max ) {

				if((d1.getCurrentBit() != d2.getCurrentBit()) && (d1.getCurrentBit() == 1 || d2.getCurrentBit() == 1)) {
					res.add(new Data(1,1));
				}else {
					res.add(new Data(1,0));	
				}
				d1.moveToNextBit();
				d2.moveToNextBit();
				i++;
			}

			return new Data(res);
		}
		return null;
	}

	/*********************************Ancienne Partie****************************/
	/**
	 * Soustraire 2 list<Byte> de 2 Data
	 * @param d1 Data
	 * @param d2 Data
	 * @return d1 - d2
	 */
	public Data sub2(Data d1,Data d2) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			int N = d1.numberOfBytes();
			Data res = new Data(d1.numberOfBits(),false);
			res.data.clear();

			for(int i = N-1,overflow=0;i>= 0;i--) {
				int v = (d1.data.get(i)&0xff) - (d2.data.get(i)&0xff)+overflow;
				res.data.add((byte) v);
				overflow=v>>>8;
			}
			return res;
		}
		return null;
	}

	/**
	 * Additionne 2 Data
	 * @param d1 Data
	 * @param d2 Data
	 * @return d1 - d2
	 */
	public Data add2(Data d1,Data d2) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			int N = d1.numberOfBytes();
			Data res = new Data(d1.numberOfBits(),false);
			res.data.clear();

			for(int i = N-1,overflow=0;i>= 0;i--) {
				int v = (d1.data.get(i)&0xff) + (d2.data.get(i)&0xff)+overflow;
				/*System.out.println();
					System.out.println("d1 " + (d1.data.get(i)&0xff));
					System.out.println("d2 " + (d2.data.get(i)&0xff));
					System.out.println("v " + v);
					System.out.println();
					System.out.println("d1 " + (byte)(d1.data.get(i)&0xff));
					System.out.println("d2 " + (byte)(d2.data.get(i)&0xff));
					System.out.println("v " + (byte)v);*/
				res.data.add((byte) v);
				overflow=v>>>8;
			}

			return res;
		}
		return null;
	}
	/**
	 * Multiplie 2 Data
	 * @param d1 Data
	 * @param d2 Data
	 * @return d1 - d2
	 */
	public Data mul2(Data d1,Data d2) {
		if(d1.numberOfBits() == d2.numberOfBits()) {
			int N = d1.numberOfBytes();
			Data res = new Data(d1.numberOfBits(),false);
			res.data.clear();

			for(int i = N-1,overflow=0;i>= 0;i--) {
				int v = (d1.data.get(i)&0xff) + (d2.data.get(i)&0xff)+overflow;
				res.data.add((byte) v);
				overflow=v>>>8;
			}
			return res;
		}
		return null;
	}

	
	/**
	 * Multiplier 2 Data
	 * @param d1 Data 
	 * @param d2 Data
	 * @return d1 * d2
	 */
	public Data mul(Data d1,Data d2) {
		byte[] transfer1 = DataToArray(d1);
		byte[] transfer2 = DataToArray(d2);

		byte [] cal = mul(transfer1,transfer2,d1.numberOfBits());

		return arrayToData(cal,d1.numberOfBits());
	}

	/**
	 * Additionner 2 Data
	 * @param d1 Data 
	 * @param d2 Data
	 * @return d1 + d2
	 */
	public Data add(Data d1,Data d2) {
		byte[] transfer1 = DataToArray(d1);
		byte[] transfer2 = DataToArray(d2);

		byte [] cal = add(transfer1,transfer2,d1.numberOfBits());

		return arrayToData(cal,d1.numberOfBits());
	}
	/**
	 * Soustraire 2 Data
	 * @param d1 Data
	 * @param d2 Data
	 * @return d1 - d2
	 */
	public Data sub(Data d1,Data d2) {

		byte[] transfer1 = DataToArray(d1);
		byte[] transfer2 = DataToArray(d2);

		byte [] cal = sub(transfer1,transfer2,d1.numberOfBits());

		return arrayToData(cal,d1.numberOfBits());
	}
	/**
	 * Addition 2 tableau de byte
	 * @param data1 tableau de byte
	 * @param data2 tableau de byte
	 * @param n nombre de bit total
	 * @return data1 + data2
	 */
	private byte[] add(byte[] data1, byte[] data2, int n) {

		int N = 1 + (n - 1)/8;
		System.out.println("N = " + N);
		byte[] result=new byte[N];
		for(int i=N-1, overflow=0; i>=0; i--) {
			/*System.out.println("i =  " + i +" | ov = "+overflow);
			System.out.println("d1 : " + (data1[i]&0xff));
			System.out.println("d2 : " + (data2[i]&0xff));
			System.out.println("res = " + ((data1[i]&0xff)+(data2[i]&0xff)));*/
			int v = (data1[i]&0xff)+(data2[i]&0xff)+overflow;
			/*System.out.println("v = " + v);
			System.out.println("v byte = " + (byte)v);
			System.out.println();
			System.out.println();
			System.out.println("---------------");*/
			result[i]=(byte)v;
			overflow=v>>>8;
		}
		return result;
	}
	/**
	 * Soustraire 2 tableau de byte
	 * @param data1 tableau de byte
	 * @param data2 tableau de byte
	 * @param n nombre de bit total
	 * @return data1 - data2
	 */
	private byte[] sub(byte[] data1, byte[] data2, int n) {
		int N = 1 + (n - 1)/8;

		byte[] result=new byte[N];
		for(int i=N-1, overflow=0; i>=0; i--) {
			int v = (data1[i]&0xff)-(data2[i]&0xff)+overflow;
			result[i]=(byte)v;
			overflow=v>>>8;
		}
		return result;
	}


	/**
	 * Multiplier 2 tableau de byte
	 * @param data1 tableau de byte
	 * @param data2 tableau de byte
	 * @param n nombre de bit total
	 * @return data1 * data2
	 */
	private byte[] mul(byte[] data1, byte[] data2, int n) {
		int N = 1 + (n - 1)/8;

		byte[] result=new byte[N];
		for(int i=N-1, overflow=0; i>=0; i--) {
			int v = (data1[i]&0xff)*(data2[i]&0xff)+overflow;
			result[i]=(byte)v;
			overflow=v>>>8;
		}
		return result;
	}
	/**
	 * Converti list<Byte>data d'une Data en byte []
	 * @param d Data
	 * @return byte [] de d.data
	 */
	private byte[] DataToArray(Data d) {
		System.out.println("Number of Bytes = " + d.numberOfBytes());
		byte[] transfer = new byte[d.numberOfBytes()];
		for(int i = 0; i<d.data.size();i++) {
			transfer[i] = d.data.get(i);
		}
		return transfer;
	}
	/**
	 * Converti byte[] en Data
	 * @param b tableau de byte
	 * @param size nombre de bits total
	 * @return Data avec b converti en list<Byte>
	 */
	private Data arrayToData(byte[] b,int size){
		Data res = new Data(size,false);
		res.data.clear();
		for(int i = 0 ; i<b.length;i++) {
			res.data.add(b[i]);
		}
		return res;
	}

}
