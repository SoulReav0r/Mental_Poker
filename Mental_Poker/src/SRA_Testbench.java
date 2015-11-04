import java.math.BigInteger;

public class SRA_Testbench {
    public static void main(String[] args) {
        
        // Kevin and Falk p agree on shared p q:
        BigInteger  p = new BigInteger("4242424242424242", 16);
        BigInteger  q = new BigInteger("1337e397256dd8a5d4c6eee730eee3d5b20bf6c87d3b3cc9d7947", 16);
        
        CoinFlipping Kevin = new CoinFlipping(p, q);
        CoinFlipping Falk  = new CoinFlipping(p, q);
        
        // Kevin creates challenge:
        Falk.checkResult(Kevin.solveChallenge(Falk.getChallenge(Kevin.createChallenge())));        
    }

}
