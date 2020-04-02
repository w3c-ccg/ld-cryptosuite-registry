package proof;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.junit.Assert;
import org.junit.Test;

import static proof.Proof.ED_SPEC;
import static proof.Proof.ED_TYPE;

public class ProofTest {
    final String keySeed = "12345678901234567890123456789012";
    final EdDSAPrivateKeySpec privKeySpec = new EdDSAPrivateKeySpec(keySeed.getBytes(), ED_SPEC);
    final EdDSAPrivateKey privKey = new EdDSAPrivateKey(privKeySpec);
    final EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(privKey.getAbyte(), ED_SPEC);

    final String nonce = "0948bb75-60c2-4a92-ad50-01ccee169ae0";
    final String creatorKey = "did:work:6sYe1y3zXhmyrBkgHgAgaq#key-1";
    final String expectedSignature = "2NQNA7SXVrTJRPYGAtpdxXAaKZDdzzQ3XYEghVVhRKH8AGrNS9kHa4USgbUYxbgG3wHpF8Qzou34P5jqYC9x4UYE";

    final String testJSON = "{\"some\":\"one\",\"test\":\"two\",\"structure\":\"three\"}";

    @Test
    public void proofTest() {
        Proof proof = null;
        try {
            proof = Proof.createEd25519Proof(testJSON.getBytes(), creatorKey, privKeySpec, nonce);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(nonce, proof.getNonce());
        Assert.assertEquals(creatorKey, proof.getCreator());
        Assert.assertEquals(expectedSignature, proof.getSignatureValue());
        Assert.assertEquals(ED_TYPE, proof.getType());

        try {
            Assert.assertTrue(Proof.verifyEd25519Proof(pubKeySpec, proof, testJSON.getBytes()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
