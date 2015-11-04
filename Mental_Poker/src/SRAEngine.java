
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class SRAEngine 
    implements AsymmetricBlockCipher 
{
    
    private SRACore core;
   
    // init
    
    public void init(boolean forEncryption, CipherParameters param) {
        if (core == null) {
            core = new SRACore();
        }
        core.init(forEncryption, param);
    }

    public int getInputBlockSize() {
        return core.getInputBlockSize();
    }

    public int getOutputBlockSize() {
        return core.getOutputBlockSize();
    }

    public byte[] processBlock(
            byte[]  in, // Input Array 
            int     inOff, // Offset
            int     inLen) // length of array
    {
        if (core == null) {
            throw new IllegalStateException("no SRA engine dude!");
        }
        return core.convertOutput(core.processBlock(core.convertInput(in, inOff, inLen)));
    }


}
