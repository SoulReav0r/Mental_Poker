import java.math.BigInteger;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;


public class SRAParameters 
    extends AsymmetricKeyParameter
{

    private BigInteger p;
    private BigInteger q;
    private BigInteger exponent;
    
    public SRAParameters(
            boolean isPrivate,
            BigInteger p,
            BigInteger q,
            BigInteger exponent) {
        super (isPrivate);
        this.p = p;
        this.q = q;
        this.exponent = exponent;
    }
    
    public BigInteger getModulus()
    {
        return p.multiply(q);
    }
    
    public BigInteger getExponent()
    {
        return exponent;
    }
    
    public BigInteger getP() 
    {
        return p;
    }
    
    public BigInteger getQ() 
    {
        return q;
    }

}
