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
package org.eclipse.winery.repository.export;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Debugs the execution time of one or more (possibly recurring/nested) blocks of code. Usage example:
 * <code>
 *     ExecutionTimeDebugger debugger = new ExecutionTimeDebugger();
 *      
 *      debugger.startPhase("for loop")
 *      for(...) {
 *          statement1;
 *          
 *          debugger.startPhase("s2");
 *          statement2;
 *          debugger.endPhase("s2", "for loop");
 *          
 *          statement3;
 *      }
 *     debugger.endPhase("for loop");
 *     
 *     Duration forLoopExecutionTime = debugger.getTotalDuration("for loop");
 *     Duration statement2TotalExecutionTime = debugger.getTotalDuration("s2");
 *     
 * </code>
 * */
public class ExecutionTimeDebugger {
    private Map<String, AccumulatedMethodInvocationTime> durations;
    private Map<String, LocalDateTime> phaseStartTimes;

    /**
     * Creates a new instance of the class
     */
    public ExecutionTimeDebugger() {
        this.durations = new HashMap<>();
        this.phaseStartTimes = new HashMap<>();
    }

    /**
     * Starts calculating the execution time of one of the (possibly recurring) executions of some block of code
     * @param key the identifier of the corresponding code block
     */
    public void startPhase(String key) {
        this.phaseStartTimes.put(key, LocalDateTime.now());
    }

    /**
     * Finishes calculating the execution time of one of the (possibly recurring) executions of some block of code, 
     * and accumulates it to the overall execution time of this code block. It also associates the code block with a higher
     * level code block which enhances printing of a tree-like final result.
     * @param key the identifier of the corresponding code block
     * @param parent the identifier of the parent code block
     * @return the total duration accumulated so far.
     */
    public Duration endPhase(String key, String parent) {
        if (this.phaseStartTimes.containsKey(key)) {
            LocalDateTime start = this.phaseStartTimes.get(key);
            return accumulateDuration(key, parent, Duration.between(LocalDateTime.now(), start));
        } else {
            throw new IllegalStateException("No startPhase has been called for the given key: " + key);
        }
    }

    /**
     * Finishes calculating the execution time of one of the (possibly recurring) executions of some block of code, 
     * and accumulates it to the overall execution time of this code block. (this override considers this code block to 
     * be at the topmost level of the hierarchy).
     * @param key the identifier of the corresponding code block
     * @return the total duration accumulated so far.
     */
    public Duration endPhase(String key) {
        return  this.endPhase(key, "");
    }

    /**
     * Gets the overall execution time accumulated so far for the corresponding code block
     * @param key the identifier of the code block
     * @return the overall execution time accumulated so far for the corresponding code block
     */
    public Duration getTotalDuration(String key) {
        return this.durations.get(key).duration;
    }

    /**
     * Generates a hierarchical representation of all monitored code blocks and their overall execution times.  
     * @return a string with a hierarchical representation of all monitored code blocks and their overall execution times.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        List<AccumulatedMethodInvocationTime> roots =
            this.durations.
                values().
                stream().
                filter(entry ->
                    entry.parent == null || entry.parent.isEmpty())
                .collect(Collectors.toList());
        traverse(builder, roots, 0);

        return builder.toString();
    }

    private Duration accumulateDuration(String name, String parent, Duration toAccumulate) {
        Duration newDuration;
        AccumulatedMethodInvocationTime methodInvocationTime;

        if (this.durations.containsKey(name)) {
            methodInvocationTime = this.durations.get(name);
            methodInvocationTime.parent = parent;
            newDuration = methodInvocationTime.accumulateDuration(toAccumulate);
        } else {
            methodInvocationTime = new AccumulatedMethodInvocationTime(name, parent, toAccumulate);
            this.durations.put(name, methodInvocationTime);
            newDuration = toAccumulate;
        }

        return newDuration;
    }

    /**
     * Recursive depth-first traversal of the "tree"
     * @param builder string builder to accumulate the result
     * @param children the direct children at the current position
     * @param level the depth at the current position
     */
    private void traverse(StringBuilder builder, List<AccumulatedMethodInvocationTime> children, int level) {
        if (children.size() > 0) {
            
            StringBuilder paddingB = new StringBuilder();
            for (int i = 0; i <= level; i++) {
                paddingB.append('*');
            }
            String padding = paddingB.toString();

            for (AccumulatedMethodInvocationTime entry : children) {
                builder.append(String.format("%s Processing %s lasted (in total): %s\n", 
                    padding, entry.name, entry.duration ));
                traverse(builder, this.getChildren(entry.name), level + 1);
            }
        }
    }

    private List<AccumulatedMethodInvocationTime> getChildren(String parent) {
        return this
            .durations
            .values()
            .stream()
            .filter(entry ->
                entry.parent.equalsIgnoreCase(parent))
            .collect(Collectors.toList());
    }

    private class AccumulatedMethodInvocationTime {
        private String name;
        private String parent;
        private Duration duration;

        private AccumulatedMethodInvocationTime(String name, String parent, Duration duration) {
            this.name = name;
            this.parent = parent;
            this.duration = duration;
        }

        private Duration accumulateDuration(Duration duration) {
            this.duration = this.duration.plus(duration);
            return this.duration;
        }
    }
}
