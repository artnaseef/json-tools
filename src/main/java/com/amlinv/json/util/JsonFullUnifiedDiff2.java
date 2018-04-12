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

package com.amlinv.json.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by art on 7/6/16.
 */
public class JsonFullUnifiedDiff2 {

  public static void main(String[] args) {
    new JsonFullUnifiedDiff2().instanceMain(args);
  }

  public void instanceMain(String[] args) {
    if (args.length != 2) {
      System.err.println("Usage: JsonFullUnifiedDiff2 <filename1> <filename2>");
      System.err.println("Use filename - for standard input");
      System.exit(1);
    }

    try {
      String filename1 = args[0];
      String filename2 = args[1];

      JsonElement first = this.parseInput(filename1);
      JsonElement second = this.parseInput(filename2);

      this.diff(first, second, new PrintWriter(System.out));
    } catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
  }

  public void diff(JsonElement first, JsonElement second, PrintWriter out) {
    this.diffLevel(first, second, out, 0);

    out.flush();
  }

//========================================
// Internal Methods
//----------------------------------------

  private JsonElement parseInput(String filename) throws FileNotFoundException {
    InputStream inputStream;

    if (filename.equals("-")) {
      inputStream = System.in;
    } else {
      inputStream = new FileInputStream(filename);
    }

    Reader inputReader = new InputStreamReader(inputStream);

    return new JsonParser().parse(inputReader);
  }

  private void diffLevel(JsonElement first, JsonElement second, PrintWriter out, int level) {
    String prefix = this.createLevelPrefix(level);

    if (first.isJsonPrimitive()) {
      if ((!second.isJsonPrimitive() || (!first.equals(second)))) {
        outputSimpleDiff(out, prefix, first, second);
      } else {
        out.println("  " + prefix + first);
      }
    } else if (first.isJsonNull()) {
      if (!second.isJsonNull()) {
        outputSimpleDiff(out, prefix, first, second);
      } else {
        out.println("  " + prefix + "null");
      }
    } else if (first.isJsonObject()) {
      if (second.isJsonObject()) {
        diffObjectsLevel(first, second, out, prefix, level);
      } else {
        outputSimpleDiff(out, prefix, first, second);
      }
    } else if (first.isJsonArray()) {
      if (second.isJsonArray()) {
        diffArraysLevel(first, second, out, prefix, level);
      } else {
        outputSimpleDiff(out, prefix, first, second);
      }
    } else {
      outputSimpleDiff(out, prefix, first, second);
    }
  }

  private void diffObjectsLevel(JsonElement first, JsonElement second, PrintWriter out,
                                String prefix, int level) {
    JsonObject firstObject = (JsonObject) first;
    JsonObject secondObject = (JsonObject) second;

    Set<String> allMemberNames = new TreeSet<>();

    firstObject.entrySet().stream().map(Map.Entry::getKey).forEach(allMemberNames::add);
    secondObject.entrySet().stream().map(Map.Entry::getKey).forEach(allMemberNames::add);

    out.println("  " + prefix + "{");

    String valuePrefix = this.createLevelPrefix(level + 1);

    int tot = allMemberNames.size();
    int cur = 0;

    for (String memberName : allMemberNames) {
      cur++;
      String optionalComma = "";
      if (cur < tot) {
        optionalComma = ",";
      }

      JsonElement firstChild = firstObject.get(memberName);
      JsonElement secondChild = secondObject.get(memberName);

      if (firstChild == null) {
        out.println("+ " + valuePrefix + "\"" + memberName + "\": " + secondChild + optionalComma);
      } else if (secondChild == null) {
        out.println("- " + valuePrefix + "\"" + memberName + "\": " + firstChild + optionalComma);
      } else {
        out.println("  " + valuePrefix + "\"" + memberName + "\":");

        diffLevel(firstChild, secondChild, out, level + 1);
        if (cur < tot) {
          out.println("  " + valuePrefix + ",");
        }
      }
    }

    out.println("  " + prefix + "}");
  }

  private void diffArraysLevel(JsonElement first, JsonElement second, PrintWriter out,
                               String prefix, int level) {
    JsonArray firstArray = (JsonArray) first;
    JsonArray secondArray = (JsonArray) second;

    JsonArray biggerArray;
    String direction;

    int minSize = firstArray.size();
    int maxSize = firstArray.size();
    if (secondArray.size() > maxSize) {
      direction = "+ ";
      biggerArray = secondArray;
      maxSize = secondArray.size();
    } else {
      direction = "- ";
      biggerArray = firstArray;
      minSize = secondArray.size();
    }

    out.println("  " + prefix + "[");
    String valuePrefix = this.createLevelPrefix(level + 1);

    int cur = 0;
    while (cur < minSize) {
      JsonElement firstEle = firstArray.get(cur);
      JsonElement secondEle = secondArray.get(cur);

      this.diffLevel(firstEle, secondEle, out, level + 1);

      if (cur < (maxSize - 1)) {
        out.println("  " + valuePrefix + ",");
      }

      cur++;
    }

    while (cur < maxSize) {
      String optionalComma = "";
      if (cur < (maxSize - 1)) {
        optionalComma = ",";
      }

      out.println(direction + valuePrefix + biggerArray.get(cur) + optionalComma);

      cur++;
    }

    out.println("  " + prefix + "]");
  }

  private void outputSimpleDiff(PrintWriter out, String prefix, JsonElement first,
                                JsonElement second) {

    out.println("- " + prefix + first);
    out.println("+ " + prefix + second);
  }

  private String createLevelPrefix(int level) {
    char[] chars = new char[level * 4];

    Arrays.fill(chars, ' ');

    return new String(chars);
  }
}
