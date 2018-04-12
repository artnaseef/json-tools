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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.spi.json.GsonJsonProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by art on 5/12/16.
 */
public class JsonPathUtil {

  private Gson gson;
  private Configuration jsonPathConfiguration;
  private boolean raw = false;

  public static void main(String[] args) {
    new JsonPathUtil().instanceMain(args);
  }

  public void instanceMain(String[] args) {
    if ((args.length > 0) && (args[0].equals("--raw"))) {
      args = Arrays.copyOfRange(args, 1, args.length);
      this.raw = true;
    }

    if (args.length < 2) {
      System.err.println("Usage: JsonPathUtil [--raw] <filename> <path> ...");
      System.err.println("Use filename - for standard input");
      System.exit(1);
    }

    try {
      String filename = args[0];
      String[] paths = Arrays.copyOfRange(args, 1, args.length);

      this.init();

      JsonElement jsonDoc = parseInput(filename);

      for (String onePath : paths) {
        JsonPath jsonPath = JsonPath.compile(onePath);

        JsonArray resultList = jsonPath.read(jsonDoc, this.jsonPathConfiguration);

        if (raw) {
          for (JsonElement oneResult : resultList) {
            if (oneResult.isJsonPrimitive()) {
              System.out.println(oneResult.getAsString());
            } else {
              System.out.println(oneResult.toString());
            }
          }
        } else {
          System.out.println("PATH \"" + onePath + "\" " + resultList.size() + " result(s):");
          for (JsonElement oneResult : resultList) {
            System.out.println("\t" + oneResult.toString());
          }
        }
      }
    } catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
  }

  private void init() {
    this.gson = new GsonBuilder().create();
    this.jsonPathConfiguration =
        Configuration.builder()
            .jsonProvider(new GsonJsonProvider())
            .options(Option.ALWAYS_RETURN_LIST)
            .build();

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
}
