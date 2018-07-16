/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.common;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashingUtil.class);
    
    public static String getHashForFile(String pathInsideRepo, String algorithm) {
        try {
            File file = new File(pathInsideRepo);
            return getChecksum(file, algorithm);
        } catch (NoSuchAlgorithmException e) {
            HashingUtil.LOGGER.error("Could not instantiate hash algorithm.", e);
        } catch (IOException e) {
            HashingUtil.LOGGER.error("Could not get the specified file for hashing.", e);
        } catch (Exception e) {
            HashingUtil.LOGGER.info("Could not create hash for file <" + pathInsideRepo + ">");
        }

        return null;
    }

    public static String getChecksum(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        return new BigInteger(1, digest.digest(bytes))
            .toString(16);
    }

    public static String getChecksum(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        return getChecksum(IOUtils.toByteArray(file.toURI()), algorithm);
    }
}
