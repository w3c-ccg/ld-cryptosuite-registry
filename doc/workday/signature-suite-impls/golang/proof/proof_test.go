package proof

import (
	"bytes"
	"testing"

	"github.com/mr-tron/base58"
	"github.com/stretchr/testify/assert"
	"golang.org/x/crypto/ed25519"
)

var (
	keySeed       = []byte("12345678901234567890123456789012")
	issuerPrivKey = ed25519.NewKeyFromSeed(keySeed) // this matches the public key in didDocJson
	issuerPubKey  = issuerPrivKey.Public().(ed25519.PublicKey)

	nonce             = "0948bb75-60c2-4a92-ad50-01ccee169ae0"
	creatorKey        = "did:work:6sYe1y3zXhmyrBkgHgAgaq#key-1"
	expectedSignature = "2NQNA7SXVrTJRPYGAtpdxXAaKZDdzzQ3XYEghVVhRKH8AGrNS9kHa4USgbUYxbgG3wHpF8Qzou34P5jqYC9x4UYE"

	testJSON      = `{"some":"one","test":"two","structure":"three"}`
	differentJSON = `{"some":"one","test":"two","structure":"banana"}`
)

func TestProofGeneration(t *testing.T) {
	proofUnderTest, err := CreateEd25519Proof([]byte(testJSON), creatorKey, &issuerPrivKey, nonce)
	assert.Nil(t, err)
	assert.Equal(t, proofUnderTest.Nonce, nonce)
	assert.Equal(t, proofUnderTest.Creator, creatorKey)
	assert.Equal(t, expectedSignature, proofUnderTest.SignatureValue)
	validateProof(t, "did:work:6sYe1y3zXhmyrBkgHgAgaq#key-1", issuerPubKey, *proofUnderTest, []byte(testJSON))

	assert.NoError(t, VerifyEd25519Proof(issuerPubKey, *proofUnderTest, []byte(testJSON)))
}

func TestValidationOfProof(t *testing.T) {
	proofUnderTest, _ := CreateEd25519Proof([]byte(testJSON), creatorKey, &issuerPrivKey, nonce)
	err := VerifyEd25519Proof(issuerPubKey, *proofUnderTest, []byte(testJSON))
	assert.Nil(t, err)

	err = VerifyEd25519Proof(issuerPubKey, *proofUnderTest, []byte(differentJSON))
	assert.Error(t, err)
	assert.Equal(t, err.Error(), "failure while verifying signature (b58) 2NQNA7SXVrTJRPYGAtpdxXAaKZDdzzQ3XYEghVVhRKH8AGrNS9kHa4USgbUYxbgG3wHpF8Qzou34P5jqYC9x4UYE for pub key (b58) 4CcKDtU1JNGi8U4D8Rv9CHzfmF7xzaxEAPFA54eQjRHF")
}

func validateProof(t *testing.T, signingKeysRef string, pubKey ed25519.PublicKey, proofUnderTest Proof, docToProve []byte) {
	assert.Equal(t, proofUnderTest.Type, EdType)
	assert.Equal(t, proofUnderTest.Creator, signingKeysRef)
	nonce := proofUnderTest.Nonce

	var buf bytes.Buffer
	buf.Write(docToProve)
	buf.Write([]byte("." + nonce))
	toSign := buf.Bytes()
	sigBytes, _ := base58.Decode(proofUnderTest.SignatureValue)
	valid := ed25519.Verify(pubKey, toSign, sigBytes)
	if !valid {
		t.Error("failure while verifying signature")
	}
}
