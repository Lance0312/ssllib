package co.verdigris.ssl

import java.security.{KeyStore, PrivateKey}
import java.security.cert.X509Certificate

object KeyStoreFactory {
  def fromCertAndKey(certificate: X509Certificate, privateKey: PrivateKey, keyStorePassword: String): KeyStore = {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)

    keyStore.load(null, null)
    keyStore.setCertificateEntry("certificate", certificate)
    keyStore.setKeyEntry("private-key", privateKey, keyStorePassword.toCharArray, Array(certificate))

    keyStore
  }

  def fromCertAndKey(certificate: String, privateKey: String, keyStorePassword: String): KeyStore = {
    fromCertAndKey(X509CertificateFactory.fromPEM(certificate), PrivateKeyFactory.fromPEM(privateKey), keyStorePassword)
  }
}
