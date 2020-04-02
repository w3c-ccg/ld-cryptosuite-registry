package proof

import (
	"bytes"
	"crypto"
	cryptorand "crypto/rand"
	"fmt"
	"time"

	"github.com/mr-tron/base58"
	"github.com/pkg/errors"
	"golang.org/x/crypto/ed25519"
)

const (
	EdSignatureType = "WorkdayEd25519VerificationKey2020"
	EdType 			= "WorkdayEd25519Signature2020"
)

// Proof a signed proof of a given ledger document
type Proof struct {
	Created        string `json:"created"`
	Creator        string `json:"creator"`
	Nonce          string `json:"nonce"`
	SignatureValue string `json:"signatureValue"`
	Type           string `json:"type"`
}

// CreateEd25519Proof create a proof
func CreateEd25519Proof(unsignedDoc []byte, keyRef string, privKey *ed25519.PrivateKey, nonce string) (*Proof, error) {
	var buf bytes.Buffer
	buf.Write(unsignedDoc)
	buf.Write([]byte("." + nonce))
	toSign := buf.Bytes()
	signature, err := privKey.Sign(cryptorand.Reader, toSign, crypto.Hash(0))
	if err != nil {
		return nil, errors.New("failed to Sign JSON DOC")
	}

	b58sig := base58.Encode(signature)
	genTime := time.Now().UTC()
	return &Proof{
		Type:           EdType,
		Created:        genTime.Format(time.RFC3339),
		Creator:        keyRef,
		SignatureValue: b58sig,
		Nonce:          nonce,
	}, nil
}

func VerifyEd25519Proof(pubKey ed25519.PublicKey, proofUnderTest Proof, bytesToProve []byte) error {
	if proofUnderTest.Type != EdType {
		return errors.Errorf("cannot verify proof with type %s as Ed25519 signature", proofUnderTest.Type)
	}

	nonce := proofUnderTest.Nonce
	var buf bytes.Buffer
	buf.Write(bytesToProve)
	buf.Write([]byte("." + nonce))
	toSign := buf.Bytes()

	sigBytes, err := base58.Decode(proofUnderTest.SignatureValue)
	if err != nil {
		return err
	}
	if valid := ed25519.Verify(pubKey, toSign, sigBytes); !valid {
		return fmt.Errorf("failure while verifying signature (b58) %s for pub key (b58) %s", proofUnderTest.SignatureValue, base58.Encode(pubKey))
	}
	return nil
}
