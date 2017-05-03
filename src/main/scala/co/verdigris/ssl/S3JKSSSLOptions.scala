package co.verdigris.ssl

import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import com.amazonaws.ClientConfiguration
import com.amazonaws.services.s3.model.{S3Object, S3ObjectInputStream}
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
    var s3TrustStoreUrl: Option[String],
    private var trustStorePassword: Option[String],
    var trustStoreType: Option[String] = Some("JKS"),
    var s3KeyStoreUrl: Option[String] = None,
    private var keyStorePassword: Option[String] = None,
    var keyStoreType: Option[String] = None,
    var awsRegion: Option[String] = None) extends SSLOptions {
  protected lazy val clientConfiguration: ClientConfiguration = new ClientConfiguration()
  protected lazy val s3ClientBuilder: AmazonS3ClientBuilder = AmazonS3ClientBuilder.standard()

  protected lazy val s3TrustStoreURI: Option[AmazonS3URI] = this.getS3URI(this.s3TrustStoreUrl)
  protected lazy val trustStoreS3Object: Option[S3Object] = this.getS3Object(s3TrustStoreURI)
  protected lazy val trustStoreInputStream: Option[S3ObjectInputStream] = getS3ObjectInputStream(trustStoreS3Object)
  protected lazy val trustStore: Option[KeyStore] = this.getKeyStore(this.s3TrustStoreUrl, this.trustStoreType)
  protected lazy val tmf: Option[TrustManagerFactory] = for {
    ts ← this.trustStore
    tsInputStream ← this.trustStoreInputStream
  } yield {
    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    ts.load(tsInputStream, this.trustStorePassword.map(_.toCharArray).orNull)
    tmf.init(ts)
    tmf
  }

  protected lazy val s3KeyStoreURI: Option[AmazonS3URI] = this.getS3URI(this.s3KeyStoreUrl)
  protected lazy val keyStoreS3Object: Option[S3Object] = this.getS3Object(s3KeyStoreURI)
  protected lazy val keyStoreInputStream: Option[S3ObjectInputStream] = getS3ObjectInputStream(keyStoreS3Object)
  protected lazy val keyStore: Option[KeyStore] = this.getKeyStore(this.s3KeyStoreUrl, this.keyStoreType)
  protected lazy val kmf: Option[KeyManagerFactory] = for {
    ks ← this.keyStore
    ksInputStream ← this.keyStoreInputStream
  } yield {
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    ks.load(ksInputStream, this.keyStorePassword.map(_.toCharArray).orNull)
    kmf.init(ks, this.keyStorePassword.map(_.toCharArray).orNull)
    kmf
  }

  protected lazy val s3Client: AmazonS3 = {
    // Use Signature Version 4 to access S3 objects encrypted with SSE-KMS.
    clientConfiguration.setSignerOverride("AWSS3V4SignerType")
    s3ClientBuilder.setClientConfiguration(clientConfiguration)

    if (this.awsRegion.nonEmpty) s3ClientBuilder.withRegion(this.awsRegion.mkString)

    s3ClientBuilder.build()
  }

  protected lazy val context: SSLContext = {
    val initSSLContext = Try {
      val sslContext = SSLContext.getInstance("TLS")
      sslContext.init(this.kmf.map(_.getKeyManagers).orNull, this.tmf.map(_.getTrustManagers).orNull, null)
      sslContext
    }

    // Close the stream or we'll have a memory leak on our hands.
    for (tsInputStream ← this.trustStoreInputStream) yield { tsInputStream.close() }
    for (ksInputStream ← this.keyStoreInputStream) yield { ksInputStream.close() }

    initSSLContext match {
      case Success(sslContext) ⇒ sslContext
      case Failure(e) ⇒ throw e
    }
  }

  def this(
      s3TrustStoreUrl: Option[String],
      trustStorePassword: Option[String],
      trustStoreType: Option[String],
      awsRegion: Option[String]) = {
    this(s3TrustStoreUrl, trustStorePassword, trustStoreType, None, None, None, awsRegion)
  }

  def this(
      s3TrustStoreUrl: String,
      trustStorePassword: String,
      trustStoreType: String,
      s3KeyStoreUrl: String,
      keyStorePassword: String,
      keyStoreType: String,
      awsRegion: String) = this(
    Some(s3TrustStoreUrl),
    Some(trustStorePassword),
    Some(trustStoreType),
    Some(s3KeyStoreUrl),
    Some(keyStorePassword),
    Some(keyStoreType),
    Some(awsRegion))

  def this(
      s3TrustStoreUrl: String,
      trustStorePassword: String,
      trustStoreType: String,
      s3KeyStoreUrl: String,
      keyStorePassword: String,
      keyStoreType: String) = this(
    Some(s3TrustStoreUrl),
    Some(trustStorePassword),
    Some(trustStoreType),
    Some(s3KeyStoreUrl),
    Some(keyStorePassword),
    Some(keyStoreType),
    None)

  def this(
      s3TrustStoreUrl: String,
      trustStorePassword: String,
      trustStoreType: String,
      awsRegion: String) = {
    this(Some(s3TrustStoreUrl), Some(trustStorePassword), Some(trustStoreType), awsRegion = Some(awsRegion))
  }

  def this(
      s3TrustStoreUrl: String,
      trustStorePassword: String,
      trustStoreType: String) = this(Some(s3TrustStoreUrl), Some(trustStorePassword), Some(trustStoreType))

  def this(s3TrustStoreUrl: String, trustStorePassword: String) = this(Some(s3TrustStoreUrl), Some(trustStorePassword))

  def this() = this(
    sys.env.get(S3JKSSSLOptions.ENV_VAR_S3_TRUST_STORE_URL),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_TRUST_STORE_PASSWORD),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_TRUST_STORE_TYPE),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_S3_KEY_STORE_URL),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_KEY_STORE_PASSWORD),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_KEY_STORE_TYPE),
    sys.env.get(S3JKSSSLOptions.ENV_VAR_S3_REGION)
  )

  protected def getS3URI(url: Option[String]): Option[AmazonS3URI] = url match {
    case Some("") | None ⇒ None
    case Some(u) ⇒ Some(new AmazonS3URI(u))
  }

  protected def getS3Object(uri: Option[AmazonS3URI]): Option[S3Object] = Try {
    for {
      s3URI ← uri
      bucket ← Some(s3URI.getBucket)
      key ← Some(s3URI.getKey)
    } yield {
      s3Client.getObject(bucket, key)
    }
  } match {
    case Success(s3Object) ⇒ s3Object
    case Failure(e) ⇒ throw e
  }

  protected def getS3ObjectInputStream(s3Object: Option[S3Object]): Option[S3ObjectInputStream] = s3Object match {
    case Some(s3Obj) ⇒ Some(s3Obj.getObjectContent)
    case None ⇒ None
  }

  protected def getKeyStore(
      url: Option[String],
      keyStoreType: Option[String]): Option[KeyStore] = (url, keyStoreType) match {
    case (Some(""), _) | (None, _) ⇒ None
    case (Some(_), ksType) ⇒ Some(KeyStore.getInstance(ksType.getOrElse("JKS")))
  }

  protected def getTrustManagerFactory(trustStore: Option[KeyStore]): Option[TrustManagerFactory] = trustStore match {
    case Some(_) ⇒ Some(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm))
    case None ⇒ None
  }

  protected def getKeyManagerFactory(keyStore: Option[KeyStore]): Option[KeyManagerFactory] = keyStore match {
    case Some(_) ⇒ Some(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm))
    case None ⇒ None
  }

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
  val ENV_VAR_TRUST_STORE_PASSWORD = "CASSANDRA_TRUST_STORE_PASSWORD"
  val ENV_VAR_TRUST_STORE_TYPE = "CASSANDRA_TRUST_STORE_TYPE"
  val ENV_VAR_S3_KEY_STORE_URL = "CASSANDRA_S3_KEY_STORE_URL"
  val ENV_VAR_KEY_STORE_PASSWORD = "CASSANDRA_KEY_STORE_PASSWORD"
  val ENV_VAR_KEY_STORE_TYPE = "CASSANDRA_KEY_STORE_TYPE"
  val ENV_VAR_S3_REGION = "CASSANDRA_S3_REGION"

  class Builder {

  }

  def builder(): Builder = new Builder
}
