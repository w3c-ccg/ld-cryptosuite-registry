package proof;

import canonical.Canonical;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bitcoinj.core.Base58;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Proof {

    public static final String ED_VERIFICATION_TYPE = "WorkdayEd25519VerificationKey2020";
    public static final String ED_TYPE = "WorkdayEd25519Signature2020";
    public static final EdDSAParameterSpec ED_SPEC = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);

    private String created;
    private String creator;
    private String nonce;
    private String signatureValue;
    private String type;

    public Proof(final String created,
                 final String creator,
                 final String nonce,
                 final String signatureValue,
                 final String type) {
        this.created = created;
        this.creator = creator;
        this.nonce = nonce;
        this.signatureValue = signatureValue;
        this.type = type;
    }

    public static Proof createEd25519Proof(final byte[] unsignedDoc,
                                           final String keyRef,
                                           final EdDSAPrivateKeySpec privKey,
                                           final String nonce) throws InvalidKeyException, SignatureException, IOException, NoSuchAlgorithmException {

        final Signature sgr = new EdDSAEngine(MessageDigest.getInstance(ED_SPEC.getHashAlgorithm()));
        final PrivateKey sKey = new EdDSAPrivateKey(privKey);
        sgr.initSign(sKey);

        // append nonce to doc to sign
        final byte[] nonceBytes = ("." + nonce).getBytes();

        // combine signature with nonce
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(unsignedDoc);
        baos.write(nonceBytes);
        final byte[] toSign = baos.toByteArray();

        // do the signing
        sgr.update(toSign);
        final byte[] signature = sgr.sign();

        // base58 encode signature
        final String base58Signature = Base58.encode(signature);

        return new Proof(Proof.getRFC3339Time(), keyRef, nonce, base58Signature, ED_TYPE);
    }

    public static boolean verifyEd25519Proof(final EdDSAPublicKeySpec pubKey,
                                             final Proof proofUnderTest,
                                             final byte[] bytesToProve) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!proofUnderTest.type.equals(ED_TYPE)) {
            // we only know how to handle our own type
            return false;
        }

        final Signature sgr = new EdDSAEngine(MessageDigest.getInstance(ED_SPEC.getHashAlgorithm()));

        // Add nonce back in
        final byte[] nonceBytes = ("." + proofUnderTest.nonce).getBytes(StandardCharsets.UTF_8);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bytesToProve);
        baos.write(nonceBytes);
        final byte[] toVerify = baos.toByteArray();

        // Decode signature
        final byte[] signature = Base58.decode(proofUnderTest.signatureValue);

        final PublicKey vKey = new EdDSAPublicKey(pubKey);
        sgr.initVerify(vKey);
        sgr.update(toVerify);
        return sgr.verify(signature);
    }

    private static String getRFC3339Time() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
    }

    public String getCreated() {
        return created;
    }

    public String getCreator() {
        return creator;
    }

    public String getNonce() {
        return nonce;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return Canonical.toJson(this);
    }
}
