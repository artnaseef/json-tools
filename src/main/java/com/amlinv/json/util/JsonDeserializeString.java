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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by art on 5/12/16.
 */
public class JsonDeserializeString {

  public static void main(String[] args) {
    new JsonDeserializeString().instanceMain(args);
  }

  public void instanceMain(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: JsonDeserializeString <filename>");
      System.err.println("Use filename - for standard input");
      System.exit(1);
    }

    try {
      String filename = args[0];

      JsonElement result = this.parseInput(filename);

      JsonElement secondParseResult = new JsonParser().parse(result.getAsString());

      System.out.println(secondParseResult.toString());
    } catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
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
