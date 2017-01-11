# SslLib

[![CircleCI](https://img.shields.io/circleci/project/VerdigrisTech/ssllib.svg)](https://circleci.com/gh/VerdigrisTech/ssllib)
[![Codecov](https://img.shields.io/codecov/c/github/VerdigrisTech/ssllib/master.svg)](https://codecov.io/gh/VerdigrisTech/ssllib)
[![GitHub release](https://img.shields.io/github/release/VerdigrisTech/ssllib.svg)](https://github.com/VerdigrisTech/ssllib/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/VerdigrisTech/ssllib/blob/master/LICENSE)

A simple SSL helper library for Scala that contains factories generating
X509 certificates, private keys, and KeyStore instances from PEM encoded
strings.

## Usage

### SBT

In `build.sbt`:

```scala
libraryDependencies += "co.verdigris.ssl" %% "ssllib" % "0.1.0"

resolvers += "Verdigris Scala Lib Repository" at "https://s3.amazonaws.com/scala-jars"
```

### X509Certificate

To get an instance of X509Certificate object, pass in the PEM encoded
string to the `fromPEM` method:

```scala
import co.verdigris.ssl._

// Actual string would be much longer. Abbreviated for example.
val pem =
    s"""-----BEGIN CERTIFICATE-----
       |MIIGIzCCBAugAwIBAgIJAJW7Ipl+TXmYMA0GCSqGSIb3DQEBCwUAMIGnMQswCQYD
       |...
       |-----END CERTIFICATE-----""".stripMargin

val cert = X509CertificateFactory.fromPEM(pem)
```

### PrivateKey

To get an instance of PrivateKey object, pass in the PEM encoded string
to the `fromPEM` method:

```scala
import co.verdigris.ssl._

// Actual string would be much longer. Abbreviated for example. Also,
// it is extremely bad security practice to embed unprotected PKCS1
// encoded private key into your source code. Never do this for
// production code!!!
val pem =
    s"""-----BEGIN RSA PRIVATE KEY-----
       |MIIJKAIBAAKCAgEAzcpAXdLceRbWR+NApZl+kKNc3xvG5hlnNEFZnyR4DG8Td/cy
       |...
       |-----END RSA PRIVATE KEY-----""".stripMargin

val privateKey = PrivateKey.fromPEM(pem)
```

### KeyStore

To get an instance of KeyStore object, pass in the certificate and
private key:

```scala
import co.verdigris.ssl._

// Actual string would be much longer. Abbreviated for example.
val certPem =
    s"""-----BEGIN CERTIFICATE-----
       |MIIGIzCCBAugAwIBAgIJAJW7Ipl+TXmYMA0GCSqGSIb3DQEBCwUAMIGnMQswCQYD
       |...
       |-----END CERTIFICATE-----""".stripMargin

val privateKeyPem =
    s"""-----BEGIN RSA PRIVATE KEY-----
       |MIIJKAIBAAKCAgEAzcpAXdLceRbWR+NApZl+kKNc3xvG5hlnNEFZnyR4DG8Td/cy
       |...
       |-----END RSA PRIVATE KEY-----""".stripMargin

val cert = X509CertificateFactory.fromPEM(pem)
val privateKey = PrivateKey.fromPEM(pem)
val keyStore = KeyStoreFactory.fromCertAndKey(cert, privateKey, "somekeystorepassword")
```

If you have the PEM encoded string for both certificate and private key,
you can also pass them in directly instead of trying to pass in
X509Certificate and PrivateKey object which requires more boilerplate
code:

```scala
// certPem and privateKeyPem from above example
val keyStore = KeyStoreFactory.fromCertAndKey(certPem, privateKeyPem, "reallysecurepassword1234")
```

## API Documentation

For more information, check out the [Scaladocs](https://verdigristech.github.io/ssllib/latest/api)
on API usage.

## License

Distributed under [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
