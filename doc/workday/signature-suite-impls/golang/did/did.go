package did

import (
	"github.com/google/uuid"
	"github.com/mr-tron/base58"
	"github.com/pkg/errors"
	"golang.org/x/crypto/ed25519"

	"signaturesuite/canonical"
	"signaturesuite/proof"
)

type UnsignedDIDDoc struct {
	ID             string       `json:"id"`
	PublicKey      []KeyDef     `json:"publicKey"`
	Authentication []string     `json:"authentication"`
	Service        []ServiceDef `json:"service"`
}

// DIDDoc a W3C compliant signed DID Document
type DIDDoc struct {
	*UnsignedDIDDoc
	*proof.Proof `json:"proof"`
}

// KeyDef represents a DID public key
type KeyDef struct {
	ID              string `json:"id"`
	Type            string `json:"type"`
	Controller      string `json:"controller,omitempty"`
	PublicKeyBase58 string `json:"publicKeyBase58"`
}

type ServiceDef struct {
	ID              string `json:"id"`
	Type            string `json:"type"`
	ServiceEndpoint string `json:"serviceEndpoint"`
}

const (
	// InitialKey the key reference assigned to the first key in a DidDoc
	InitialKey      = "key-1"
	IssuerDIDMethod = "did:work:"
)

// GenerateDID generate a DID
func GenerateDID(publicKey *ed25519.PublicKey) string {
	bytes := *publicKey
	return IssuerDIDMethod + base58.Encode(bytes[0:16])
}

func SignDIDDoc(unsignedDoc UnsignedDIDDoc, privKey *ed25519.PrivateKey, keyRef string) (*DIDDoc, error) {
	docBytes, err := canonical.Marshal(&unsignedDoc)
	if err != nil {
		return nil, errors.New("failed to Marshal Unsigned Doc to JSON")
	}
	nonce := uuid.New().String()
	docProof, err := proof.CreateEd25519Proof(docBytes, keyRef, privKey, nonce)
	return &DIDDoc{UnsignedDIDDoc: &unsignedDoc, Proof: docProof}, err
}

func ValidateDIDDocProof(didDoc DIDDoc, pubKey ed25519.PublicKey) error {
	bytes, err := canonical.Marshal(didDoc.UnsignedDIDDoc)
	if err != nil {
		return err
	}
	return proof.VerifyEd25519Proof(pubKey, *didDoc.Proof, bytes)
}
