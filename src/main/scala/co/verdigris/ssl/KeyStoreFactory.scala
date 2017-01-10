package co.verdigris.ssl

import java.security.{KeyStore, PrivateKey}
import java.security.cert.X509Certificate

/** Factory for instantiating [[KeyStore]] from certficiate and private key. */
object KeyStoreFactory {
  /** Creates a KeyStore from a given certificate, private key, and keystore password.
    *
    * @param certificate X.509 SSL certificate
    * @param privateKey RSA private key
    * @param keyStorePassword password for the keystore
    * @return a new [[KeyStore]] instance which contains the given certificate and private key that is protected by the
    *         keystore password.
    */
  def fromCertAndKey(certificate: X509Certificate, privateKey: PrivateKey, keyStorePassword: String): KeyStore = {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)

    keyStore.load(null, null)
    keyStore.setCertificateEntry("certificate", certificate)
    keyStore.setKeyEntry("private-key", privateKey, keyStorePassword.toCharArray, Array(certificate))

    keyStore
  }

  /** Creates a KeyStore from a given PEM encoded certificate string, private key string, and keystore password.
    *
    * @param certificate string representation of PEM encoded X.509 certificate
    * @param privateKey string representation of PEM encoded RSA private key
    * @param keyStorePassword password for the keystore
    * @return a new [[KeyStore]] instance which contains the given certificate and private key that is protected by the
    *         keystore password.
    */
  def fromCertAndKey(certificate: String, privateKey: String, keyStorePassword: String): KeyStore = {
    fromCertAndKey(X509CertificateFactory.fromPEM(certificate), PrivateKeyFactory.fromPEM(privateKey), keyStorePassword)
  }
}
