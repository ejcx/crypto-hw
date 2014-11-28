import java.math.BigInteger;

/**
 * 
 * Class that can be used for DiffieHellman 
 * key agreement.
 * 
 * @author Evan J Johnson
 *
 */
public class DiffieHellman {

  	private static BigInteger 		p;	//public key
	private static BigInteger		g;	//public base
	
	/**
	 * Initalize public key and base
	 * @param p
	 * @param g
	 */
	public DiffieHellman(BigInteger p, BigInteger g)
	{
		this.p = p;
		this.g = g;	
	}
	
	/**
	 * Creates a public exponent 
	 * Using your private key
	 * @param priv
	 * @return
	 */
	public BigInteger createPub(BigInteger priv)
	{
		return this.g.modPow(priv, this.p);
	}
	
	/**
	 * Returns the shared key
	 * @param pub
	 * @param priv
	 * @return
	 */
	public BigInteger getShared(BigInteger pub, BigInteger priv)
	{
		return pub.modPow(priv, this.p);
	}
}
