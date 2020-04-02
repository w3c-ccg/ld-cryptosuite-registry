package did;

import canonical.Canonical;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bitcoinj.core.Base58;
import proof.Proof;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public final class Did extends UnsignedDidDoc {
    public static final String INITIAL_KEY = "key-1";
    public static final String DID_METHOD_PREFIX = "did:work:";

    private Proof proof;

    private Did(final UnsignedDidDoc unsignedDidDoc,
                final Proof proof) {
        super(unsignedDidDoc.getId(), unsignedDidDoc.getPublicKey(), unsignedDidDoc.getAuthentication(), unsignedDidDoc.getService());
        this.proof = proof;
    }

    public static String generateDID(final EdDSAPublicKey pubKey) {
        final byte[] pubKeyBytes = pubKey.getAbyte();
        final byte[] firstSixteen = Arrays.copyOfRange(pubKeyBytes, 0, 16);
        return DID_METHOD_PREFIX + Base58.encode(firstSixteen);
    }

    public static Did signDIDDoc(final UnsignedDidDoc unsignedDidDoc,
                                 final EdDSAPrivateKeySpec privKey,
                                 final String keyRef) throws Exception {
        final String didJson = Canonical.toJson(unsignedDidDoc);
        final String canonicalDidJson = Canonical.canonicalize(didJson);
        final String nonce = UUID.randomUUID().toString();
        final Proof proof = Proof.createEd25519Proof(canonicalDidJson.getBytes(StandardCharsets.UTF_8), keyRef, privKey, nonce);
        return new Did(unsignedDidDoc, proof);
    }

    public static boolean validateDidDocProof(final Did didDoc,
                                              final EdDSAPublicKeySpec pubKey) throws Exception {
        final String didJson = Canonical.toJson(didDoc.getUnsignedDidDoc());
        final String canonicalDidJson = Canonical.canonicalize(didJson);
        return Proof.verifyEd25519Proof(pubKey, didDoc.getProof(), canonicalDidJson.getBytes());
    }

    public UnsignedDidDoc getUnsignedDidDoc() {
        return new UnsignedDidDoc(this.getId(), this.getPublicKey(), this.getAuthentication(), this.getService());
    }

    public Proof getProof() {
        return proof;
    }

    @Override
    public String toString() {
        return Canonical.toJson(this);
    }
}

class UnsignedDidDoc {
    private String id;
    private KeyDef[] publicKey;
    private String[] authentication;
    private ServiceDef[] service;

    public UnsignedDidDoc(final String id,
                          final KeyDef[] publicKey,
                          final String[] authentication,
                          final ServiceDef[] service) {
        this.id = id;
        this.publicKey = publicKey;
        this.authentication = authentication;
        this.service = service;
    }

    public String getId() {
        return id;
    }

    public KeyDef[] getPublicKey() {
        return publicKey;
    }

    public String[] getAuthentication() {
        return authentication;
    }

    public ServiceDef[] getService() {
        return service;
    }

    @Override
    public String toString() {
        return Canonical.toJson(this);
    }
}

class KeyDef {
    private String id;
    private String type;
    private String controller;
    private String publicKeyBase58;

    public KeyDef(final String id,
                  final String type,
                  final String controller,
                  final String publicKeyBase58) {
        this.id = id;
        this.type = type;
        this.controller = controller;
        this.publicKeyBase58 = publicKeyBase58;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getController() {
        return controller;
    }

    public String getPublicKeyBase58() {
        return publicKeyBase58;
    }

    @Override
    public String toString() {
        return Canonical.toJson(this);
    }
}

class ServiceDef {
    private String id;
    private String type;
    private String serviceEndpoint;

    public ServiceDef(final String id,
                      final String type,
                      final String serviceEndpoint) {
        this.id = id;
        this.type = type;
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    @Override
    public String toString() {
        return Canonical.toJson(this);
    }
}
