import java.math.BigInteger;

/**
 * 
 * Class for encrypting with a elliptic curve 
 * over a prime in the form 
 * [y^2 = x^3 + ax + b mod p]
 * 
 * @author Evan J Johnson
 *
 */
public class EllipticCurve {

  private  BigInteger[] 	g;	
	private  BigInteger   	a;	
	private  BigInteger	p;
	private  BigInteger   	b;
	
	public EllipticCurve (BigInteger[] g, BigInteger a, BigInteger p, BigInteger b)
	{
		this.g = g;
		this.a = a;
		this.p = p;
		this.b = b;
	}
	
	
	/**
	 * encryption requires your private key and the reciever's public key
	 * @param m
	 * @param k
	 * @param pub
	 * @return
	 */
	public BigInteger[][] encrypt (BigInteger[] m, BigInteger k, BigInteger[] pub)
	{
		BigInteger[] 			C1;
		BigInteger[]			C2;
		
		C1 = kmultiplication(k, this.g);
		C2 = pointaddition(m, kmultiplication(k, pub));
		
		return new BigInteger[][]{C1, C2};
	}
	
	/**
	 * Decryption. Requires a private key
	 * @param k
	 * @param ct
	 * @return
	 */
	public BigInteger[] decrypt(BigInteger k, BigInteger[][] ct)
	{
		BigInteger[]			t;
		BigInteger			s;
		
		t = kmultiplication(k, ct[0]);
		s = t[1].multiply(new BigInteger("-1"));
		s = s.mod(this.p);
		
		t[1] = s;
		return pointaddition(ct[1], t);
	}
	
	
	/**
	 * multiply a point a certain number of times.
	 * This is done efficiently using double and add algorithm
	 * @param k
	 * @param g
	 * @return
	 */
	public BigInteger[] kmultiplication(BigInteger k, BigInteger[] g)
	{
		BigInteger[] product = g.clone();
		String kstring = k.toString(2);
		
		/**
		 * hackish double and add to avoid messing with zero point
		 * 49 is ascii character code for '1'. We are looking for where the
		 * string contains 1
		 */
		for (int i = 0 ; i < kstring.length() ; i++)
		{
			if (i != 0)
			{
				product = pointaddition(product, product);
				if (kstring.charAt(i) == 49)
				{
					product = pointaddition(g, product);
				}
			} 
			else
			{
				if (kstring.charAt(i) == 49){
					product = g.clone();
				}
			}
		}
		
		return product;
	}
	
	
	
	/**
	 * Matches a message to a ordered pair that is within the field E
	 * @param m
	 * @return
	 */
	public BigInteger[] map (BigInteger m)
	{
		String				l,r;
		BigInteger 			z;
		BigInteger			u;
		BigInteger			mu;
		BigInteger			y;
		
		//set to -1 so that the do-while is more readable.
		u = new BigInteger("-1");
		l = m.toString(2);
		
		
		do
		{
			//Create pad
			u = u.add(BigInteger.ONE);
			r = "";
			
			//hardcodes the length of the pad to 20bits. If alice is sending a message padded with 20
			//bits, Bob must also use 20 bits. This is very important. Without this pad it may be
			//possible that a message will not map to any particular point within your field
			for (int i=u.toString(2).length(); i < 20; i++)	
			{
				r = r+"0";
			}

			r = r+u.toString(2);
			//pad created
			
			//mu pad
			mu = new BigInteger(l+r, 2);
			
			z = mu.pow(3).add(this.a.multiply(mu)).add(this.b).mod(this.p);	
			y = z.modPow((this.p.add(BigInteger.ONE).divide(new BigInteger("4"))), this.p);
			
		}	while (square(z, this.p).compareTo(BigInteger.ONE) != 0);
		
		return new BigInteger[]{mu, y};
	}
	
	
	/**
	 * Add two points together
	 * @param P
	 * @param Q
	 * @return
	 */
	public BigInteger[] pointaddition(BigInteger[] P, BigInteger[] Q)
	{
		BigInteger					lambda, 
								Xr,
								Yr;
		BigInteger					numerator,
								denom;
		BigInteger[]					R;
		
		//determine if the points are equivalent. Adding two points that are the same is different than two that are different
		if (P[0].compareTo(Q[0]) == 0 && P[1].compareTo(Q[1]) == 0)
		{
			numerator	= P[0].multiply(P[0]).multiply(new BigInteger("3")).add(this.a);
			denom		= new BigInteger("2").multiply(P[1]).modInverse(this.p);
		
		}
		else 
		{
			numerator 	= Q[1].subtract(P[1]);
			denom		= Q[0].subtract(P[0]).modInverse(this.p);
		}
		
		//Determine lambda
		lambda = numerator.multiply(denom).mod(p);
		
		Xr	= lambda.multiply(lambda).subtract(P[0]).subtract(Q[0]).mod(p);
		Yr	= lambda.multiply(P[0].subtract(Xr)).subtract(P[1]).mod(p);
		
		R	= new BigInteger[]{Xr, Yr};
		
		return R;
	}
	



	/**
	 * Jacobi Algorithm. Used to determine if a is a square mod p.
	 * 
	 * This algorithm looks really big and scary. It is only big and scary
	 * because BigIntegers are required to be used. Jacobi Algorithm is actually
	 * quite easy to understand.
	 * @param a
	 * @param n
	 * @return
	 */
	public static BigInteger square(BigInteger a, BigInteger n)
	{
		BigInteger				e;
		BigInteger				a1;
		BigInteger				s;
		//a = a.mod(n);
		
		if (n.compareTo(new BigInteger("2")) == 0 || n.compareTo(BigInteger.ONE) == 0)
		{
			if (a.compareTo(BigInteger.ZERO) != 0)		
				return BigInteger.ONE;
		}
		
		
		if (a.compareTo(BigInteger.ONE) == 0)
		{
			return BigInteger.ONE;
		} 
		else if (a.compareTo(BigInteger.ZERO)== 0) 
		{
			return BigInteger.ZERO;
		} 
		else 
		{
			
			e = BigInteger.ZERO;
			a1 = a;
			while (a1.mod(new BigInteger("2")).compareTo(BigInteger.ONE) != 0)
			{
				a1 = a1.divide(new BigInteger("2"));
				e = e.add(new BigInteger("1"));
			}

			s = BigInteger.ZERO;
			if (e.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0)
			{
				s = BigInteger.ONE;
			} 
			else
			{
				if (n.mod(new BigInteger("8")).compareTo(BigInteger.ONE) == 0 || n.mod(new BigInteger("8")).compareTo(new BigInteger("7")) == 0)
				{
					s = BigInteger.ONE;
				} 
				else if (n.mod(new BigInteger("8")).compareTo(new BigInteger("3")) == 0 || n.mod(new BigInteger("8")).compareTo(new BigInteger("5")) == 0 )
				{
					s = new BigInteger("-1");
				}
			}
			
			if (n.mod(new BigInteger("4")).compareTo(new BigInteger("3")) == 0 && a1.mod(new BigInteger("4")).compareTo(new BigInteger("3")) == 0)
			{
				s = s.multiply(new BigInteger("-1"));
			}
		}
		
		n = n.mod(a1);
		
		if (a1.equals(BigInteger.ONE))		return s;
		else					return (s.multiply(square(n,a1)));
	}
}
