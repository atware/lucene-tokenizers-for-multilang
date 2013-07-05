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

import static org.apache.commons.lang.ArrayUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class CranioCaudalFilterTest {

    private final CJKTokenizerFactory tokenizerFactory = new CJKTokenizerFactory();
    private final CranioCaudalFilterFactory filterFactory = new CranioCaudalFilterFactory();

    @Before
    public void setUp() {
        tokenizerFactory.init(new HashMap<String, String>());
        filterFactory.init(new HashMap<String, String>());
    }

    @DataPoints
    public static final TestData[] testDataSet = new TestData[] {
            //case001 語頭の定冠詞・不定冠詞の除去、語頭以外は削除しない
            testData(
                    //input
                    "d'hogehoge l'gehogeho dell'hogegeho taildell' hogedell'geho l'dell'foobar",
                    //expected token
                    "hogehoge", "gehogeho", "hogegeho", "taildell'",
                    "hogedell'geho", "dell'foobar"),
            //case002 名詞の所有格語尾の除去 語尾以外は削除しない
            testData(
            //input
                    "hoge's geho's gehogeho's",
                    //expected token
                    "hoge", "geho", "gehogeho"),

            //case003 語頭の定冠詞・不定冠詞の除去、語頭以外は削除しない
            testData(
                    //input
                    "D'hogehoge L'gehogeho DELL'hogegeho TAILDELL' HOGEDELL'geho L'DELL'foobar",
                    //expected token
                    "hogehoge", "gehogeho", "hogegeho", "TAILDELL'",
                    "HOGEDELL'geho", "DELL'foobar"),
            //case004 名詞の所有格語尾の除去 語尾以外は削除しない
            testData(
            //input
                    "hoge'S geho'S gehogeho'S",
                    //expected token
                    "hoge", "geho", "gehogeho"),

            //case005 語頭・語尾両方削除
            testData(
            //input
                    "d'hoge's l'geho's dell'gehogeho's",
                    //expected token
                    "hoge", "geho", "gehogeho"),

            //case006 語頭・語尾でないものは削除しない
            testData(
            //input
                    "d' l' dell' 's",
                    //expected token
                    "d'", "l'", "dell'", "'s"),

    };

    @Theory
    public void testIncrementToken(TestData testData) throws Exception {
        TokenStream tokenStream = createTokenStream(testData.input);
        CharTermAttribute termAtt = tokenStream
                .getAttribute(CharTermAttribute.class);
        List<String> actual = new ArrayList<String>();
        while (tokenStream.incrementToken()) {
            actual.add(termAtt.toString());
        }
        assertThat(actual.toArray(EMPTY_STRING_ARRAY), is(testData.expected));
    }

    private TokenStream createTokenStream(String input) {
        StringReader reader = new StringReader(input);
        TokenStream tokenStream = tokenizerFactory.create(reader);
        return filterFactory.create(tokenStream);
    }

    /**
     * TestCaseを記述するクラスです.
     * @author atware
     */
    private static final class TestData {

        private TestData(String input, String[] expected) {
            this.input = input;
            this.expected = expected;
        }

        private final String input;
        private final String[] expected;
    }

    /**
     * テストデータ生成
     * @param input
     * @param expected
     * @return
     */
    public static TestData testData(String input, String... expected) {
        return new TestData(input, expected);
    }
}
