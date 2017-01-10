package co.verdigris.ssl

import java.security.{KeyStore, PrivateKey}
import java.security.cert.X509Certificate

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class KeyStoreFactorySpec extends FlatSpec with Matchers with BeforeAndAfter {
  var certificatePEM: String = _
  var certificate: X509Certificate = _
  var privateKeyPEM: String = _
  var privateKey: PrivateKey = _

  before {
    certificatePEM =
      s"""-----BEGIN CERTIFICATE-----
         |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
         |-----END CERTIFICATE-----""".stripMargin

    privateKeyPEM =
      s"""-----BEGIN RSA PRIVATE KEY-----
         |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
         |-----END RSA PRIVATE KEY-----""".stripMargin

    certificate = X509CertificateFactory.fromPEM(certificatePEM)
    privateKey = PrivateKeyFactory.fromPEM(privateKeyPEM)
  }

  "fromCertAndKey" should "return an instance of KeyStore" in {
    KeyStoreFactory.fromCertAndKey(certificate, privateKey, "testkeystorepassword") shouldBe a [KeyStore]
  }

  it should "accept PEM encoded strings as arguments and return an instance of KeyStore" in {
    KeyStoreFactory.fromCertAndKey(certificatePEM, privateKeyPEM, "testkeystorepassword2") shouldBe a [KeyStore]
  }
}
