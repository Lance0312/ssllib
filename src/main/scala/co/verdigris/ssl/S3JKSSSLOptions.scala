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
class S3JKSSSLOptions extends SSLOptions {
  var s3URLEnvVarKey: String = "CASSANDRA_S3_TRUST_STORE_URL"
  var jksPasswordEnvVarKey: String = "CASSANDRA_TRUST_STORE_PASSWORD"
  protected var _s3url: Option[String] = None
  private var _jksPassword: Option[String] = None
  protected lazy val s3Client: AmazonS3 = {
    // Use Signature Version 4 to access S3 objects encrypted with SSE-KMS.
    clientConfiguration.setSignerOverride("AWSS3V4SignerType")
    s3ClientBuilder.setClientConfiguration(clientConfiguration)
    s3ClientBuilder.setCredentials(new ProfileCredentialsProvider())
    s3ClientBuilder.build()
  }
  protected lazy val clientConfiguration: ClientConfiguration = new ClientConfiguration()
  protected lazy val s3ClientBuilder: AmazonS3ClientBuilder = AmazonS3ClientBuilder.standard()
  protected lazy val context: SSLContext = {
    val trustStore = KeyStore.getInstance("JKS")
    val s3Object = for {
      s3URI ← Some(new AmazonS3URI(this.s3Url))
      bucket ← Some(s3URI.getBucket)
      key ← Some(s3URI.getKey)
    } yield s3Client.getObject(bucket, key)

    var inputStream: S3ObjectInputStream = null
    val initSSLContext = Try {
      inputStream = s3Object.get.getObjectContent
      trustStore.load(inputStream, this.jksPassword.toCharArray)

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

  def this(s3Url: String, jksPassword: String) = {
    this()
    this._s3url = Some(s3Url)
    this._jksPassword = Some(jksPassword)
  }

  /** URL for Amazon Web Services S3 which stores the Java Key Store (JKS) file.
    *
    * The precedence for how the URL is resolved is first by looking up the value that was passed via the constructor
    * or the setter, then by the environment variable key specified in the `s3URLEnvVarKey` property (defaults to
    * `CASSANDRA_S3_TRUST_STORE_URL`).
    */
  def s3Url: String = {
    val envVarS3Url: String = sys.env.get(s3URLEnvVarKey).orNull
    this._s3url.getOrElse(envVarS3Url)
  }

  /** Sets the Amazon Web Services S3 URL that points to a Java Key Store (JKS) file.
    *
    * @param s3Url AWS S3 URL
    */
  def s3Url_=(s3Url: String) { this._s3url = Some(s3Url) }

  /** Returns the JKS password in the same precedence as the `s3Url` property but the visibility is set to private.
    * Password access is one way only for security reasons (e.g. you can set the password but not read it back out).
    */
  private def jksPassword: String = {
    val envVarJksPassword: String = sys.env.get(jksPasswordEnvVarKey).orNull
    this._jksPassword.getOrElse(envVarJksPassword)
  }

  /** Sets the JKS password required to decrypt the KeyStore.
    *
    * @param password the password to decrypt the entries in the Java KeyStore
    */
  def jksPassword_=(password: String) { this._jksPassword = Some(password) }

  override def newSSLHandler(channel: SocketChannel): SslHandler = {
    lazy val sslEngine = {
      val engine = context.createSSLEngine()
      engine.setUseClientMode(true)
      engine
    }

    new SslHandler(sslEngine)
  }
}
