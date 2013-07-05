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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.ResourceLoader;

public class MultistageMappingCharFilterFactory extends
        MappingCharFilterFactory {

    private static final String PTN_STAGE_DELIMITER = "(?<!\\\\);";
    private static final String PTN_REMOVE_ESCAPE_CHAR = "\\\\(?=;)";

    protected final List<NormalizeCharMap> normMapList = new ArrayList<NormalizeCharMap>();

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        String mapping = args.get("mapping");
        if (mapping == null) {
            return;
        }
        for (String fileNames : mapping.split(PTN_STAGE_DELIMITER)) {
            fileNames = fileNames.replaceAll(PTN_REMOVE_ESCAPE_CHAR, "");
            args.put("mapping", fileNames);
            super.inform(loader);
            if (normMap != null) {
                normMapList.add(normMap);
            }
        }
    }

    @Override
    public Reader create(Reader input) {
        for (NormalizeCharMap charMap : normMapList) {
            input = charMap == null ? input : new MappingCharFilter(charMap,
                    input);
        }
        return input;
    }
}
