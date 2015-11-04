import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class CoinFlipping {
    
    private BigInteger e; 
    private SRAParameters pubParams;
    private SRAParameters privParams;
    private AsymmetricBlockCipher encrEngine = new SRAEngine();
    private AsymmetricBlockCipher decrEngine = new SRAEngine();
    
    private BigInteger myChallenge;
    private BigInteger myChallengeEncr;
    
    
    private BigInteger otherChallenge;
    private Coin myGuess;

    public CoinFlipping(BigInteger p, BigInteger q) {
        generateE();
        pubParams = new SRAParameters(false, p, q, e);
        privParams = new SRAParameters(true, p, q, e);
        encrEngine.init(true, pubParams);
        decrEngine.init(false, privParams);        
    }
    
    private void generateE()
    {
        e = BigInteger.probablePrime(1024, new SecureRandom());
    }
    
    // My challenge:
    
    public BigInteger createChallenge()
    {
        myChallenge = new BigInteger(256, new SecureRandom());
        try {
            myChallengeEncr = new BigInteger(encrEngine.processBlock(myChallenge.toByteArray(), 0, myChallenge.toByteArray().length));
        } catch (InvalidCipherTextException e) {
        }                
        return myChallengeEncr;
    }
    
    public AsymmetricBlockCipher solveChallenge(Coin guess)
    {
        // % 2 == 0 --> HEAD
        Coin correctAnswer = (myChallenge.mod(new BigInteger("2")).intValue() == 0 ? Coin.HEAD : Coin.TAILS); 
        System.out.println("opposites result: " + (guess == correctAnswer) );
        return decrEngine;
    }
    
    
    public Coin getChallenge(BigInteger challenge)
    {
        Random rand = new SecureRandom();
        otherChallenge = challenge;
        myGuess = rand.nextInt(2) == 0 ? Coin.HEAD : Coin.TAILS;
        return myGuess;
    }
    
    public void checkResult(AsymmetricBlockCipher decrypter)
    {
        BigInteger decryptedChallenge = null;
        try {
            decryptedChallenge = new BigInteger(decrypter.processBlock(otherChallenge.toByteArray(), 0, otherChallenge.toByteArray().length));
        } catch (InvalidCipherTextException e) {
        }
        Coin result = decryptedChallenge.mod(new BigInteger("2")).intValue() == 0 ? Coin.HEAD : Coin.TAILS;
        
        System.out.println("My result: " + (result == myGuess));
        
    }
    
}
