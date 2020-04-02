package did;

import com.google.gson.Gson;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bitcoinj.core.Base58;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import static proof.Proof.ED_SPEC;
import static proof.Proof.ED_VERIFICATION_TYPE;

public class DidTest {

    String keySeed = "12345678901234567890123456789012";
    EdDSAPrivateKeySpec privKeySpec = new EdDSAPrivateKeySpec(keySeed.getBytes(), ED_SPEC);
    EdDSAPrivateKey privKey = new EdDSAPrivateKey(privKeySpec);
    EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(privKey.getAbyte(), ED_SPEC);

    @Test
    public void didTest() {
        Did testDid = null;
        try {
            testDid = generateTestDidDoc(pubKeySpec, privKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(testDid);
        Assert.assertTrue(verifyDIDDoc(testDid, pubKeySpec));
    }

    @Test
    public void didTestKnownSignature() {
        final Gson gson = new Gson();
        try {
            final BufferedReader br = new BufferedReader(new FileReader("../../signed-diddoc.json"));
            final Did did = gson.fromJson(br, Did.class);
            Assert.assertNotNull(did);

            // Get public key
            final String pubKeyBase58 = did.getPublicKey()[0].getPublicKeyBase58();
            final byte[] pubKey = Base58.decode(pubKeyBase58);
            final EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(pubKey, ED_SPEC);

            // Validate signature
            Assert.assertTrue(Did.validateDidDocProof(did, pubKeySpec));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }

    private static Did generateTestDidDoc(EdDSAPublicKeySpec pubKey, EdDSAPrivateKeySpec privKey) throws Exception {
        final String did = Did.generateDID(new EdDSAPublicKey(pubKey));
        final String keyRef = did + "#" + Did.INITIAL_KEY;
        final String base58PubKey = Base58.encode(pubKey.getA().toByteArray());
        final KeyDef testKeyDef = new KeyDef(keyRef, ED_VERIFICATION_TYPE, did, base58PubKey);
        final ServiceDef testServiceDef = new ServiceDef("schemaID", "schema", "schemaID");
        final UnsignedDidDoc unsignedDidDoc = new UnsignedDidDoc(did, new KeyDef[]{testKeyDef}, null, new ServiceDef[]{testServiceDef});
        return Did.signDIDDoc(unsignedDidDoc, privKey, keyRef);
    }

    private static boolean verifyDIDDoc(Did doc, EdDSAPublicKeySpec pubKey) {
        // Validate key
        Assert.assertEquals(1, doc.getUnsignedDidDoc().getPublicKey().length);

        // Validate did from key
        final byte[] publicKey = Base58.decode(doc.getUnsignedDidDoc().getPublicKey()[0].getPublicKeyBase58());
        final byte[] firstSixteen = Arrays.copyOfRange(publicKey, 0, 16);
        Assert.assertEquals(Did.DID_METHOD_PREFIX + Base58.encode(firstSixteen), doc.getUnsignedDidDoc().getId());

        // Validate proof on doc
        try {
            return Did.validateDidDocProof(doc, pubKey);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            return false;
        }
    }
}
