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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public final class CranioCaudalFilter extends TokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    private final String[] beginningOfWords;
    private final String[] endOfWords;

    public CranioCaudalFilter(TokenStream input, String[] beginningOfWords,
            String[] endOfWords) {
        super(input);
        this.beginningOfWords = beginningOfWords;
        this.endOfWords = endOfWords;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        if (!keywordAttr.isKeyword()) {
            stem(beginningOfWords, true);
            stem(endOfWords, false);
        }
        return true;
    }

    private void stem(String[] targetWords, boolean isBeginning) {
        char termBuffer[] = termAtt.buffer();
        int length = termAtt.length();
        for (String word : targetWords) {
            if (length > word.length()
                    && contains(termBuffer, word, isBeginning ? 0 : length
                            - word.length())) {
                int offset = isBeginning ? word.length() : 0;
                length = length - word.length();
                termAtt.copyBuffer(termBuffer, offset, length);
                break;
            }
        }
    }

    private boolean contains(char[] termBuffer, String word, int start) {
        for (int i = 0; i < word.length(); i++) {
            if (termBuffer[i + start] != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
