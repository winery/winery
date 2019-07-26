/********************************************************************************
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

package org.eclipse.winery.accountability;

public class GanacheManager {
    
    private static GanacheManager instance;
    private final String privateKey;
    
    public static GanacheManager getInstance(final String privateKey) throws InterruptedException {
        if (instance == null || !instance.privateKey.equals(privateKey)) {
            // if key changed stop previous instance.
            if (instance != null) {
                instance.stopGanache();
            }
            
            instance = new GanacheManager(privateKey);
        } 
        
        return instance;
    }
    
    private Process process;
    
    private GanacheManager(final String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * Starts Ganache in the Console for Integration Tests
     */
    public void startGanache() throws Exception {
        String OS = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder = new ProcessBuilder();
        final String blockGasLimit = "471238800";
        final String blockGasPrice = "1";
        final String initialBalanceWei = "100000000000000000000";
        final String command = String.format("ganache-cli -l %s -v -g %s --keepAliveTimeout 15000 --account=\"%s,%s\"", blockGasLimit, blockGasPrice, this.privateKey, initialBalanceWei);
        // ganache-cli -l 471238800 -g 1 -v --keepAliveTimeout 15000 --account="0xb1211de9fb36bc81c9e269e1f6b783d80e01f3709562bb78451d1a956b3c2038,100000000000000000000"
        
        if (OS.contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }
        this.process = processBuilder.start();
        Thread.sleep(10000);
    }

    /**
     * shuts down the Ganache Instance
     */
    public boolean stopGanache() throws InterruptedException {
        this.process.destroyForcibly().waitFor();
        return process.isAlive();
    }
}
