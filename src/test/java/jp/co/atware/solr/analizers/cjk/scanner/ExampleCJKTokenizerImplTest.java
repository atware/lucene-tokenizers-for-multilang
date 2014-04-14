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
import java.util.Map;

import jp.co.atware.solr.analizers.cjk.CJKTokenizer;
import jp.co.atware.solr.analizers.cjk.CJKTokenizerFactory;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

/**
 * exampleとして用意した設定ファイル用のテストクラスです.
 * @author atware
 */
public class ExampleCJKTokenizerImplTest extends AbstractCJKTokenizerImplTest {

    @DataPoints
    public static final TestData[] testDataSet = new TestData[] {
            //testcase1
            testData(
                    //INPUT 英字、数字、ロシア語、英字・ロシア語交じり、アラビア文字
                    "acyzABYZ ａｂｙｚＡＢＹＺ 0189０１８９ постатейный abcdeпостатейный السياسة",
                    //Expected
                    expected(0, 8, WORD_TYPE, 1),
                    expected(9, 17, WORD_TYPE, 1),
                    expected(18, 26, WORD_TYPE, 1),
                    expected(27, 38, WORD_TYPE, 1),
                    expected(39, 44, WORD_TYPE, 1),
                    expected(44, 55, WORD_TYPE, 1),
                    expected(56, 63, WORD_TYPE, 1)),
            //testcase2
            testData(
                    //INPUT 漢字、平仮名、片仮名、ハングル
                    "漢字ひらカタ조건",
                    //Expected
                    expected(0, 1, NGRAM_TYPE, 1),
                    expected(1, 2, NGRAM_TYPE, 1),
                    expected(2, 3, NGRAM_TYPE, 1),
                    expected(3, 4, NGRAM_TYPE, 1),
                    expected(4, 5, NGRAM_TYPE, 1),
                    expected(5, 6, NGRAM_TYPE, 1),
                    expected(6, 7, NGRAM_TYPE, 1),
                    expected(7, 8, NGRAM_TYPE, 1)), };

    @Theory
    public void testIncrementToken(TestData testData) throws Exception {
        CJKTokenizer testTarget = createTestTarget(testData.input);
        super.testIncrementToken(testData, testTarget);
    }

    @Override
    @Before
    public void setup() {
        Map<String, String> args = new HashMap<String, String>();
        args.put("custom", "true");
        args.put("action", getPath("example-settings/zz_action"));
        args.put("cmap", getPath("example-settings/zz_cmap"));
        args.put("trans", getPath("example-settings/zz_trans"));

        factory = new CJKTokenizerFactory(args);

    }

    protected CJKTokenizer createTestTarget(String string) {
        StringReader reader = new StringReader(string);
        CJKTokenizer tokenizer = (CJKTokenizer) factory.create(reader);
        return tokenizer;
    }

    private String getPath(String string) {
        return ExampleCJKTokenizerImplTest.class.getClassLoader()
                .getResource(string).getPath();
    }
}
