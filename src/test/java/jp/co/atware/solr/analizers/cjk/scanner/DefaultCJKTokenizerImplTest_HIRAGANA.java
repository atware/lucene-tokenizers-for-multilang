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

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

import jp.co.atware.solr.analizers.cjk.CJKTokenizer;
import jp.co.atware.solr.analizers.cjk.CJKTokenizerFactory;

/**
 * Test class for {@link CJKTokenizerScanner} about Hiragana.
 * @author atware
 */
public class DefaultCJKTokenizerImplTest_HIRAGANA extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory();
        factory.init(new HashMap<String, String>());

    }

    @DataPoints
    public static TestData[] testData = new TestData[] {

            //平仮名の区切り
            testData(
            //Input Stream
                    "あ い　う+え＋おengか0120き漢くけケこ한さЀし\uD800す\uDC00せ",
                    //expected tokens
                    //あ
                    expected(0, 1, NGRAM_TYPE, 1),
                    //い
                    expected(2, 3, NGRAM_TYPE, 1),
                    //う
                    expected(4, 5, NGRAM_TYPE, 1),
                    //+
                    expected(5, 6, WORD_TYPE, 1),
                    //え
                    expected(6, 7, NGRAM_TYPE, 1),
                    //＋
                    expected(7, 8, NGRAM_TYPE, 1),
                    //お
                    expected(8, 9, NGRAM_TYPE, 1),
                    //eng
                    expected(9, 12, WORD_TYPE, 1),
                    //か
                    expected(12, 13, NGRAM_TYPE, 1),
                    //0120
                    expected(13, 17, WORD_TYPE, 1),
                    //き
                    expected(17, 18, NGRAM_TYPE, 1),
                    //漢
                    expected(18, 19, NGRAM_TYPE, 1),
                    //く
                    expected(19, 20, NGRAM_TYPE, 1),
                    //け
                    expected(20, 21, NGRAM_TYPE, 1),
                    //ケ
                    expected(21, 22, NGRAM_TYPE, 1),
                    //こ
                    expected(22, 23, NGRAM_TYPE, 1),
                    //한
                    expected(23, 24, NGRAM_TYPE, 1),
                    //さ
                    expected(24, 25, NGRAM_TYPE, 1),
                    //Ѐ
                    expected(25, 26, NGRAM_TYPE, 1),
                    //し
                    expected(26, 27, NGRAM_TYPE, 1),
                    //す
                    expected(28, 29, NGRAM_TYPE, 1),
                    //せ
                    expected(30, 31, NGRAM_TYPE, 1)),

            //Hiragana letters, Small letters \u3041 - \u3096 
            testData(
            //Input Stream
                    "ぁあぃいをんゔゕゖ",
                    //expected tokens
                    //\u4E00
                    expected(0, 1, NGRAM_TYPE, 1),
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
                    expected(8, 9, NGRAM_TYPE, 1)),

            //Voicing marks, Iteration marks, Hiragana digraph
            testData(
            //Input Stream
                    "\u3099\u309A\u309B\u309Cゝゞゟ",
                    //expected tokens
                    //\u3099
                    expected(0, 1, NGRAM_TYPE, 1),
                    //\u309A
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\u309B
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\u309C
                    expected(3, 4, NGRAM_TYPE, 1),
                    //\u309D
                    expected(4, 5, NGRAM_TYPE, 1),
                    //\u309E
                    expected(5, 6, NGRAM_TYPE, 1),
                    //\u309F
                    expected(6, 7, NGRAM_TYPE, 1)),

            //Historic Hiragana
            testData(
            //Input Stream
                    "𛀁",
                    //expected tokens
                    //\u1B001
                    expected(0, 2, NGRAM_TYPE, 1)) };

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