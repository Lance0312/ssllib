package co.verdigris.ssl

import java.io.ByteArrayInputStream
import java.security.cert.{CertificateFactory, X509Certificate}

import org.apache.commons.codec.binary.Base64
import sun.security.provider.X509Factory

/** Factory for instantiating [[X509Certificate]] from PEM formatted certificate string. */
object X509CertificateFactory {
  /** Create a X509Certificate instance from PEM formatted certificate string.
    *
    * @param pem string representing X.509 SSL certificate in PEM format
    * @return a new [[X509Certificate]] instance
    */
  def fromPEM(pem: String): X509Certificate = {
    val cf = CertificateFactory.getInstance("X.509")
    val pemBytes = Base64.decodeBase64(pem.stripPrefix(X509Factory.BEGIN_CERT).stripSuffix(X509Factory.END_CERT))

    cf.generateCertificate(new ByteArrayInputStream(pemBytes)).asInstanceOf[X509Certificate]
  }
}
