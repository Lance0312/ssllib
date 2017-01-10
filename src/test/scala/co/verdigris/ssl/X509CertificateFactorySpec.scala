package co.verdigris.ssl

import java.security.cert.{CertificateException, X509Certificate}

import org.scalatest._

class X509CertificateFactorySpec extends FlatSpec with Matchers {
  "fromPEM" should "return a X509Certificate" in {
    val pem =
      s"""-----BEGIN CERTIFICATE-----
         |MIIGIzCCBAugAwIBAgIJAJW7Ipl+TXmYMA0GCSqGSIb3DQEBCwUAMIGnMQswCQYD
         |VQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNTW91bnRhaW4g
         |VmlldzEjMCEGA1UECgwaVmVyZGlncmlzIFRlY2hub2xvZ2llcyBJbmMxCzAJBgNV
         |BAsMAklUMRkwFwYDVQQDDBBUZXN0IENlcnRpZmljYXRlMR4wHAYJKoZIhvcNAQkB
         |Fg9jYUB2ZXJkaWdyaXMuY28wHhcNMTcwMTEwMDgxODQ2WhcNMTcwMTExMDgxODQ2
         |WjCBpzELMAkGA1UEBhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcM
         |DU1vdW50YWluIFZpZXcxIzAhBgNVBAoMGlZlcmRpZ3JpcyBUZWNobm9sb2dpZXMg
         |SW5jMQswCQYDVQQLDAJJVDEZMBcGA1UEAwwQVGVzdCBDZXJ0aWZpY2F0ZTEeMBwG
         |CSqGSIb3DQEJARYPY2FAdmVyZGlncmlzLmNvMIICIjANBgkqhkiG9w0BAQEFAAOC
         |Ag8AMIICCgKCAgEA6OYDB0MMlwzoWVnQA+BAKXQbJJfWmTnDoRlo6gSfH3V/on0h
         |TOMZ4Ba52yO04TrP4F8QcKIm8gtwufeA6MaF+ook9L7rQ4oxciNImI+zcvSaDmWl
         |aZGZndFc2s8ZgIvX5qIc4+cvWHrGLTX5xfGbvGDu8f/aIcfmPcvKgIqs8i8kj2jI
         |0c/1+P6UINa/i6VZYZ3DLW/hyzwQVUY+ms9h1daEvbUfiWgW82j+5B9+rRjgwGRk
         |ukSSdWf3YuEvtunPYi6GOg4zWK9S7ectSaDkQWBSihaHAFMw5yRgPyDOYLam5w6j
         |9VyrjHU3kKHEJSF6TMX6i7olCezn2kvhztZLI1PZ1IHbteUSDOPsdGu3pEKu8xb7
         |gdDRLln/zOgqwtY1rKrC3Wpu//I2LJer2IeogDxKMllNHWLu4iK0oot8FRpxdmq1
         |roHLHjwXSjU4/li6Z79CpRuPAixAi3PqB3pcwx/2oEFKUu05TmGnQifS8B3p/HzJ
         |Fk59Ii7657xSIEnWcyEAKboTc8kjo/WcXkUoA4IjlA3nwLqQ4wtAdoD7QjEnStzO
         |XO6PnLIQ5eS8n1+/HNwZq3MWftftIR64ZBQpmtcVZOzWadIwPsNBw0Gg3/CE0HRW
         |CEebOMa6ctcBVIauzDxSYuORIqblv5K0JGm8vqKD0nefzPXNrvpSbJF7nV8CAwEA
         |AaNQME4wHQYDVR0OBBYEFJzcUynFHk7hpO7c3Mr4osJ6iyBmMB8GA1UdIwQYMBaA
         |FJzcUynFHk7hpO7c3Mr4osJ6iyBmMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEL
         |BQADggIBAFBbSbj3fjEZRkvIR+IrnZs6igllnUDPNQdxTDvVFaDzt/tpphqruysD
         |XjWue6RXHvw1xXdcbEGj3w/ZADJ7crNpo4X3KkZ5AlUZve/9d2HbyRClkiazt1I0
         |lEHfa27w9woaZAP538Ls6A5qeuIL+vMsT9pQiqLuZ69QrwULz8WYC/z8AoRD4CY+
         |uKVXGbuDVS1xEMdTwCRQ60uOh4Odu8LpB2lPzf6JvKa2nEORZ/Xc43eCKLVMjXyo
         |RsjVKGTdd+Us3sS7le0R6nym+/vhaD39dPvMTRV+A8aL1YSxcniwu7N0AHGnAtyo
         |LTrgGWyX1N5BTyJ2r4v6LDtM2BFhi5fQSKXDQKYb/TEG6UIQwclrNHV55Ge5t1aR
         |sKom32Zf92KDBUk8sH+/rljbTelykkv+QL+Qa5/4qhg6wfolgVWPmsxRhcshUxPe
         |yq95iXvr+Ls6CWbyU7x10mCFSDb3rJcQK2iNp0yudbiekMQeM1I++3msU256UZtU
         |bVs1r71+WbdSk9ejBS4P6cjc/6PjukMn/ooZHvYWlRQ5dSYvDzK3kEXPijJL77no
         |+TSQpn4K87WkeYtehU2iEsFAu4taLeakdgbvOuI7zrPQDZ1wY7BBIeZnia2dBJyC
         |G7x+oqXzs4W0EZiJ6SfZ7ENn4wMcHyIXlshac/xncdJXBzpagVvE
         |-----END CERTIFICATE-----""".stripMargin

    X509CertificateFactory.fromPEM(pem) shouldBe a [X509Certificate]
  }

  it should "parse without the optional prefix and suffix" in {
    val pem =
      s"""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""".stripMargin

    X509CertificateFactory.fromPEM(pem) shouldBe a [X509Certificate]
  }

  it should "fail on invalid string" in {
    val pem =
      s"""-----BEGIN CERTIFICATE-----
         |Never gonna give you up, never gonna let you down
         |Never gonna run around and desert you
         |Never gonna make you cry, never gonna say goodbye
         |Never gonna tell a lie and hurt you
         |-----END CERTIFICATE-----""".stripMargin

    a [CertificateException] should be thrownBy X509CertificateFactory.fromPEM(pem)
  }
}
