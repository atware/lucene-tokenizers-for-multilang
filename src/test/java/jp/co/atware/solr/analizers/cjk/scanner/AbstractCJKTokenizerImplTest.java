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

package jp.co.atware.solr.analizers.cjk.scanner;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;

import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Before;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import jp.co.atware.solr.analizers.cjk.CJKTokenizer;
import jp.co.atware.solr.analizers.cjk.CJKTokenizerFactory;

@RunWith(Theories.class)
public abstract class AbstractCJKTokenizerImplTest {

    protected static final String WORD_TYPE = CJKTokenizer.TOKEN_TYPES[CJKTokenizer.WORD];
    protected static final String NGRAM_TYPE = CJKTokenizer.TOKEN_TYPES[CJKTokenizer.NGRAM];

    protected CJKTokenizerFactory factory;

    /**
     * {@link CJKTokenizerFactory}のinstance生成処理及び、初期化処理を記述
     */
    @Before
    public abstract void setup();

    /**
     * {@link TestData#input}が期待した位置で、期待したトークンタイプとして区切られる事を確認する.
     * @param testData
     * @param testTarget
     * @throws IOException
     */
    protected void testIncrementToken(TestData testData, CJKTokenizer testTarget)
            throws IOException {
        int actLength = 0;
        while (testTarget.incrementToken()) {
            TokenAttr expected = testData.expected[actLength++];
            OffsetAttribute actOffset = testTarget
                    .getAttribute(OffsetAttribute.class);
            TypeAttribute actType = testTarget
                    .getAttribute(TypeAttribute.class);
            PositionIncrementAttribute actPos = testTarget
                    .getAttribute(PositionIncrementAttribute.class);

            assertThat(actOffset.startOffset(), is(expected.startOffset));
            assertThat(actOffset.endOffset(), is(expected.endOffset));
            assertThat(actType.type(), is(expected.type));
            assertThat(actPos.getPositionIncrement(), is(expected.position));
        }
        assertThat(actLength, is(testData.expected.length));
    }

    protected static class TestData {
        protected TestData(String input, TokenAttr[] expected) {
            this.input = input;
            this.expected = expected;
        }

        protected final String input;
        protected final TokenAttr[] expected;
    }

    /**
     * 切り出されるトークン情報の期待値を保持するクラスです.
     * @author atware
     */
    protected static class TokenAttr {

        /**
         * トークン情報を保持するクラスのコンストラクタ.
         * @param startOffset トークンの開始位置
         * @param endOffset トークンの終了位置 + 1
         * @param type トークンタイプ
         * @param position
         */
        protected TokenAttr(int startOffset, int endOffset, String type,
                int position) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.type = type;
            this.position = position;
        }

        protected final int startOffset;
        protected final int endOffset;
        protected final String type;
        protected final int position;

    }

    /**
     * 分割されるトークンの期待値を保持するインスタンスを生成します.
     * @param start トークンの開始位置
     * @param end トークンの終了位置 + 1
     * @param type トークンタイプ
     * @param pos
     * @return
     */
    protected static TokenAttr expected(int start, int end, String type, int pos) {
        return new TokenAttr(start, end, type, pos);
    }

    /**
     * テストデータ(インプット、期待値インスタンス)を生成します.
     * @param input 入力ストリーム
     * @param tokenAttrs 期待値保持インスタンス
     * @return
     */
    protected static TestData testData(String input, TokenAttr... tokenAttrs) {
        return new TestData(input, tokenAttrs);
    }
}
