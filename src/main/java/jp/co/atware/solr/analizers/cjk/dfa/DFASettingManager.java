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

import static jp.co.atware.solr.analizers.cjk.dfa.DFAInitializer.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DFASettingManager {

    private static final DFASettingManager instance = new DFASettingManager();

    private final Map<String, char[]> cmaps = new HashMap<String, char[]>();
    private final Map<String, int[]> actions = new HashMap<String, int[]>();
    private final Map<String, int[][]> transes = new HashMap<String, int[][]>();

    private DFASettingManager() {
    }

    public static DFASettingManager getInstance() {
        return instance;
    }

    public char[] getCMap(String filePath) {
        synchronized (cmaps) {
            if (cmaps.containsKey(filePath)) {
                return cmaps.get(filePath);
            }
            cmaps.put(filePath, readCMap(getInputStream(filePath)));
            return cmaps.get(filePath);
        }
    }

    public int[] getAction(String filePath) {
        synchronized (actions) {
            if (actions.containsKey(filePath)) {
                return actions.get(filePath);
            }
            actions.put(filePath, readAction(getInputStream(filePath)));
            return actions.get(filePath);
        }
    }

    public int[][] getTrans(String filePath) {
        synchronized (transes) {
            if (transes.containsKey(filePath)) {
                return transes.get(filePath);
            }
            transes.put(filePath, readTrans(getInputStream(filePath)));
            return transes.get(filePath);
        }
    }

    private InputStream getInputStream(String filePath) {
        try {
            return new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
