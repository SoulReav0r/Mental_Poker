import java.math.BigInteger;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;

class SRACore 
{
    
    private SRAParameters keyParams;
    private boolean encrypt;

    public SRACore() {
        
    }

    public void init(
            boolean forEncription, 
            CipherParameters param) {
        
        if (param instanceof ParametersWithRandom)
        {
            ParametersWithRandom    rParam = (ParametersWithRandom)param;

            keyParams = (SRAParameters)rParam.getParameters();
        }
        else
        {
            keyParams = (SRAParameters)param;
        }
        
        this.encrypt = forEncription;
        
    }

    /**
     * Return the maximum size for an input block to this engine.
     * For SRA this is always one byte less than the key size on
     * encryption, and the same length as the key size on decryption.
     *
     * @return maximum size for an input block.
     */
    public int getInputBlockSize() {
        int     bitSize = keyParams.getModulus().bitLength();

        if (encrypt)
        {
            return (bitSize + 7) / 8 - 1;
        }
        else
        {
            return (bitSize + 7) / 8;
        }
    }

    /**
     * Return the maximum size for an output block to this engine.
     * For SRA this is always one byte less than the key size on
     * decryption, and the same length as the key size on encryption.
     *
     * @return maximum size for an output block.
     */
    public int getOutputBlockSize() {
        int     bitSize = keyParams.getModulus().bitLength();

        if (encrypt)
        {
            return (bitSize + 7) / 8;
        }
        else
        {
            return (bitSize + 7) / 8 - 1;
        }
    }

    public BigInteger convertInput(byte[] in, int inOff, int inLen) {
        if (inLen > (getInputBlockSize() + 1))
        {
            throw new DataLengthException("input too large for SRA cipher.");
        }
        else if (inLen == (getInputBlockSize() + 1) && !encrypt)
        {
            throw new DataLengthException("input too large for SRA cipher.");
        }

        byte[]  block;

        if (inOff != 0 || inLen != in.length)
        {
            block = new byte[inLen];

            System.arraycopy(in, inOff, block, 0, inLen);
        }
        else
        {
            block = in;
        }

        BigInteger res = new BigInteger(1, block);
        if (res.compareTo(keyParams.getModulus()) >= 0)
        {
            throw new DataLengthException("input too large for SRA cipher.");
        }

        return res;
    }
    public byte[] convertOutput(BigInteger result) {
        byte[]      output = result.toByteArray();

        if (encrypt)
        {
            if (output[0] == 0 && output.length > getOutputBlockSize())        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }

            if (output.length < getOutputBlockSize())     // have ended up with less bytes than normal, lengthen
            {
                byte[]  tmp = new byte[getOutputBlockSize()];

                System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);

                return tmp;
            }
        }
        else
        {
            if (output[0] == 0)        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }
        }

        return output;
    }

    public BigInteger processBlock(BigInteger input) {
       
        
        if (encrypt) {
            return input.modPow(keyParams.getExponent(), keyParams.getModulus());
        } else {
            BigInteger pMinusOne = keyParams.getP().subtract(new BigInteger("1"));
            BigInteger qMinusOne = keyParams.getQ().subtract(new BigInteger("1"));
            BigInteger d = keyParams.getExponent().modInverse(pMinusOne.multiply(qMinusOne));
            return input.modPow(d, keyParams.getModulus());
        }
        
    }

}
