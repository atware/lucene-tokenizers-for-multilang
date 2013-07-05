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

package jp.co.atware.solr.analizers.cjk.dfa;

import static org.apache.commons.lang.ArrayUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DFAInitializer {

    private static final int CODEPOINTS_SIZE = 0x10000;
    private static final String CMAP_VALUE_PREFIX = "\\u";

    public static char[] readCMap(InputStream cmapFile) {
        char[] cmap = new char[CODEPOINTS_SIZE];
        DFAFileHandler<char[]> handler = new DFAFileHandler<char[]>() {
            @Override
            public void handleLine(String line, char[] cmap) {
                String[] columns = line.split("\t");
                int start = Integer.parseInt(
                        columns[0].substring(CMAP_VALUE_PREFIX.length()), 16);
                int end = Integer.parseInt(
                        columns[1].substring(CMAP_VALUE_PREFIX.length()), 16);
                int value = Integer.parseInt(columns[2]);
                for (int i = start; i <= end; i++) {
                    cmap[i] = (char) value;
                }
            }
        };
        handler.handleFile(cmapFile, cmap);
        return cmap;
    }

    public static int[][] readTrans(InputStream transFile) {
        List<int[]> tmpList = new ArrayList<int[]>();
        DFAFileHandler<List<int[]>> handler = new DFAFileHandler<List<int[]>>() {
            @Override
            public void handleLine(String line, List<int[]> zzTrans) {
                String[] columns = line.split("\t");
                int[] transColumns = new int[columns.length - 1];
                for (int i = 1; i < columns.length; i++) {
                    int value = Integer.parseInt(columns[i]);
                    transColumns[i - 1] = value;
                }
                zzTrans.add(transColumns);
            }
        };
        handler.handleFile(transFile, tmpList);
        return tmpList.toArray(new int[][] {});
    }

    public static int[] readAction(InputStream actionFile) {
        List<Integer> tmpAction = new ArrayList<Integer>();
        DFAFileHandler<List<Integer>> handler = new DFAFileHandler<List<Integer>>() {
            @Override
            public void handleLine(String line, List<Integer> zzAction) {
                String[] columns = line.split("\t");
                int actionNum = Integer.parseInt(columns[1]);
                zzAction.add(actionNum);
            }
        };
        handler.handleFile(actionFile, tmpAction);
        return toPrimitive(tmpAction.toArray(EMPTY_INTEGER_OBJECT_ARRAY), 0);
    }

    private static abstract class DFAFileHandler<T> {
        public void handleFile(InputStream initFile, T t) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(initFile,
                        "UTF-8"));
                String line = null;
                boolean isHead = true;
                while ((line = reader.readLine()) != null) {
                    if (isHead) {
                        isHead = false;
                    } else {
                        if (!line.isEmpty()) {
                            handleLine(line, t);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }

        public abstract void handleLine(String line, T t);
    }
}
