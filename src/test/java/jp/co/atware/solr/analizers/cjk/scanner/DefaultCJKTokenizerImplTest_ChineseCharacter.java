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
 * Test class for {@link CJKTokenizerScanner} about Chinese Character
 * @author atware
 */
public class DefaultCJKTokenizerImplTest_ChineseCharacter extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory();
        factory.init(new HashMap<String, String>());

    }

    @DataPoints
    public static TestData[] testData = new TestData[] {
            //漢字の区切りチェック
            testData(
                    //Input Stream
                    "漢 字　統+合＋拡eng張0120単体あ試ケ験한一Ѐ二\uD800三\uDC00四",
                    //expected tokens
                    expected(0, 1, NGRAM_TYPE, 1),
                    expected(2, 3, NGRAM_TYPE, 1),
                    expected(4, 5, NGRAM_TYPE, 1),
                    expected(5, 6, WORD_TYPE, 1),
                    expected(6, 7, NGRAM_TYPE, 1),
                    expected(7, 8, NGRAM_TYPE, 1),
                    expected(8, 9, NGRAM_TYPE, 1),
                    expected(9, 12, WORD_TYPE, 1),
                    expected(12, 13, NGRAM_TYPE, 1),
                    expected(13, 17, WORD_TYPE, 1),
                    expected(17, 18, NGRAM_TYPE, 1),
                    expected(18, 19, NGRAM_TYPE, 1),
                    expected(19, 20, NGRAM_TYPE, 1),
                    expected(20, 21, NGRAM_TYPE, 1),
                    expected(21, 22, NGRAM_TYPE, 1),
                    expected(22, 23, NGRAM_TYPE, 1),
                    expected(23, 24, NGRAM_TYPE, 1),
                    expected(24, 25, NGRAM_TYPE, 1),
                    expected(25, 26, NGRAM_TYPE, 1),
                    expected(26, 27, NGRAM_TYPE, 1),
                    expected(28, 29, NGRAM_TYPE, 1),
                    expected(30, 31, NGRAM_TYPE, 1)),

            //CJK統合漢字 \u4E00 - \u9FCC
            testData(
                    //Input Stream
                    "\u4DFF\u4E00\u4E01\u9FCB\u9FCC\u9FCD",
                    //expected tokens
                    expected(1, 2, NGRAM_TYPE, 1),
                    expected(2, 3, NGRAM_TYPE, 1),
                    expected(3, 4, NGRAM_TYPE, 1),
                    expected(4, 5, NGRAM_TYPE, 1)),

            //CJK統合漢字拡張A \u3400 - \u4DB5
            testData(
                    //Input Stream
                    "\u33FF\u3400\u3401\u4DB4\u4DB5\u4DB6",
                    //expected tokens
                    expected(1, 2, NGRAM_TYPE, 1),
                    expected(2, 3, NGRAM_TYPE, 1),
                    expected(3, 4, NGRAM_TYPE, 1),
                    expected(4, 5, NGRAM_TYPE, 1)),

            //CJK互換漢字 \uF900 - \uFAD9
            testData(
                    //Input Stream
                    "\uF8FF\uF900\uF901\uFAD8\uFAD9\uFADA",
                    //expected tokens
                    expected(1, 2, NGRAM_TYPE, 1),
                    expected(2, 3, NGRAM_TYPE, 1),
                    expected(3, 4, NGRAM_TYPE, 1),
                    expected(4, 5, NGRAM_TYPE, 1)),

            //CJK統合漢字拡張B \u20000 - \u2A6DF
            testData(
            //Input Stream
                    "𠀀𠀁　　𪛕𪛖",
                    //expected toke5ns
                    //\u20000
                    expected(0, 2, NGRAM_TYPE, 1),
                    //\u20001
                    expected(2, 4, NGRAM_TYPE, 1),
                    //\u2A6DE
                    expected(6, 8, NGRAM_TYPE, 1),
                    //\u2A6DF
                    expected(8, 10, NGRAM_TYPE, 1)),

            //CJK統合漢字拡張C \u2A700 - \u2B73F
            testData(
            //Input Stream
                    "𪜀𪜁 𫜳𫜴",
                    //expected tokens
                    //\u4E00
                    expected(0, 2, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(2, 4, NGRAM_TYPE, 1),
                    //\u9FCB
                    expected(5, 7, NGRAM_TYPE, 1),
                    //\u9FCC
                    expected(7, 9, NGRAM_TYPE, 1)),

            //CJK統合漢字拡張D \u2B740 - \u2B81F
            testData(
            //Input Stream
                    "𫝀𫝁1234english𫠜𫠝",
                    //expected tokens
                    //\u4E00
                    expected(0, 2, NGRAM_TYPE, 1),
                    //\u4E01
                    expected(2, 4, NGRAM_TYPE, 1),
                    //\u9FCB
                    expected(4, 15, WORD_TYPE, 1),
                    //\u9FCC
                    expected(15, 17, NGRAM_TYPE, 1),
                    //\u9FCC
                    expected(17, 19, NGRAM_TYPE, 1)),

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
