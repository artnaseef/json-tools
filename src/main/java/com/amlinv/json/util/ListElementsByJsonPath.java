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
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by art on 1/31/18.
 */
public class ListElementsByJsonPath {

  private boolean includeValues = false;

  public static void main(String[] args) {
    new ListElementsByJsonPath().instanceMain(args);
  }

  public void instanceMain(String[] args) {
    if (args.length < 1) {
      this.dumpUsageAndTerminate();
    }

    if (args[0].equals("--include-values")) {
      this.includeValues = true;
      args = Arrays.copyOfRange(args, 1, args.length);
    }

    if (args.length < 1) {
      this.dumpUsageAndTerminate();
    }

    try {
      for (String filename : args) {
        JsonElement jsonElement = this.parseInput(filename);

        this.dumpJsonPath(jsonElement);
      }
    } catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
  }

//========================================
// Internal Methods
//----------------------------------------

  private void dumpUsageAndTerminate() {
    System.err.println("Usage: ListElementsByJsonPath [--include-values] <filename1> ...");
    System.err.println("Use filename - for standard input");
    System.exit(1);
  }

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

  private void dumpJsonPath(JsonElement jsonElement) {
    this.dumpJsonPathAtPath("$", jsonElement);
  }

  private void dumpJsonPathAtPath(String prefix, JsonElement jsonElement) {
    if (jsonElement.isJsonNull()) {
      System.out.print(prefix);

      if (this.includeValues) {
        System.out.print(": null");
      }

      System.out.println();
    } else if (jsonElement.isJsonPrimitive()) {
      System.out.print(prefix);

      if (this.includeValues) {
        System.out.print(": " + jsonElement.getAsString());
      }

      System.out.println();
    } else if (jsonElement.isJsonObject()) {
      JsonObject object = jsonElement.getAsJsonObject();

      for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
        this.dumpJsonPathAtPath(prefix + "." + entry.getKey(), entry.getValue());
      }
    } else if (jsonElement.isJsonArray()) {
      JsonArray jsonArray = jsonElement.getAsJsonArray();

      int index = 0;
      for (JsonElement entry : jsonArray) {
        this.dumpJsonPathAtPath(prefix + "[" + index + "]", entry);
        index++;
      }
    } else {
      System.out.println(
          "UNKNOWN ELEMENT TYPE AT " + prefix + ": class=" + jsonElement.getClass().getName());
    }
  }
}
