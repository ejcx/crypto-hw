import java.math.BigInteger;
import java.util.Random;


public class RSA {
	
	private BigInteger 		n, d, e;
	
	//Generic common value for e.
	private BigInteger e = new BigInteger("65537");
	/**
	 * Use this constructor if you wish to specify the amount of bits you want
	 * for your P and Q
	 * @param n
	 */
	public RSA(int n)
	{
		keygen(BigInteger.probablePrime(n, new Random()), BigInteger.probablePrime(n, new Random()));
	}
	
	/**
	 * If you have your own P and Q
	 * @param p
	 * @param q
	 */
	public RSA(BigInteger p, BigInteger q)
	{
		keygen(p,q);
	}
	
	/**
	 * If you have your own small P and Q and don't feel like messing with BigIntegers
	 * @param p
	 * @param q
	 */
	public RSA(int p, int q)
	{
		keygen(new BigInteger(Integer.toString(p)),new BigInteger(Integer.toString(q)));
	}
	
	
	/**
	 * Helper method in case you want to work through RSA by yourself
	 * @param a
	 * @param e
	 * @return
	 */
	public static int gcd(int a, int e)
	{
		int 			t;
		
		//xor swap
		if (a < e)
		{
			a = a ^ e;
			e = a ^ e;
			a = a ^ e;
		}
		
		while (e != 0)
	    {
			t = e;
			e = a % t;
			a = t;
	    }
	    return a;
	}
	
	/**
	 * Helper method in case you want to work through RSA by yourself
	 * @param a
	 * @param e
	 * @return
	 */
	public static int multiplicativeinverse(int a, int e)
	{
		int			i,m;
		int[]			t;		
		
		//xor swap
		if (a<e)
		{
			a = a ^ e;
			e = a ^ e;
			a = a ^ e;
		}
		
		t = xgcd(a,e);
	
		if (t[1] < 0)				i = (a - Math.abs(t[1]));
		else						i = (t[1] % a);
	
		return i;
	}
	
	/**
	 * Helper method in case you want to work through RSA by yourself
	 * @param a
	 * @param e
	 * @return
	 */
	public static int[] xgcd(int a, int e)
	{
		//xor swap
		if (a<e)					
		{
			a = a ^ e;
			e = a ^ e;
			a = a ^ e;
			
		}
		int[]			qr;
		int[]			st;
		if (e == 0)
			return new int[]{1, 0};
		else
		{
			qr = divide (a, e);
			st = xgcd(e, qr[1]);
			return new int[]{st[1], (st[0] - qr[0] * st[1])};
		}
	}
	
	/**
	 * Helper method in case you want to work through RSA by yourself
	 * @param a
	 * @param e
	 * @return
	 */
	public static int[] divide(int a, int e)
	{	
		int 			q;
		
		q = 0;
		while (a >= e)
		{
			a = a-e;
			q++;
		}

		return new int[]{q,a};
	}
	
	private void keygen (BigInteger p, BigInteger q)
	{	
		BigInteger		phi;
		
		this.n 	= p.multiply(q);
		phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		
		
		this.d 	= e.modInverse(phi);
	}
	
	
	public BigInteger encrypt(BigInteger message)
	{
		BigInteger 		c;
		
		c 	= message.modPow(this.e, this.n);
		
		return c;
	}
	
	public BigInteger decrypt(BigInteger ciphertext)
	{
		BigInteger 		p;
		
		p 	= ciphertext.modPow(this.d, this.n);
		
		return p;
	}
}
