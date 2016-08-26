/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.ltr.feature.norm.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.ltr.feature.norm.Normalizer;
import org.junit.Test;

public class TestStandardNormalizer {

  private final SolrResourceLoader solrResourceLoader = new SolrResourceLoader();

  private Normalizer implTestStandard(Map<String,Object> params,
      float expectedAvg, float expectedStd) {
    final Normalizer n = Normalizer.getInstance(
        solrResourceLoader,
        StandardNormalizer.class.getCanonicalName(),
        params);
    assertTrue(n instanceof StandardNormalizer);
    final StandardNormalizer sn = (StandardNormalizer)n;
    assertEquals(sn.getAvg(), expectedAvg, 0.0);
    assertEquals(sn.getStd(), expectedStd, 0.0);
    return n;
  }

  @Test
  public void testNormalizerNoParams() {
    implTestStandard(new HashMap<String,Object>(),
        0.0f,
        1.0f);
  }

  @Test
  public void testInvalidSTD() {
    final Map<String,Object> params = new HashMap<String,Object>();
    params.put("std", "0f");
    implTestStandard(params,
        0.0f,
        0.0f);
  }

  @Test
  public void testInvalidSTD2() {
    final Map<String,Object> params = new HashMap<String,Object>();
    params.put("std", "-1f");
    implTestStandard(params,
        0.0f,
        -1f);
  }

  @Test
  public void testInvalidSTD3() {
    final Map<String,Object> params = new HashMap<String,Object>();
    params.put("avg", "1f");
    params.put("std", "0f");
    implTestStandard(params,
        1f,
        0f);
  }

  @Test
  public void testNormalizer() {
    Map<String,Object> params = new HashMap<String,Object>();
    params.put("avg", "0f");
    params.put("std", "1f");
    final Normalizer identity =
        implTestStandard(params,
            0f,
            1f);

    float value = 8;
    assertEquals(value, identity.normalize(value), 0.0001);
    value = 150;
    assertEquals(value, identity.normalize(value), 0.0001);
    params = new HashMap<String,Object>();
    params.put("avg", "10f");
    params.put("std", "1.5f");
    final Normalizer norm = Normalizer.getInstance(
        solrResourceLoader,
        StandardNormalizer.class.getCanonicalName(),
        params);

    for (final float v : new float[] {10f, 20f, 25f, 30f, 31f, 40f, 42f, 100f,
        10000000f}) {
      assertEquals((v - 10f) / (1.5f), norm.normalize(v), 0.0001);
    }
  }
}