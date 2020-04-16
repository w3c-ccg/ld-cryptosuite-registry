# Workday Signature Suite
- Authors: 
  - Gabe Cohen [gabe.cohen@workday.com](mailto:gabe.cohen@workday.com)
- Last updated: 2020-04-06

## Status
- Status: **DRAFT**
- Status Date: 2019-02-20

## Abstract
Operating within the ecosystem of DIDs, Verifiable Credentials, and Distributed Ledger technology, Workday has a vested interest in a consistent, reliable, and secure method to sign over and verify different types of information. Additionally, we have designed the Workday Credentials system to be interoperable with the wider community. In the interest of all listed above, we propose our addition to the [Linked Data Cryptographic Suite Registry](https://w3c-ccg.github.io/ld-cryptosuite-registry).

## Contents
  * [Abstract](#abstract)
  * [Contents](#contents)
  * [Specification](#specification)
    - [ID](#id)
    - [Type](#type)
      + [Verification Key](#verification-key)
    - [Canonicalization Algorithm](#canonicalization-algorithm)
    - [Digest Algorithm](#digest-algorithm)
    - [Signature Algorithm](#signature-algorithm)
  * [Prior Art](#prior-art)
  * [Implementation Notes](#implementation-notes)
  * [References](#references)

## Specification
According to the [Linked Data Signatures](https://w3c-dvcg.github.io/ld-signatures/) document, a _Linked Data Signature_

>  is designed to be easy to use by developers and therefore strives to minimize the amount of information one has to remember to generate a signature. 

The document outlines five properties that are required to comprise a Signature Suite: **id**, **type**, **canonicalizationAlgorithm**, **digestAlgorithm**, and **signatureAlgorithm**.

This specification is intended to work only on JSON data.

### ID
> A URL that identifies the signature suite. For example: https://w3id.org/security/v1#Ed25519Signature2018.

The URL which hosts. It will be on a publicly facing GitHub repository, or hosted on the credentials site such as: [https://credentials.workday.com/docs/specification/v1.0/signature-suite.md](https://credentials.workday.com/docs/specification/v1.0/signature-suite.md).

### Type
> The value SignatureSuite.
   
WorkEd25519Signature2020. This type is used to represent proofs.

## Verification Key

WorkEd25519VerificationKey2020. This type is used to represent keys that specifically generate WorkEd25519Signature2020 signatures.

### Canonicalization Algorithm
> A URL that identifies the canonicalization algorithm to use on the document. For example: https://w3id.org/security#URDNA2015.

This signature suite uses [Draft 17 of the JSON Canonicalization Scheme (JCS)](https://tools.ietf.org/id/draft-rundgren-json-canonicalization-scheme-17.html), currently holding the __Informational__ status, by the [IETF Network Working Group](https://datatracker.ietf.org/drafts/current/). Future versions of this specification may use later versions of JCS as the spec progresses.

### Digest Algorithm
> A URL that identifies the message digest algorithm to use on the canonicalized document. For example: https://www.ietf.org/assignments/jwa-parameters#SHA256

SHA-512, as a part of the [Ed25519](https://tools.ietf.org/html/rfc8032) signing process, as outlined in [IETF RFC 6234](https://tools.ietf.org/html/rfc6234).

### Signature Algorithm
> A URL that identifies the signature algorithm to use on the data to be signed. For example: http://w3id.org/security#ed25519 

Ed25519 as outlined in [IETF RFC 8032](https://tools.ietf.org/html/rfc8032).

## Prior Art
The signature suite, and signing methodology, find influence by information in the following documents: [Verifiable Credentials Data Model](https://w3c.github.io/vc-data-model/), [DID Core Specification](https://w3c.github.io/did-core/), [Linked Data Signatures](https://w3c-dvcg.github.io/ld-signatures), [Linked Data Proofs](https://w3c-dvcg.github.io/ld-proofs/), [Linked Data Cryptosuite Registry](https://w3c-ccg.github.io/ld-cryptosuite-registry).

## Implementation Notes

We have provided reference implementations in Golang and Java.

## References
- [Linked Data Cryptosuite Registry](https://w3c-ccg.github.io/ld-cryptosuite-registry)
- [Verifiable Credentials Data Model](https://w3c.github.io/vc-data-model/)
- [DID Core Specification](https://w3c.github.io/did-core/)
- [Linked Data Signatures](https://w3c-dvcg.github.io/ld-signatures)
- [Linked Data Proofs](https://w3c-dvcg.github.io/ld-proofs/)
- [JSON Canonicalization Scheme (JCS) Draft 17](https://tools.ietf.org/id/draft-rundgren-json-canonicalization-scheme-17.html)
- [UTF-8](https://tools.ietf.org/html/rfc3629)
- [SHA-512](https://tools.ietf.org/html/rfc6234)
- [Ed25519](https://tools.ietf.org/html/rfc8032)
