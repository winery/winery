# Hosting Configuration

We use `%2F` in URLs which is not allowed by default in many systems.
This document lists how to configure each one of them.

## Apache Tomcat

Pass `-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true` as argument.
See also <https://stackoverflow.com/a/15949229/873282>.

It is not possible to set this in Winery during execution:
It has to be configured outside.

## Apache Web Server

Use [AllowEncodedSlashes](http://httpd.apache.org/docs/2.2/mod/core.html#allowencodedslashes)
