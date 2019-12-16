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

package org.eclipse.winery.crawler.chefcookbooks.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class RubyCodeHelperTest {

    @ParameterizedTest(name = "{index} => ''{2}''")
    @MethodSource("getRemoveNewlinesArguments")
    public void removeUnnecessaryNewlines(String inputString, String expectedString) throws IOException {
        String processedString;
        Reader inputStringReader = new StringReader(inputString);
        BufferedReader reader = new BufferedReader(inputStringReader);
        processedString = RubyCodeHelper.removeUnnecessaryCode(reader);
        reader.close();
        assertEquals(expectedString, processedString);
    }

    private static Stream<Arguments> getRemoveNewlinesArguments() {
        return Stream.of(
            Arguments.of("default['java']['jdk_version'] = \n '6'", "default['java']['jdk_version'] =   '6'\n"),
            Arguments.of("default['java']['jdk_version'] = 'he' +\n'llo'", "default['java']['jdk_version'] = 'he' + 'llo'\n"),
            Arguments.of("default['java']['jdk_version'] = 'he' +\n 'llo'", "default['java']['jdk_version'] = 'he' +  'llo'\n"),
            Arguments.of("# This is a comment. \ndefault['java']['jdk_version'] = '6'\n", "default['java']['jdk_version'] = '6'\n"),
            Arguments.of("if s3_bucket && s3_remote_path\n" +
                "\n" +
                "  aws_s3_file 'cache_file_path' do\n" +
                "    aws_session_token aws_session_token\n" +
                "\n" +
                "    checksum pkg_checksum if pkg_checksum\n" +
                "  end\n" +
                "  \n" +
                "else\n" +
                "\n" +
                "  ruby_block 'Enable Accessing cookies' do\n" +
                "    block do\n" +
                "      cookie_jar = Chef::HTTP::CookieJar\n" +
                "      cookie_jar.instance[\"#{uri.host}:#{uri.port}\"] = 'oraclelicense=accept-securebackup-cookie'\n" +
                "    end\n" +
                "\t\n" +
                "    only_if { node['java']['oracle']['accept_oracle_download_terms'] }\n" +
                "  end\n" +
                "  remote_file cache_file_path do\n" +
                "    checksum pkg_checksum if pkg_checksum\n" +
                "\t\n" +
                "    source node['java']['windows']['url']\n" +
                "\n" +
                "\t\n" +
                "\t\n" +
                "    backup false\n" +
                "\t\n" +
                "    action :create\n" +
                "  end\n" +
                "end\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "package 'test' do\n" +
                "\taction :install\n" +
                "end", "if s3_bucket && s3_remote_path\n" +
                "  aws_s3_file 'cache_file_path' do\n" +
                "    aws_session_token aws_session_token\n" +
                "    checksum pkg_checksum if pkg_checksum\n" +
                "  end\n" +
                "else\n" +
                "  ruby_block 'Enable Accessing cookies' do\n" +
                "    block do\n" +
                "      cookie_jar = Chef::HTTP::CookieJar\n" +
                "      cookie_jar.instance[\"#{uri.host}:#{uri.port}\"] = 'oraclelicense=accept-securebackup-cookie'\n" +
                "    end\n" +
                "    only_if { node['java']['oracle']['accept_oracle_download_terms'] }\n" +
                "  end\n" +
                "  remote_file cache_file_path do\n" +
                "    checksum pkg_checksum if pkg_checksum\n" +
                "    source node['java']['windows']['url']\n" +
                "    backup false\n" +
                "    action :create\n" +
                "  end\n" +
                "end\n" +
                "\n" +
                "package 'test' do\n" +
                "\taction :install\n" +
                "end\n")

        );
    }
}
