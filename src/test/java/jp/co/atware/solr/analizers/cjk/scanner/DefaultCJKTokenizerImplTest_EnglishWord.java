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
 * Test class for {@link CJKTokenizerScanner} about English word.
 * @author atware
 */
public class DefaultCJKTokenizerImplTest_EnglishWord extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory();
        factory.init(new HashMap<String, String>());
    }

    @DataPoints
    public static TestData[] testData = new TestData[] {
            //Half width English word, number, _, #, +
            testData(
                    //Input Stream
                    "_abcdefg hijklmn#opqrstuvwxyz+ abc0123456789def hij-klm ABCXYZ",
                    //expected tokens
                    expected(0, 8, WORD_TYPE, 1),
                    expected(9, 30, WORD_TYPE, 1),
                    expected(31, 47, WORD_TYPE, 1),
                    expected(48, 55, WORD_TYPE, 1),
                    expected(56, 62, WORD_TYPE, 1)),

            //Full width English word, number, _, #, +
            testData(
                    //Input Stream
                    "＿ａｂｃｄｅｆｇ　ｈｉｊｋｌｍｎ＃ｏｐｑｒｓｔｕｖｗｘｙｚ＋　ａｂｃ０１２３４５６７８９ｄｅｆ　ｈｉｊ－ｋｌｍ　ＡＢＣＸＹＺ",
                    //expected tokens
                    expected(0, 1, NGRAM_TYPE, 1),
                    expected(1, 8, WORD_TYPE, 1),
                    expected(9, 16, WORD_TYPE, 1),
                    expected(16, 17, NGRAM_TYPE, 1),
                    expected(17, 29, WORD_TYPE, 1),
                    expected(29, 30, NGRAM_TYPE, 1),
                    expected(31, 47, WORD_TYPE, 1),
                    expected(48, 51, WORD_TYPE, 1),
                    expected(51, 52, NGRAM_TYPE, 1),
                    expected(52, 55, WORD_TYPE, 1),
                    expected(56, 62, WORD_TYPE, 1)),

            //上位サロゲートの範囲チェック
            testData(
                    //Input Stream
                    "\uD7FF\uDC00\uD800\uDC00\uDBFF\uDC00\uDC00\uDC00",
                    //expected tokens
                    expected(2, 4, NGRAM_TYPE, 1),
                    expected(4, 6, NGRAM_TYPE, 1)),

            //下位サロゲートの範囲チェック
            testData(
                    //Input Stream
                    "\uD800\uDBFF\uD800\uDC00\uD800\uDFFF\uD800\uE000",
                    //expected tokens
                    expected(2, 4, NGRAM_TYPE, 1),
                    expected(4, 6, NGRAM_TYPE, 1)),

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
