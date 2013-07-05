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

public class DefaultCJKTokenizerImplTest_SurrogatePair extends
        AbstractCJKTokenizerImplTest {

    @Override
    public void setup() {
        factory = new CJKTokenizerFactory();
        factory.init(new HashMap<String, String>());

    }

    @DataPoints
    public static TestData[] testData = new TestData[] {

            //上位サロゲートの範囲チェック
            testData(
                    //Input
                    "\uD7FF\uDC00\uD800\uDC00\uDBFF\uDC00\uDC00\uDC00",
                    //Expected
                    expected(2, 4, NGRAM_TYPE, 1),
                    expected(4, 6, NGRAM_TYPE, 1)),

            //下位サロゲートの範囲チェック
            testData(
                    //Input
                    "\uD800\uDBFF\uD800\uDC00\uD800\uDFFF\uD800\uE000",
                    //Expected
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
