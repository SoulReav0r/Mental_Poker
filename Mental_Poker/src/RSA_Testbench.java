
import java.math.BigInteger;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.encoders.Hex;



public class RSA_Testbench {

    public static void main(String[] args) {
        
        
        
        BigInteger  p = new BigInteger("f75e80839b9b9379f1cf1128f321639757dba514642c206bbbd99f9a4846208b3e93fbbe5e0527cc59b1d4b929d9555853004c7c8b30ee6a213c3d1bb7415d03", 16);
        BigInteger  q = new BigInteger("b892d9ebdbfc37e397256dd8a5d3123534d1f03726284743ddc6be3a709edb696fc40c7d902ed804c6eee730eee3d5b20bf6bd8d87a296813c87d3b3cc9d7947", 16);
        // Klartext
        String plainText = "Hier koennte ihre Werbung stehen";
        // n = p * q
        BigInteger n = p.multiply(q);
        // e
        BigInteger  e = new BigInteger("11", 16);
        
        // d
        BigInteger pMinusOne = p.subtract(new BigInteger("1"));
        BigInteger qMinusOne = q.subtract(new BigInteger("1"));
        BigInteger d = e.modInverse(pMinusOne.multiply(qMinusOne));
        
        // d mod (p-1)
        BigInteger  pExp = d.mod(pMinusOne);
        // d mod (q-1)
        BigInteger  qExp = d.mod(qMinusOne);
        // chinesischer Restansatz zur Bestimmung der Teilerfremde
        BigInteger  crtCoef = q.modInverse(p);

        byte[]                data = plainText.getBytes();
        byte[]                encrData = null;
        byte[]                decrData = null;
        
        // Encrypt
        
        RSAKeyParameters    pubParameters = new RSAKeyParameters(false, n, e);
        AsymmetricBlockCipher   engEn = new RSAEngine();        
        engEn.init(true, pubParameters);        
        
        try {
            encrData = engEn.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException error) {
            error.printStackTrace();
        }
        
        // Decrypt
        
        RSAKeyParameters    privParameters = new RSAPrivateCrtKeyParameters(n, e, d, p, q, pExp, qExp, crtCoef);
        AsymmetricBlockCipher   engDe = new RSAEngine();        
        engDe.init(false,  privParameters);
        
        try {
            decrData = engDe.processBlock(encrData, 0, encrData.length);
        } catch (InvalidCipherTextException error) {
            error.printStackTrace();
        }
        
        // Testing
        
        if (Arrays.equals(data, decrData)) {
            System.out.println("Läuft!\n"+"plain: "+new String(decrData)+"\n");
        } else {
        	System.err.println("ZONG"+"encrypt: "+new String(encrData)+"\n"+"plain: "+new String(decrData));
        }
        
    }

}
