/******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 ******************************************************************************/

package org.eclipse.winery.bpel2bpmn.parser;

import org.eclipse.winery.bpel2bpmn.exception.ParseException;
import org.eclipse.winery.bpel2bpmn.model.Scope;
import org.eclipse.winery.bpel2bpmn.model.gen.*;
import org.eclipse.winery.bpel2bpmn.utils.NullChecker;
import org.eclipse.winery.bpel2bpmn.utils.ObjectSearcher;
import org.eclipse.winery.bpel2bpmn.utils.ScopeFilter;
import org.eclipse.winery.bpel2bpmn.utils.TypeMapper;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The Parser for Scope/{@link TScope}
 */
public class ScopeParser {
    /**
     * The Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopeParser.class);
    /**
     * The Pattern of the main sequence
     */
    private static final Pattern PATTERN_MAIN_SEQUENCE = Pattern.compile("^.*_mainSequence$");
    /**
     * The Pattern of the pattern phase
     */
    private static final Pattern PATTERN_PHASE_SEQUENCE = Pattern.compile("^.*_(pre|provisioning|post)Phase$");
    /**
     * The {@link SequenceParser}
     */
    private final SequenceParser sequenceParser = new SequenceParser();

    /**
     * Parses the scope
     *
     * @param tProcess The {@link TProcess}
     * @return A list of {@link Scope}
     * @throws ParseException If something happens during parsing
     */
    public List<Scope> parseScopes(final TProcess tProcess) throws ParseException {
        List<Scope> result = new ArrayList<>();
        TFlow tFlow = this.findFlow(tProcess);

        List<Object> activities = tFlow.getActivity();
        activities.stream()
            .filter(new ScopeFilter())
            .map(new TypeMapper<Object, TScope>())
            .forEach(tScope -> {
                    Scope scope = null;
                    try {
                        LOGGER.info("Parsing scope {}", tScope.getName());
                        scope = extractScope(tScope);
                    } catch (ParseException e) {
                        LOGGER.info(e.getMessage(), e);
                    }
                    result.add(scope);
                }
            );

        return result;
    }

    /**
     * All scopes are inside a <flow></flow> tag
     *
     * @param process The root {@link TProcess}
     * @return The {@link TFlow} element
     * @throws ParseException If something happens during parsing
     */
    protected TFlow findFlow(final TProcess process) throws ParseException {
        TSequence sequence = process.getSequence();
        List<Object> activities = sequence.getActivity();
        return ObjectSearcher.findFirst(activities, TFlow.class)
            .orElseThrow(() -> new ParseException("Could not find <flow>"));
    }

    /**
     * Extracts a {@link Scope} from the {@link TScope}
     *
     * @param tScope The {@link TScope} element
     * @return An extracted {@link Scope}
     * @throws ParseException if something happens during parsing
     */
    protected Scope extractScope(final TScope tScope) throws ParseException {
        Scope scope = new Scope();
        String tScopeName = tScope.getName();
        String scopeName = tScopeName.split("_")[0];
        scope.setScopeName(scopeName);

        NullChecker.check(tScope.getTargets())
            .map(tTargets -> tTargets.getTarget().stream().map(TTarget::getLinkName)
                .collect(Collectors.toList()))
            .ifPresent(scope::setTargets);

        NullChecker.check(tScope.getSources())
            .map(tSources -> tSources.getSource().stream().map(TSource::getLinkName)
                .collect(Collectors.toList()))
            .ifPresent(scope::setSources);

        List<TSequence> subSequences = NullChecker.orThrow(tScope.getSequence(), new ParseException("No Sequence"))
            .filter(tSequence -> PATTERN_MAIN_SEQUENCE.matcher(tSequence.getName()).matches())
            .map(mainSequence -> {
                List<Object> activities = mainSequence.getActivity();
                return ObjectSearcher.findAny(activities, TSequence.class);
            }).orElseThrow(() -> new ParseException("No <sequence> in the main <sequence>"));

        for (TSequence subSequence : subSequences) {
            if (PATTERN_PHASE_SEQUENCE.matcher(subSequence.getName()).matches()) {
                LOGGER.debug("Parsing Sub <sequence> {}", subSequence.getName());
                List<ManagementTask> managementTasks = this.sequenceParser.parseSequence(subSequence);
                scope.getManagementTasks().addAll(managementTasks);
            }
        }
        return scope;
    }
}
