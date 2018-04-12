/*
 * Copyright (c) 2018 Arthur Naseef
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlinv.json.util.output;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.Assert.*;

/**
 * Created by art on 7/6/16.
 */
public class FullUnifiedJsonDiffPrinterTest {

  private ByteArrayOutputStream outBuffer;
  private PrintWriter printWriter;
  private FullUnifiedJsonDiffPrinter printer;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.outBuffer = new ByteArrayOutputStream();
    this.printWriter = new PrintWriter(this.outBuffer);

    this.printer = new FullUnifiedJsonDiffPrinter(this.printWriter);
  }

  @Test
  public void testSimpleObject() {
    this.printer.startObject();
    this.printer.startMember("first");
    this.printer.printValue("123");
    this.printer.startMember("second");
    this.printer.printValue("456");
    this.printer.finishObject();

    this.printer.close();

    assertEquals("{\n    \"first\": 123,\n    \"second\": 456\n}\n", this.outBuffer.toString());
  }

  @Test
  public void testSimpleNestedObject() {
    this.printer.startObject();

    this.printer.startMember("child1");
    this.printer.startObject();
    this.printer.startMember("first");
    this.printer.printValue("123");
    this.printer.startMember("second");
    this.printer.printValue("456");
    this.printer.finishObject();

    this.printer.finishObject();

    this.printer.close();
    assertEquals(
        "{\n    \"child1\": {\n        \"first\": 123,\n        \"second\": 456\n    }\n}\n",
        this.outBuffer.toString());
  }

  @Test
  public void testSimpleArray() {
    this.printer.startArray();
    this.printer.startArrayMember();
    this.printer.printValue("123");
    this.printer.startArrayMember();
    this.printer.printValue("456");
    this.printer.finishArray();

    this.printer.close();

    assertEquals("[\n    123,\n    456\n]\n", this.outBuffer.toString());
  }

  @Test
  public void testSimpleNestedArray() {
    this.printer.startArray();

    this.printer.startArray();
    this.printer.startArrayMember();
    this.printer.printValue("123");
    this.printer.startArrayMember();
    this.printer.printValue("456");
    this.printer.finishArray();

    this.printer.finishArray();

    this.printer.close();

    assertEquals("[\n    [\n        123,\n        456\n    ]\n]\n", this.outBuffer.toString());
  }

  @Test
  public void testComplexObject() {
    this.printer.startObject();

    this.printer.startMember("childObject1");
    this.printer.startObject();

    this.printer.startMember("grandchildObject1");
    this.printer.startObject();
    this.printer.startMember("grandchild-member1");
    this.printer.printValue("grandchild-value1");
    this.printer.startMember("grandchild-member2");
    this.printer.printValue("grandchild-value2");
    this.printer.finishObject();

    this.printer.startMember("grandchildArray2");
    this.printer.startArray();
    this.printer.startArrayMember();
    this.printer.printValue("\"home\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"work\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"mall\"");
    this.printer.finishArray();

    this.printer.finishObject();

    this.printer.startMember("childObject2");
    this.printer.startObject();
    this.printer.startMember("grandchildObject3");

    this.printer.startObject();
    this.printer.startMember("grandchild-member3");
    this.printer.printValue("\"Joe\"");
    this.printer.finishObject();

    this.printer.startMember("grandchildObject4");
    this.printer.startObject();
    this.printer.startMember("grandchild-member4");
    this.printer.printValue("\"George\"");
    this.printer.finishObject();

    this.printer.finishObject();
    this.printer.finishObject();


    this.printer.close();

    assertEquals("{\n"
                 + "    \"childObject1\": {\n"
                 + "        \"grandchildObject1\": {\n"
                 + "            \"grandchild-member1\": grandchild-value1,\n"
                 + "            \"grandchild-member2\": grandchild-value2\n"
                 + "        },\n"
                 + "        \"grandchildArray2\": [\n"
                 + "            \"home\",\n"
                 + "            \"work\",\n"
                 + "            \"mall\"\n"
                 + "        ]\n"
                 + "    },\n"
                 + "    \"childObject2\": {\n"
                 + "        \"grandchildObject3\": {\n"
                 + "            \"grandchild-member3\": \"Joe\"\n"
                 + "        },\n"
                 + "        \"grandchildObject4\": {\n"
                 + "            \"grandchild-member4\": \"George\"\n"
                 + "        }\n"
                 + "    }\n"
                 + "}\n",
                 this.outBuffer.toString());
  }

  @Test
  public void testComplexArray() {
    this.printer.startArray();

    this.printer.startArrayMember();
    this.printer.startObject();

    this.printer.startMember("grandchildObject1");
    this.printer.startObject();
    this.printer.startMember("grandchild-member1");
    this.printer.printValue("grandchild-value1");
    this.printer.startMember("grandchild-member2");
    this.printer.printValue("grandchild-value2");
    this.printer.finishObject();

    this.printer.startMember("grandchildArray2");
    this.printer.startArray();
    this.printer.startArrayMember();
    this.printer.printValue("\"home\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"work\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"mall\"");
    this.printer.finishArray();

    this.printer.finishObject();

    this.printer.startArrayMember();
    this.printer.startObject();
    this.printer.startMember("grandchildObject3");

    this.printer.startObject();
    this.printer.startMember("grandchild-member3");
    this.printer.printValue("\"Joe\"");
    this.printer.finishObject();

    this.printer.startMember("grandchildObject4");
    this.printer.startObject();
    this.printer.startMember("grandchild-member4");
    this.printer.printValue("\"George\"");
    this.printer.finishObject();

    this.printer.finishObject();
    this.printer.finishArray();


    this.printer.close();

    assertEquals("[\n"
                 + "    {\n"
                 + "        \"grandchildObject1\": {\n"
                 + "            \"grandchild-member1\": grandchild-value1,\n"
                 + "            \"grandchild-member2\": grandchild-value2\n"
                 + "        },\n"
                 + "        \"grandchildArray2\": [\n"
                 + "            \"home\",\n"
                 + "            \"work\",\n"
                 + "            \"mall\"\n"
                 + "        ]\n"
                 + "    },\n"
                 + "    {\n"
                 + "        \"grandchildObject3\": {\n"
                 + "            \"grandchild-member3\": \"Joe\"\n"
                 + "        },\n"
                 + "        \"grandchildObject4\": {\n"
                 + "            \"grandchild-member4\": \"George\"\n"
                 + "        }\n"
                 + "    }\n"
                 + "]\n",
                 this.outBuffer.toString());
  }

  @Test
  public void testComplexArrayWithDiffs() {
    this.printer.startArray();

    this.printer.startArrayMember();
    this.printer.startObject();

    this.printer.startMember("grandchildObject1");
    this.printer.startObject();
    this.printer.startMember("grandchild-member1");
    this.printer.printValue("grandchild-value1");
    this.printer.startMember("grandchild-member2");
    this.printer.printValue("grandchild-value2");
    this.printer.finishObject();

    this.printer.startMember("grandchildArray2");
    this.printer.startArray();
    this.printer.startArrayMember();
    this.printer.printValue("\"home\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"work\"");
    this.printer.startArrayMember();
    this.printer.printValue("\"mall\"");
    this.printer.finishArray();

    this.printer.finishObject();

    this.printer.startArrayMember();
    this.printer.startObject();
    this.printer.startMember("grandchildObject3");

    this.printer.startObject();
    this.printer.startMember("grandchild-member3");
    this.printer.printValue("\"Joe\"");
    this.printer.finishObject();

    this.printer.startMember("grandchildObject4");
    this.printer.startObject();
    this.printer.startMember("grandchild-member4");
    this.printer.printValue("\"George\"");
    this.printer.finishObject();

    this.printer.finishObject();
    this.printer.finishArray();


    this.printer.close();

    assertEquals("[\n"
                 + "    {\n"
                 + "        \"grandchildObject1\": {\n"
                 + "            \"grandchild-member1\": grandchild-value1,\n"
                 + "            \"grandchild-member2\": grandchild-value2\n"
                 + "        },\n"
                 + "        \"grandchildArray2\": [\n"
                 + "            \"home\",\n"
                 + "            \"work\",\n"
                 + "            \"mall\"\n"
                 + "        ]\n"
                 + "    },\n"
                 + "    {\n"
                 + "        \"grandchildObject3\": {\n"
                 + "            \"grandchild-member3\": \"Joe\"\n"
                 + "        },\n"
                 + "        \"grandchildObject4\": {\n"
                 + "            \"grandchild-member4\": \"George\"\n"
                 + "        }\n"
                 + "    }\n"
                 + "]\n",
                 this.outBuffer.toString());
  }}