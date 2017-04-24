package co.verdigris.ssl

import java.io.IOException

import com.amazonaws.services.s3.model.AmazonS3Exception
import com.datastax.driver.core.SSLOptions
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslHandler
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class S3JKSSSLOptionsTest extends FlatSpec with Matchers with BeforeAndAfter {
  var sslOptions: S3JKSSSLOptions = _

  before {
    sslOptions = new S3JKSSSLOptions("s3://ssllib/testkeystore.jks", "password", "us-east-1")
  }

  "Constructor" should "return an instance that implements SSLOptions interface" in {
    sslOptions shouldBe a [SSLOptions]
  }

  it should "initialize awsRegion property to None when region is not passed in" in {
    sslOptions = new S3JKSSSLOptions("s3://ssllib/testkeystore.jks", "password")
    sslOptions.awsRegion shouldBe None
  }

  it should "fallback to environment variables when no arguments are passed in" in {
    sslOptions = new S3JKSSSLOptions()

    // Can only test s3Url property because JKS password is a private property for security reasons.
    sslOptions.s3Url shouldBe sys.env.get(S3JKSSSLOptions.ENV_VAR_S3_TRUST_STORE_URL)
  }

  "newSSLHandler" should "return an SslHandler" in {
    sslOptions.newSSLHandler(new NioSocketChannel()) shouldBe a [SslHandler]
  }

  it should "throw an exception when S3 URL contains the wrong bucket" in {
    val sslOptions = new S3JKSSSLOptions("s3://invalidssllibbucket/404.jks", "password", "us-east-1")
    an [AmazonS3Exception] should be thrownBy sslOptions.newSSLHandler(new NioSocketChannel())
  }

  it should "throw an exception when S3 URL contains the wrong key" in {
    val sslOptions = new S3JKSSSLOptions("s3://ssllib/404.jks", "password", "us-east-1")
    an [AmazonS3Exception] should be thrownBy sslOptions.newSSLHandler(new NioSocketChannel())
  }

  it should "throw an exception when JKS password is incorrect" in {
    val sslOptions = new S3JKSSSLOptions("s3://ssllib/testkeystore.jks", "otherpassword", "us-east-1")
    an [IOException] should be thrownBy sslOptions.newSSLHandler(new NioSocketChannel())
  }
}
