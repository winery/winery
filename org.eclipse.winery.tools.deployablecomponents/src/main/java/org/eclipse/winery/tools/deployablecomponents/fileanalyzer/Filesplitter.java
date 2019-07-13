/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.tools.deployablecomponents.fileanalyzer;

import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Filesplitter {

    // splits a dockerfile into logical lines
    public List<String> splitDockerfile(Dockerfile dockerfile) {
        Scanner fileScanner = new Scanner(dockerfile.getContent());

        List<String> logicalLines = new ArrayList<>();
        StringBuilder nextLine = new StringBuilder();
        if (fileScanner.hasNextLine()) {
            nextLine.append(fileScanner.nextLine());
        }
        while (fileScanner.hasNextLine()) {
            // backslash at end of line means logical line continues in next line
            while (fileScanner.hasNextLine() && nextLine.length() > 0 && nextLine.charAt(nextLine.length() - 1) == '\\') {
                nextLine.deleteCharAt(nextLine.length() - 1);
                nextLine.append(" " + fileScanner.nextLine());
            }
            logicalLines.add(nextLine.toString());

            if (!fileScanner.hasNextLine()) {
                continue;
            }
            // read line for next iteration
            do {
                nextLine = new StringBuilder(fileScanner.nextLine());
                // skip empty lines and comment lines
            } while (fileScanner.hasNextLine() && (nextLine.length() == 1 || nextLine.length() == 0 || nextLine.charAt(0) == '#'));
        }
        // add last line only if not already added (can be the case if last logical line has consists of several physical lines)
        if (!logicalLines.contains(nextLine.toString())) {
            logicalLines.add(nextLine.toString());
        }

        return logicalLines;
    }
}
