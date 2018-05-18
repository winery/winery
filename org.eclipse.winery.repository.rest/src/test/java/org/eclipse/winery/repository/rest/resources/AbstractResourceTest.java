/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.eclipse.jetty.server.Server;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.rest.server.WineryUsingHttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.xmlunit.matchers.CompareMatcher;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public abstract class AbstractResourceTest extends TestWithGitBackedRepository {

    // with trailing /
    private static final String PREFIX = "http://localhost:9080/winery/";

    private static Server server;

    @BeforeClass
    public static void init() throws Exception {
        server = WineryUsingHttpServer.createHttpServer(9080);
        server.start();
    }

    @AfterClass
    public static void shutdown() throws Exception {
        server.stop();
    }

    public static String readFromClasspath(String fileName) {
        final InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalStateException("Could not find " + fileName + " on classpath");
        }
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
    }

    protected RequestSpecification start() {
        return given()
            .log()
            .ifValidationFails();
    }

    protected String callURL(String restURL) {
        return PREFIX + Util.URLdecode(restURL);
    }

    private boolean isXml(String fileName) {
        return (fileName.endsWith("xml"));
    }

    private boolean isTxt(String fileName) {
        return (fileName.endsWith("txt"));
    }

    private boolean isZip(String fileName) {
        return (fileName.endsWith("zip") || fileName.endsWith(".csar"));
    }

    private String getAccept(String fileName) {
        if (isXml(fileName)) {
            return ContentType.XML.toString();
        } else if (fileName.endsWith("-badrequest.txt")) {
            // convention: we always expect JSON
            return ContentType.JSON.toString();
        } else if (isZip(fileName)) {
            return "application/zip";
        } else {
            return ContentType.JSON.toString();
        }
    }

    protected void assertNotFound(String restURL) {
        try {
            start()
                .get(callURL(restURL))
                .then()
                .statusCode(404);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void assertGet(String restURL, String fileName) {
        try {
            String expectedStr = readFromClasspath(fileName);
            final String receivedStr = start()
                .accept(getAccept(fileName))
                .get(callURL(restURL))
                .then()
                .log()
                .ifValidationFails()
                .statusCode(200)
                .extract()
                .response()
                .getBody()
                .asString();
            if (isXml(fileName)) {
                org.hamcrest.MatcherAssert.assertThat(receivedStr, CompareMatcher.isIdenticalTo(expectedStr).ignoreWhitespace());
            } else if (isZip(fileName)) {
                // TODO  @pmeyer: Cool ZIP equal test
                Assert.assertNotNull(receivedStr);
            } else {
                JSONAssert.assertEquals(
                    expectedStr,
                    receivedStr,
                    // we allow different ordering in lists, but not extensible JSON. That means, more elements are NOT OK. The tests need to be adapted in case new elements come in
                    JSONCompareMode.NON_EXTENSIBLE);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertGetNoContent(String restURL) {
        start()
            .get(callURL(restURL))
            .then()
            .log()
            .ifValidationFails()
            .statusCode(204);
    }

    protected void assertGetExpectBadRequestResponse(String restURL, String fileName) {
        try {
            String expectedStr = readFromClasspath(fileName);
            final String receivedStr = start()
                .accept(getAccept(fileName))
                .get(callURL(restURL))
                .then()
                .log()
                .ifValidationFails()
                .statusCode(400)
                .extract()
                .response()
                .getBody()
                .asString();
            if (isXml(fileName)) {
                org.hamcrest.MatcherAssert.assertThat(receivedStr, CompareMatcher.isIdenticalTo(expectedStr).ignoreWhitespace());
            } else if (isTxt(fileName)) {
                Assert.assertEquals(expectedStr, receivedStr);
            } else {
                JSONAssert.assertEquals(
                    expectedStr,
                    receivedStr,
                    true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertPostExpectBadRequestResponse(String restURL, String fileName) {
        try {
            start()
                .contentType(getAccept(fileName))
                .post(callURL(restURL))
                .then()
                .log()
                .ifValidationFails()
                .statusCode(400)
                .extract()
                .response()
                .getBody()
                .asString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertGetSize(String restURL, int size) {
        start()
            .accept(ContentType.JSON)
            .get(callURL(restURL))
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .body("size()", is(size));
    }

    public void assertPut(String restURL, String fileName) {
        String contents = readFromClasspath(fileName);
        start()
            .body(contents)
            .contentType(getAccept(fileName))
            .put(callURL(restURL))
            .then()
            .statusCode(204);
    }

    /**
     * Maybe remove in order to force JSON.
     */
    protected void assertPutText(String restURL, String content) {
        start()
            .body(content)
            .contentType(ContentType.TEXT)
            .put(callURL(restURL))
            .then()
            .statusCode(204);
    }

    protected void assertPost(String restURL, String fileName) {
        String contents = readFromClasspath(fileName);
        start()
            .body(contents)
            .contentType(getAccept(fileName))
            .accept(getAccept(fileName))
            .post(callURL(restURL))
            .then()
            .statusCode(201);
    }

    /**
     * Because some methods don't respond with a "created" status. TODO: fix all methods which return "noContent" status
     * so that this method can be deleted.
     */
    protected void assertNoContentPost(String restUrl, String fileName) {
        String contents = readFromClasspath(fileName);
        start()
            .body(contents)
            .contentType(getAccept(fileName))
            .post(callURL(restUrl))
            .then()
            .statusCode(204);
    }

    protected void assertPost(String restUrl, Path file) {
        start()
            .multiPart(file.toFile())
            .post(callURL(restUrl))
            .then()
            .statusCode(201);
    }

    protected void assertDelete(String restURL) {
        try {
            start()
                .delete(callURL(restURL))
                .then()
                .statusCode(204);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertUploadBinary(String restURL, String fileName) {
        given()
            .multiPart(new File(fileName))
            .then()
            .statusCode(204);
    }

    public static String replacePathStringEncoding(String toConvert) {
        return toConvert.replace("%3A", "%253A").replace("%2F", "%252F");
    }
}
