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

import static jp.co.atware.solr.analizers.cjk.dfa.DFAInitializer.*;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import jp.co.atware.solr.analizers.cjk.dfa.DFASettingManager;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

public class CJKTokenizerFactory extends TokenizerFactory {

    private static final String CUSTOMIZE_DFA_KEY = "custom";
    private static final String ACTION_FILE_PATH_KEY = "action";
    private static final String CMAP_FILE_PATH_KEY = "cmap";
    private static final String TRANS_FILE_PATH_KEY = "trans";

    private static final char[] DEFAULT_CMAP;
    private static final int[] DEFAULT_ACTION;
    private static final int[][] DEFAULT_TRANS;

    static {
        DEFAULT_CMAP = readCMap(getInputStream("dfa/symbol/zz_cmap"));
        DEFAULT_ACTION = readAction(getInputStream("dfa/symbol/zz_action"));
        DEFAULT_TRANS = readTrans(getInputStream("dfa/symbol/zz_trans"));
    }

    private static InputStream getInputStream(String resourceName) {
        return CJKTokenizerFactory.class.getClassLoader().getResourceAsStream(
                resourceName);
    }

    private char[] cmap = DEFAULT_CMAP;
    private int[] action = DEFAULT_ACTION;
    private int[][] trans = DEFAULT_TRANS;

    private int maxTokenLength;
    private final DFASettingManager dfaManager = DFASettingManager
            .getInstance();

    public CJKTokenizerFactory(Map<String, String> args) {
        super(args);
        maxTokenLength = getInt(args, "maxTokenLength",
                StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH);
        if (args.containsKey(CUSTOMIZE_DFA_KEY)) {
            if (!args.containsKey(CMAP_FILE_PATH_KEY)
                    || !args.containsKey(ACTION_FILE_PATH_KEY)
                    || !args.containsKey(TRANS_FILE_PATH_KEY)) {
                throw new IllegalArgumentException(
                        "Configuration Error: dfa setting file path");
            }
            cmap = dfaManager.getCMap(args.get(CMAP_FILE_PATH_KEY));
            action = dfaManager.getAction(args.get(ACTION_FILE_PATH_KEY));
            trans = dfaManager.getTrans(args.get(TRANS_FILE_PATH_KEY));
        }
    }

    public CJKTokenizer create(AttributeFactory factory, Reader input) {
        CJKTokenizer tokenizer = null;
        tokenizer = new CJKTokenizer(input, cmap, action, trans);
        tokenizer.setMaxTokenLength(maxTokenLength);
        return tokenizer;
    }
}
