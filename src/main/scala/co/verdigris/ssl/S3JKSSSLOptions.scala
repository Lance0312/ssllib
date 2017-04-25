package co.verdigris.ssl

import java.security.KeyStore
import javax.net.ssl.{SSLContext, TrustManagerFactory}

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder, AmazonS3URI}
import com.datastax.driver.core.SSLOptions
import io.netty.channel.socket.SocketChannel
import io.netty.handler.ssl.SslHandler

import scala.util.{Failure, Success, Try}

/**
  * Uses Java KeyStore file stored on Amazon Web Services S3 to build the SSL context and handler which is used by
  * the Cassandra driver to enable client-to-node encryption.
  *
  * Can be instantiated directly with S3 URL and the Java KeyStore password or through environment variables which is
  * useful if accessing from AWS Lambda.
  */
class S3JKSSSLOptions(
    var s3Url: Option[String],
    private var jwsPassword: Option[String],
    var awsRegion: Option[String] = None) extends SSLOptions {
  protected lazy val clientConfiguration: ClientConfiguration = new ClientConfiguration()
  protected lazy val s3ClientBuilder: AmazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
  protected lazy val s3Client: AmazonS3 = {
    // Use Signature Version 4 to access S3 objects encrypted with SSE-KMS.
    clientConfiguration.setSignerOverride("AWSS3V4SignerType")
    s3ClientBuilder.setClientConfiguration(clientConfiguration)

    if (this.awsRegion.nonEmpty) s3ClientBuilder.withRegion(this.awsRegion.mkString)

    s3ClientBuilder.build()
  }
  protected lazy val context: SSLContext = {
    val trustStore = KeyStore.getInstance("JKS")
    val s3Object = for {
      s3URI ← Some(new AmazonS3URI(this.s3Url.mkString))
      bucket ← Some(s3URI.getBucket)
      key ← Some(s3URI.getKey)
    } yield s3Client.getObject(bucket, key)

    var inputStream: S3ObjectInputStream = null
    val initSSLContext = Try {
      inputStream = s3Object.get.getObjectContent
      trustStore.load(inputStream, this.jwsPassword.mkString.toCharArray)

      val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
      trustManagerFactory.init(trustStore)

      val sslContext = SSLContext.getInstance("TLS")
      sslContext.init(null, trustManagerFactory.getTrustManagers, null)
      sslContext
    }

    // Close the stream or we'll have a memory leak on our hands.
    if (inputStream != null) inputStream.close()

    initSSLContext match {
      case Success(sslContext) ⇒ sslContext
      case Failure(e) ⇒ throw e
    }
  }

  def this(
    s3Url: String,
    jksPassword: String,
    awsRegion: String) = this(Some(s3Url), Some(jksPassword), Some(awsRegion))

  def this(s3Url: String, jksPassword: String) = this(Some(s3Url), Some(jksPassword), None)

  def this() = this(
    sys.env.get(S3JKSSSLOptions.ENV_VAR_S3_TRUST_STORE_URL),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_JKS_PASSWORD)
  )

  override def newSSLHandler(channel: SocketChannel): SslHandler = {
    lazy val sslEngine = {
      val engine = context.createSSLEngine()
      engine.setUseClientMode(true)
      engine
    }

    new SslHandler(sslEngine)
  }
}

object S3JKSSSLOptions {
  val ENV_VAR_S3_TRUST_STORE_URL = "CASSANDRA_S3_TRUST_STORE_URL"
  val ENV_VAR_JKS_PASSWORD = "CASSANDRA_TRUST_STORE_PASSWORD"
}
