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

import java.io.StringReader;
import java.util.HashMap;

import jp.co.atware.solr.analizers.cjk.CJKTokenizer;
import jp.co.atware.solr.analizers.cjk.CJKTokenizerFactory;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

public class DefaultCJKTokenizerImplTest_KATAKANA extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory();
        factory.init(new HashMap<String, String>());

    }

    @DataPoints
    public static TestData[] testData = new TestData[] {
            //Katakana Delimiter
            testData(
            //Input Stream
                    "ア イ　ウ+エ＋オengカ0120キ漢クケけコ한サЀシ\uD800ス\uDC00セ",
                    //expected tokens
                    //ア
                    expected(0, 1, NGRAM_TYPE, 1),
                    //イ
                    expected(2, 3, NGRAM_TYPE, 1),
                    //ウ
                    expected(4, 5, NGRAM_TYPE, 1),
                    //+
                    expected(5, 6, WORD_TYPE, 1),
                    //エ
                    expected(6, 7, NGRAM_TYPE, 1),
                    //＋
                    expected(7, 8, NGRAM_TYPE, 1),
                    //オ
                    expected(8, 9, NGRAM_TYPE, 1),
                    //eng
                    expected(9, 12, WORD_TYPE, 1),
                    //カ
                    expected(12, 13, NGRAM_TYPE, 1),
                    //0120
                    expected(13, 17, WORD_TYPE, 1),
                    //キ
                    expected(17, 18, NGRAM_TYPE, 1),
                    //漢
                    expected(18, 19, NGRAM_TYPE, 1),
                    //ク
                    expected(19, 20, NGRAM_TYPE, 1),
                    //ケ
                    expected(20, 21, NGRAM_TYPE, 1),
                    //ケ
                    expected(21, 22, NGRAM_TYPE, 1),
                    //コ
                    expected(22, 23, NGRAM_TYPE, 1),
                    //한
                    expected(23, 24, NGRAM_TYPE, 1),
                    //サ
                    expected(24, 25, NGRAM_TYPE, 1),
                    //Ѐ
                    expected(25, 26, NGRAM_TYPE, 1),
                    //シ
                    expected(26, 27, NGRAM_TYPE, 1),
                    //ス
                    expected(28, 29, NGRAM_TYPE, 1),
                    //セ
                    expected(30, 31, NGRAM_TYPE, 1)),

            //Katakana \u30A1 - \u30FF 
            testData(
            //Input Stream
                    "\u30A0ァアヹヺ・ーヽヾヿ",
                    //expected tokens
                    //\u4E01
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(3, 4, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(4, 5, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(5, 6, NGRAM_TYPE, 1),
                    //\u9FCB
                    expected(6, 7, NGRAM_TYPE, 1),
                    //\u9FCC
                    expected(7, 8, NGRAM_TYPE, 1),
                    //\u9FCC
                    expected(8, 9, NGRAM_TYPE, 1),
                    //\u9FCC
                    expected(9, 10, NGRAM_TYPE, 1)),

            //Halfwidth Katakana variants \uFF65 - \uFF9F
            testData(
            //Input Stream
                    "･ｦｧｻﾜﾝﾞﾟ",
                    //expected tokens
                    //･
                    expected(0, 1, NGRAM_TYPE, 1),
                    //ｦ
                    expected(1, 2, NGRAM_TYPE, 1),
                    //ｧ
                    expected(2, 3, NGRAM_TYPE, 1),
                    //ｻ
                    expected(3, 4, NGRAM_TYPE, 1),
                    //ﾜ
                    expected(4, 5, NGRAM_TYPE, 1),
                    //ﾝ
                    expected(5, 6, NGRAM_TYPE, 1),
                    //ﾞ
                    expected(6, 7, NGRAM_TYPE, 1),
                    //ﾟ
                    expected(7, 8, NGRAM_TYPE, 1)),

    };

    @Theory
    public void testIncrementToken(TestData testData) throws Exception {
        CJKTokenizer testTarget = createTestTarget(testData.input);
        super.testIncrementToken(testData, testTarget);
    }

    protected CJKTokenizer createTestTarget(String string) {
        StringReader reader = new StringReader(string);
        CJKTokenizer tokenizer = factory.create(reader);
        return tokenizer;
    }
}
