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

/**
 * Test class for {@link CJKTokenizerScanner} about Hangul.
 * @author atware
 */
public class DefaultCJKTokenizerImplTest_HANGUL extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory(new HashMap<String, String>());

    }

    @DataPoints
    public static TestData[] testData = new TestData[] {
            //ハングル文字の区切り
            testData(
            //Input Stream
                    "ᄁ ᄂ　ᄃ+ᄄ＋ᄅengᄇ0120ᄈ漢ᄉあᄊイᄋᄌЀᄍ\uD800ᄎ\uDC00ᄏ",
                    //expected tokens
                    //ᄁ
                    expected(0, 1, NGRAM_TYPE, 1),
                    //ᄂ
                    expected(2, 3, NGRAM_TYPE, 1),
                    //ᄃ
                    expected(4, 5, NGRAM_TYPE, 1),
                    //+
                    expected(5, 6, WORD_TYPE, 1),
                    //ᄄ
                    expected(6, 7, NGRAM_TYPE, 1),
                    //＋
                    expected(7, 8, NGRAM_TYPE, 1),
                    //ᄅᄆ
                    expected(8, 9, NGRAM_TYPE, 1),
                    //eng
                    expected(9, 12, WORD_TYPE, 1),
                    //ᄇ
                    expected(12, 13, NGRAM_TYPE, 1),
                    //0120
                    expected(13, 17, WORD_TYPE, 1),
                    //ᄈ
                    expected(17, 18, NGRAM_TYPE, 1),
                    //漢
                    expected(18, 19, NGRAM_TYPE, 1),
                    //ᄉ
                    expected(19, 20, NGRAM_TYPE, 1),
                    //あ
                    expected(20, 21, NGRAM_TYPE, 1),
                    //ᄊ
                    expected(21, 22, NGRAM_TYPE, 1),
                    //イ
                    expected(22, 23, NGRAM_TYPE, 1),
                    //ᄋ
                    expected(23, 24, NGRAM_TYPE, 1),
                    //ᄌ
                    expected(24, 25, NGRAM_TYPE, 1),
                    //Ѐ
                    expected(25, 26, NGRAM_TYPE, 1),
                    //ᄍ
                    expected(26, 27, NGRAM_TYPE, 1),
                    //ᄎ
                    expected(28, 29, NGRAM_TYPE, 1),
                    //ᄏ
                    expected(30, 31, NGRAM_TYPE, 1)),

            //ハングル字母
            testData(
            //Input Stream
                    "\u1100\u1101\u115F\u1160\u11A7\u11A8\u11FE\u11FF",
                    //expected tokens
                    //\u1100
                    expected(0, 1, NGRAM_TYPE, 1),
                    //\u1101
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\u115F
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\u1160
                    expected(3, 4, NGRAM_TYPE, 1),
                    //\u11A7
                    expected(4, 5, NGRAM_TYPE, 1),
                    //\u11A8
                    expected(5, 6, NGRAM_TYPE, 1),
                    //u11FE
                    expected(6, 7, NGRAM_TYPE, 1),
                    //\u11FF
                    expected(7, 8, NGRAM_TYPE, 1)),

            //ハングル互換字母  \u3131 - \u318E
            testData(
            //Input Stream
                    "\u3130\u3131\u3163\u3164\u3165\u318E\u318F",
                    //expected tokens
                    //\u3131
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\u3163
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\u3164
                    expected(3, 4, NGRAM_TYPE, 1),
                    //\u3165
                    expected(4, 5, NGRAM_TYPE, 1),
                    //\u318E
                    expected(5, 6, NGRAM_TYPE, 1)),

            //ハングル字母拡張A
            testData(
            //Input Stream
                    "\uA960\uA961\uA97C\uA97D",
                    //expected tokens
                    //\uA960
                    expected(0, 1, NGRAM_TYPE, 1),
                    //\uA961
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\uA97C
                    expected(2, 3, NGRAM_TYPE, 1)),

            //ハングル音節文字 \uAC00 - \uD7A3
            testData(
            //Input Stream
                    "\uABFF\uAC00\uD7A3\uD7A4",
                    //expected tokens
                    //\uAC00
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\uD7A3
                    expected(2, 3, NGRAM_TYPE, 1)),

            //ハングル字母拡張B \uD7B0 - \uD7C6, \uD7CB - \uD7FB
            testData(
            //Input Stream
                    "\uD7AF\uD7B0\uD7C6\uD7C7\uD7CA\uD7CB\uD7FB\uD7FC",
                    //expected tokens
                    //\uD7B0
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\uD7C6
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\uD7CB
                    expected(5, 6, NGRAM_TYPE, 1),
                    //\uD7FB
                    expected(6, 7, NGRAM_TYPE, 1)),

            //半角ハングル \uFFA0 - \uFFBE, \uFFC2 - \uFFC7, \uFFCA - \uFFCF, \uFFD2 - \uFFDC
            testData(
            //Input Stream
                    "\uFFA0\uFFA1\uFFBE\uFFBF\uFFC1\uFFC2\uFFC7\uFFC8\uFFC9\uFFCA\uFFCF\uFFD0\uFFD1\uFFD2\uFFDC\uFFDD",
                    //expected tokens
                    //\uFFA0
                    expected(0, 1, NGRAM_TYPE, 1),
                    //\uFFA1
                    expected(1, 2, NGRAM_TYPE, 1),
                    //\uFFBE
                    expected(2, 3, NGRAM_TYPE, 1),
                    //\uFFC2
                    expected(5, 6, NGRAM_TYPE, 1),
                    //\uFFC7
                    expected(6, 7, NGRAM_TYPE, 1),
                    //\uFFCA
                    expected(9, 10, NGRAM_TYPE, 1),
                    //\uFFCF
                    expected(10, 11, NGRAM_TYPE, 1),
                    //\uFFD2
                    expected(13, 14, NGRAM_TYPE, 1),
                    //\uFFDC
                    expected(14, 15, NGRAM_TYPE, 1)

            ), };

    @Theory
    public void testIncrementToken(TestData testData) throws Exception {
        CJKTokenizer testTarget = createTestTarget(testData.input);
        super.testIncrementToken(testData, testTarget);
    }

    protected CJKTokenizer createTestTarget(String string) {
        StringReader reader = new StringReader(string);
        CJKTokenizer tokenizer = (CJKTokenizer) factory.create(reader);
        return tokenizer;
    }
}
