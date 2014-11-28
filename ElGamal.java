import java.math.BigInteger;
import java.util.Random;


public class ElGamal {
  /**
	 * Evan J Johnson
	 * 
	 * ElGamal encryption algorithm
	 */
	
	private static BigInteger			q;
	private static BigInteger			alpha;

	public ElGamal(BigInteger q, BigInteger alpha)
	{
		this.q 		= q;
		this.alpha 	= alpha;
	}
	
	public BigInteger[] genKeys(BigInteger priv)
	{
		return new BigInteger[]{this.q, this.alpha, this.alpha.modPow(priv, this.q)};
	}
	
	public BigInteger[] encrypt(BigInteger[] pub, BigInteger m)
	{
		BigInteger		C1, C2, k, K;
		
		do { //random k
			k = new BigInteger(Integer.toString(new Random().nextInt()));
		} while (k.compareTo(pub[0]) >= 0);
		
		K = pub[2].modPow(k, pub[0]);
		C1 = pub[1].modPow(k, pub[0]);
		C2 = m.multiply(K).mod(pub[0]);
		
		return new BigInteger[]{C1, C2};
	}
	
	public BigInteger decrypt(BigInteger[] c, BigInteger priv)
	{
		BigInteger 		K, t;
		
		K = c[0].modPow(priv, this.q);
		t =  K.modInverse(this.q);
		
		return t.multiply(c[1]).mod(this.q);
	}
}
