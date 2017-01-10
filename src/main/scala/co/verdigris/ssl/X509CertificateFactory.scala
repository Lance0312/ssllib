package co.verdigris.ssl

import java.io.ByteArrayInputStream
import java.security.cert.{CertificateFactory, X509Certificate}

import org.apache.commons.codec.binary.Base64
import sun.security.provider.X509Factory

object X509CertificateFactory {
  def fromPEM(pem: String): X509Certificate = {
    val cf = CertificateFactory.getInstance("X.509")
    val pemBytes = Base64.decodeBase64(pem.stripPrefix(X509Factory.BEGIN_CERT).stripSuffix(X509Factory.END_CERT))

    cf.generateCertificate(new ByteArrayInputStream(pemBytes)).asInstanceOf[X509Certificate]
  }
}
