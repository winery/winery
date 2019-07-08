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
    private Process process;
    /**
     * Starts Ganache in the Console for Integration Tests
     * @throws Exception
     */
    public void startGanache() throws Exception{
        String OS = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder = new ProcessBuilder();
        if(OS.contains("win")) {
            processBuilder.command("cmd.exe", "/c", "ganache-cli", "-l 0xffffffffffffff", "-v", "--account=\"0xb1211de9fb36bc81c9e269e1f6b783d80e01f3709562bb78451d1a956b3c2038,99999999999999999999999999999999\"");
        }
        
        else{
            /*
            processBuilder.command("bash", "-c", "ganache-cli", "--account=\"0xb1211de9fb36bc81c9e269e1f6b783d80e01f3709562bb78451d1a956b3c2038,99999999999999999999999999999999\"" , "-l 0xffffffffffffff", "-v");
*/

            processBuilder.command("bash", "-c", "ganache-cli--account=\"0xb1211de9fb36bc81c9e269e1f6b783d80e01f3709562bb78451d1a956b3c2038,99999999999999999999999999999999\" -l 0xffffffffffffff -v");
        }
        this.process = processBuilder.start();
        Thread.sleep(10000);
    }

    /**
     * shuts down the Ganache Instance
     */
    public  boolean stopGanache(){
        this.process.destroyForcibly();
        return process.isAlive();
    }
}
