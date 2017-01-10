package co.verdigris.ssl

import java.io.IOException
import java.security.PrivateKey

import org.scalatest.{FlatSpec, Matchers}

class PrivateKeyFactorySpec extends FlatSpec with Matchers {
  "fromPEM" should "return a PrivateKey" in {
    val pem =
      s"""-----BEGIN RSA PRIVATE KEY-----
         |MIIJKAIBAAKCAgEAzcpAXdLceRbWR+NApZl+kKNc3xvG5hlnNEFZnyR4DG8Td/cy
         |MbFFV07M8jQmEARdUjMwlKvd4L7H20LFDXcWGwnKT0dJ/jAFxlxaYlLppWQyZCrI
         |Yo8CWvSnbT050+cmKxk12Q6BBNkqlFxd2qvR+Ghut2CJjR77/XTtNW3j8sCaMQII
         |kMc4aRPeWrwkNLtuoWfSXXipwWfkbxUZuHLXtsEiw2ZR7+cZXMa/rdwdkWkvxCtT
         |vYoT/9lXrcu9aGOFUTQivNeknda9vLlogRUvXOpiD7/SsTZMysi7fI51xVf86kj6
         |KVBMIFK8zeubtSmYYXlp11QghW5+H5hHBKHB+8NQMsdlBS8eREWSLWgtfEkQWDDl
         |kZsBuepy30UzSkZEO9VriKD0wBIDjx7oM12fRLRLFRPV1lDlxNRhFqsohlvK3suA
         |keblL0xVkZOIE2/QwMp1gSY/FJEtBANTI43Tyo2KFLet+ocPHvv3/eUjnjqa/cH1
         |olVJ2RoZNJsNVXry/TWShtiwIg4Fn3CN3vbntdZPEE+ioD9Pb8NrdFxtQfJKNf5t
         |LsMOA3QWVtDztJs0x/GCpJGSTH5k2gjBFyfZytU8xrZmqT8XDFUXrMZbqNbcYk/M
         |iBDPTelbK7HWQ1PDdlG0+MUHU+Fc3uSgE0oVM1Bfw4t4eiGcLUGviY5jejkCAwEA
         |AQKCAgALMCK9txgLcUVnFSDPn/+0h10mOBFhMyJcQe1IbKgmLOfHDuZoszWM4Jjd
         |1h33ovUb4fKTMqDWdijRb9jcuYu3HpokI0EPk7bMqPq0+8L+HetrbLQGP7YmEcUE
         |eF6reZamozE9Qr+a7t+Uv0zO9aZttefHCnWP+Y5Dola/H+FTskrvvSeQD3Suqcjd
         |7/qb71C9b7KNKGE6RZayv1ftoji+E9P7AZYUz+CeHaQQJLZq0GgrZxufY2Fg+IT+
         |yuPaSAbYelglIigt+J3KI95dnjQ7/fYk2w3uWoBkSfN/exNGDlmOtzb6hgYNf9IJ
         |8/Q8Li29puctMMKv3mj2+MVrqAuutmONYCWczmfIM93LU73N9xd8lp+Q3f2DV9pa
         |ibSC7BBL8W+hvApdpwX+2fr8KiNHGKhAq3sHwZ9HGlSZeD0cRlys4KUwewUuq6LF
         |gC5P/zaez9AgjZt9hiWURKytW7+gAy/7utYKFuEYVaBdz6ILUQKN6W45nPlQ2AOd
         |dQTpB0Pl5aWrx5sBML2Ojp11bJ+lnRVL6gKX0aGUraAM4DrKzTSCjNJUsxls2m7D
         |UTx+p9XJC3qd75J/65NKk1XwhSIHgOWkZ8Fn685OmqfF5vexCDzaCAPJW1xEgDt9
         |rCVS71tw4Mq6UGN+1MXEPg0IBf4c+aBwVJDzzCJoFIVm1xMLcQKCAQEA5iesNpIj
         |wlPJdk/jYHCTH5RetBvlrIZM+9TgdTIu1Rws2FEX7LAi9wm0i68WtWU609hQjjFI
         |VLDhYf3gL9Fe4S1pYdFj+PvEylxZM37NiYgyIXtWBt7k0TLUtGepoMT7xibEEDm8
         |o09UZa/0gOp4n4QHqjWJkG4Cr7S2WwaNXAtycGIKsRRkIOqb53nsYSgf0jifvV4l
         |suG9vxvPzvTNa67GV0j9gi9XjCXvqEesRP/Nfi3SEM4QQjXRByQHv3tKi10l0j37
         |tXoVWmXhPuzPynhwRgJIsowI0/oeY1PARCUZ15ZuAt77rQIjWK5j6vg4/Q0FCLo/
         |nBEkLZWDh9XUjQKCAQEA5OYnUJdSFXOR5gkVQRB+L3y3Tlyj/PfqtuI5cAJ/xkhR
         |imPbtjNkUPXW1zRcBQZKQjXjOJHbUf4WRcpU0F5fxmuBiEmTOOAGpl6XE4ekxmjj
         |RIkx0tpSQ4bDuzN3mKLDXkW4DwGA2/nm/NULwzgAPXqfTLGUesl1CSWprGvMfa3o
         |NRcXZtn6cRyAU2e4qxUkTCjC9fVQEcCozifyIUn3XTMOaqcQmlBE4kpgZ7KUId5o
         |gUthU4q4YsBypucA9fH2ah+f7bp739D0UtOq//QzXk6onhnCH7Vy4zqMJbMTksn0
         |/zaIPD5i9nLx4MdcPWfrU95HdKPn0QrXJfUjhYUPXQKCAQEAxEFYLEB5lZ90zxpY
         |PUhBbFBMYTDYrEDpLGTd8ZGVNcwMwXiE+iNH7XetKN9occ2cWEDAkD+d+fnKdpDR
         |tnTGfyUGE49FvG6T5CrTNYIPKhOlC7/sH3e5jlnV3u/2SU0vlExCK8PQA0IGKZ/c
         |0oEd0of3MUDtyObGC6YBSaVQrQhl0UcI7u8AYsErqDrjQgXA/LWkIQpDA0JTVr8U
         |eNluwUALiSqIYZaVBMDkl45ma3/lbo8L7aCJZKwXTgt/P9yWODh5o8mO9bTguHDf
         |nZS5q2D/+/K+ouGeNYO2neQwsusgGiIANRqHaYPFOD2cniSEE+QiQsruqtpDC0vP
         |GOlRkQKCAQA18qCVd/SapbBfPQjLWu3Cl2xEhcUInA2vqMXMxmoC+4xsR3jDCn28
         |LxTBh8+gR4k87aJt9MJyIqZxMGyo53U/OJtA2cZfAz3N/EEzEycctB/MSWF6xoXY
         |8Gw1NL4dNU9CasAkmUAbmFx+fl/VVN+JndrponjSL6Tr0PqabpW1kUVHLfgP/5Pt
         |EMCV5744ZDU31euNOJH0fSnL3NJOUjaAusXNES7yj7SMeOcbyZTsHpUW1ANOQ0WN
         |0Lm06IDrdmmTPXzd9LO5XnAXDVgiFxX8RiGrkXqVKpi3QVdCk2ArmnmppNqbCEEe
         |wJyNU69Dsbhe/eZtcSvzstBeW9ZRY7jpAoIBAGQaGwTsr3SUaAmiu4xwt+bAVdk3
         |zywb2Pm4d0nXeTVIiPb1DGbYvequFeH9qkQaSlZ5kGMi+RkB3QvHUvpqP8v8/TBw
         |3pZwCrCEdNv947fsIOZOClmZF1AZ7HpmwETNHhk5mqWmDn8Pk2bbgXRd/7A/m3fB
         |tp3Qn9trbj+3MEJ7x4orV6XsblK78H4DmBYXAF8UDXCtNCLBnKeUzGAVWdZrK2++
         |MDbeegELOHmkI/dHrxK+gkSVUCq9c3BZWEJ1xU0T4oK+U9pMAiDyrHZjeej++0s9
         |kooRxdZI8YWsrPMvQ1+r5gX9KHYqs5Ga/HmpI2ojO0+hvMnKUGSXAhA0ptY=
         |-----END RSA PRIVATE KEY-----""".stripMargin
    val privateKey = PrivateKeyFactory.fromPEM(pem)

    privateKey shouldBe a [PrivateKey]
  }

  it should "parse without the optional prefix and suffix" in {
    val pem =
      s"""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""".stripMargin
    val privateKey = PrivateKeyFactory.fromPEM(pem)

    privateKey shouldBe a [PrivateKey]
  }

  it should "fail on invalid string" in {
    val pem =
      s"""-----BEGIN RSA PRIVATE KEY-----
         |Never gonna give you up, never gonna let you down
         |Never gonna run around and desert you
         |Never gonna make you cry, never gonna say goodbye
         |Never gonna tell a lie and hurt you
         |-----END RSA PRIVATE KEY-----""".stripMargin

    the [IOException] thrownBy PrivateKeyFactory.fromPEM(pem) should have message "Sequence tag error"
  }
}
