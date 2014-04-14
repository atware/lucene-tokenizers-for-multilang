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

import jp.co.atware.solr.analizers.cjk.attributes.PosticheOffsetAttribute;
import jp.co.atware.solr.analizers.cjk.scanner.CJKTokenizerScanner;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * @author atware
 */
public final class CJKTokenizer extends Tokenizer {

    private CJKTokenizerScanner scanner;

    public static final int NON_CHARACTER = 0;
    public static final int WORD = 1;
    public static final int NGRAM = 2;

    /** String token types that correspond to token type int constants */
    public static final String[] TOKEN_TYPES = new String[] {
            "<NON_CHARACTER>", "<WORD>", "<SINGLE>" };

    private int maxTokenLength = StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * Set the max allowed token length. Any token longer than this is skipped.
     */
    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    /** @see #setMaxTokenLength */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    public CJKTokenizer(Reader input, char[] cmap, int[] action, int[][] trans) {
        super(input);
        this.scanner = new CJKTokenizerScanner(input, cmap, action, trans);
    }

    // this tokenizer generates three attributes:
    // term offset, positionIncrement and type
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final PosticheOffsetAttribute posticheOffsetAtt = addAttribute(PosticheOffsetAttribute.class);

    /*
     * (non-Javadoc)
     * @see org.apache.lucene.analysis.TokenStream#next()
     */
    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        int posIncr = 1;

        while (true) {
            int tokenType = scanner.getNextToken();

            if (tokenType == CJKTokenizerScanner.YYEOF) {
                return false;
            }

            if (scanner.yylength() <= maxTokenLength) {
                posIncrAtt.setPositionIncrement(posIncr);
                scanner.getText(termAtt);
                final int start = scanner.yychar();
                //入力文字列に対するオフセット
                offsetAtt.setOffset(correctOffset(start), correctOffset(start
                        + termAtt.length()));
                //CharFilter後の文字列に対するオフセット
                posticheOffsetAtt.setOffset(start, start + termAtt.length());
                typeAtt.setType(CJKTokenizer.TOKEN_TYPES[tokenType]);
                return true;
            } else
                // When we skip a too-long term, we still increment the
                // position increment
                posIncr++;
        }
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        int finalOffset = correctOffset(scanner.yychar() + scanner.yylength());
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        scanner.yyreset(input);
    }
}
