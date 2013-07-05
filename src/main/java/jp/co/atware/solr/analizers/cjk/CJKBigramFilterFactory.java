/*
 * Copyright 2013 atWare, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.atware.solr.analizers.cjk;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CJKBigramFilterFactory extends TokenFilterFactory {
    boolean outputUnigrams;

    @Override
    public void init(Map<String, String> args) {
        super.init(args);
        outputUnigrams = getBoolean("outputUnigrams", false);
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new CJKBigramFilter(input, outputUnigrams);
    }
}