# SslLib

[![CircleCI](https://img.shields.io/circleci/project/VerdigrisTech/ssllib.svg)](https://circleci.com/gh/VerdigrisTech/ssllib)
[![Codecov](https://img.shields.io/codecov/c/github/VerdigrisTech/ssllib/master.svg)](https://codecov.io/gh/VerdigrisTech/ssllib)
[![GitHub release](https://img.shields.io/github/release/VerdigrisTech/ssllib.svg)](https://github.com/VerdigrisTech/ssllib/releases)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/VerdigrisTech/ssllib/blob/master/LICENSE)

A simple SSL helper library for Scala that contains factories generating
X509 certificates, private keys, and KeyStore instances from PEM encoded
strings.

## Usage

### SBT

In `build.sbt`:

```scala
libraryDependencies += "co.verdigris.ssl" %% "ssllib" % "0.2.0"

resolvers += "Verdigris Scala Lib Repository" at "https://s3.amazonaws.com/scala-jars"
```

## Documentation

Basic usage documentation has been moved to the [wiki](https://github.com/VerdigrisTech/ssllib/wiki).

### API Documentation

For more information, check out the [Scaladocs](https://verdigristech.github.io/ssllib/latest/api)
on API usage.

## License

Distributed under [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
