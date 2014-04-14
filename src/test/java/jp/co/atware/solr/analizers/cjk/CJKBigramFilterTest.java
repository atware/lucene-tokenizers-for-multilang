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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.apache.commons.lang.ArrayUtils.*;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class CJKBigramFilterTest {

    private static MappingCharFilterFactory charFilterFactory;
    private static CJKTokenizerFactory tokenizerFactory;
    private static CJKBigramFilterFactory tokenFilterFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("mapping", "mapping.txt");
        charFilterFactory = new MappingCharFilterFactory(map);
        charFilterFactory.inform(new FilesystemResourceLoader(new File(
                "src/test/resources/cjkbigramfilter")));
        tokenizerFactory = new CJKTokenizerFactory(new HashMap<String, String>());
        map = new HashMap<String, String>();
        map.put("outputUnigrams", "true");
        tokenFilterFactory = new CJKBigramFilterFactory(map);
    }

    @DataPoints
    public static final Fixture[] testDataSet = new Fixture[] {
            //TestCase001
            testData(
                    //INPUT mapping to \u062C\u0644\u0020\u062C\u0644\u0627\u0644\u0647
                    "\uFDFB",
                    //EXPECTED
                    "\u062C", "\u062C\u0644", "\u0644", "\u062C",
                    "\u062C\u0644", "\u0644", "\u0644\u0627", "\u0627",
                    "\u0627\u0644", "\u0644", "\u0644\u0647", "\u0647"),
            //TestCase002
            testData(
                    //INPUT mapping to サポトベクタマシン
                    "サポートベクターマシン",
                    //EXPECTED unigram + bigram
                    "サ", "サポ", "ポ", "ポト", "ト", "トベ", "ベ", "ベク", "ク", "クタ", "タ",
                    "タマ", "マ", "マシ", "シ", "シン", "ン"),
            //TestCase003
            testData(
            //INPUT
                    "本日―は晴天　なり",
                    //EXPECTED
                    "本", "本日", "日", "は", "は晴", "晴", "晴天", "天", "な", "なり", "り"),
            //TestCase004
            testData(
                    //INPUT
                    "本日の日付は2013年6月7日です  Hmmmm ",
                    //EXPECTED
                    "本", "本日", "日", "日の", "の", "の日", "日", "日付", "付", "付は", "は",
                    "2013", "年", "6", "月", "7", "日", "日で", "で", "です", "す",
                    "Hmmmm"),
            //TestCase005
            testData(
            //INPUT mapping to ""
                    "ーー ーー",
                    //EXPECTED
                    EMPTY_STRING_ARRAY), };

    @Theory
    public void testIncrementToken(Fixture testData) throws Exception {
        TokenStream tokenStream = getTokenStream(testData.input);
        CharTermAttribute termAtt = tokenStream
                .getAttribute(CharTermAttribute.class);
        List<String> actual = new ArrayList<String>();
        while (tokenStream.incrementToken()) {
            actual.add(termAtt.toString());
        }
        assertThat(actual.toArray(EMPTY_STRING_ARRAY), is(testData.expected));
    }

    private TokenStream getTokenStream(String input) throws Exception {
        Reader reader = charFilterFactory.create(new StringReader(input));
        TokenStream stream = tokenizerFactory.create(reader);
        stream = tokenFilterFactory.create(stream);
        return stream;
    }

    /**
     * テストデータクラス
     * @author atware
     */
    public static final class Fixture {
        private Fixture(String input, String... expected) {
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
    public static Fixture testData(String input, String... expected) {
        return new Fixture(input, expected);
    }
}
