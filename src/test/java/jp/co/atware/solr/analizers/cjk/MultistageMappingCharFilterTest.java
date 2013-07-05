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

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MultistageMappingCharFilterTest {

    private MultistageMappingCharFilterFactory charFilterFactory;
    private CJKTokenizerFactory tokenizerFactory;

    @Before
    public void setup() throws Exception {
        charFilterFactory = new MultistageMappingCharFilterFactory();
        Map<String, String> args = new HashMap<String, String>();
        args.put(
                "mapping",
                "src/test/resources/multistage-test/first.txt;src/test/resources/multistage-test/second.txt");
        charFilterFactory.init(args);
        charFilterFactory.inform(new FilesystemResourceLoader());
        tokenizerFactory = new CJKTokenizerFactory();
        tokenizerFactory.init(new HashMap<String, String>());
    }

    @DataPoints
    public static final TestData[] testDataSet = new TestData[] {
            //"AAAAAA" => "\u00C0", "\u00C0" => "\u0041"
            new TestData("AAAAAA", "CCB", 0, 6),
            //"\CCC" => "\u01FC", "\u01FC" => "\u0041\u0045"
            new TestData("CCC", "EEEEDC", 0, 3),
            //"\CCC" => "\u01FC", "\u01FC" => "\u0041\u0045"
            new TestData("FFFFFF", "HHHHGFF", 0, 6),
            //"\CCC" => "\u01FC", "\u01FC" => "\u0041\u0045"
            new TestData("IIIII", "KKKJI", 0, 5), };

    @Theory
    public void testMultiMappingAndOffset(TestData testData) throws Exception {
        Reader reader = charFilterFactory.create(new StringReader(
                testData.input));
        TokenStream tokenStream = tokenizerFactory.create(reader);
        OffsetAttribute actualOffset = tokenStream
                .getAttribute(OffsetAttribute.class);
        CharTermAttribute termAtt = tokenStream
                .getAttribute(CharTermAttribute.class);

        assertThat(tokenStream.incrementToken(), is(true));
        assertThat(termAtt.toString(), is(testData.expected));
        assertThat(actualOffset.startOffset(), is(testData.start));
        assertThat(actualOffset.endOffset(), is(testData.end));
        assertThat(tokenStream.incrementToken(), is(false));
    }

    public static class TestData {
        private TestData(String input, String expected, int start, int end) {
            this.input = input;
            this.expected = expected;
            this.start = start;
            this.end = end;
        }

        private final String input;
        private final String expected;
        private final int start;
        private final int end;

    }
}
