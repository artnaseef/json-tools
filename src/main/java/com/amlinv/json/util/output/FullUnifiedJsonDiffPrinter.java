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

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by art on 7/6/16.
 */
public class FullUnifiedJsonDiffPrinter implements Closeable
{
  private PrintWriter out;
  private int currentLevel = 0;
  private boolean insideMember = false; // Waiting on member value
  private boolean firstValue = true;

  private boolean newOutputLine = true;

  public FullUnifiedJsonDiffPrinter(PrintWriter out) {
    this.out = out;
  }

  public void startObject() {
    this.closeLastField(true);

    if (! this.insideMember) {
      this.startLine();
    }

    out.println("{");

    this.currentLevel++;
    this.firstValue = true;

    this.newOutputLine = true;
    this.insideMember = false;
  }

  public void finishObject() {
    this.closeLastField(false);

    this.currentLevel--;

    this.startLine();
    out.print("}");

    this.firstValue = false;
    this.newOutputLine = false;
    this.insideMember = false;
  }

  public void startArray() {
    this.closeLastField(true);

    if (! this.insideMember) {
      this.startLine();
    }

    out.println("[");

    this.currentLevel++;

    this.firstValue = true;
    this.insideMember = false;
    this.newOutputLine = true;
  }

  public void finishArray() {
    this.closeLastField(false);

    this.currentLevel--;

    this.startLine();
    out.print("]");

    this.firstValue = false;
    this.newOutputLine = false;
    this.insideMember = false;
  }

  public void startMember(String name) {
    this.closeLastField(true);
    this.startLine();

    out.print("\"");
    out.print(name);
    out.print("\": ");

    this.newOutputLine = false;
    this.insideMember = true;
  }

  public void startArrayMember() {
    this.closeLastField(true);

    this.startLine();

    this.insideMember = true;
  }

  public void printValue(String value) {
    out.print(value);

    this.firstValue = false;
    this.insideMember = false;
  }

  public void printDiffIndicator(char indicator) {
    // TBD999: this is tricky
    // TBD999: CASE 1. diff on only element in object/array
    // TBD999: CASE 2. diff on only 2 elements in object/array
    // TBD999: CASE 3. diff on last element in object/array
    // TBD999: CASE 4. value type change (e.g. from object to array)
    this.closeLastField(true);

    out.print(indicator);
    out.print(" ");

    this.startLine(-2);
  }

  @Override
  public void close() {
    this.closeLastField(false);
    this.out.flush();
  }

//========================================
// Internal Methods
//----------------------------------------

  private void startValue() {
    if (! this.firstValue) {
      out.println(",");
      this.startLine();
    }
  }

  private void closeLastField (boolean more) {
    if (this.newOutputLine) {
      return;
    }

    if (! this.insideMember) {
      if ((this.firstValue) || (!more)) {
        out.println();
      } else {
        out.println(",");
        this.firstValue = false;
      }

      this.newOutputLine = true;
    }
  }

  private void startLine() {
    this.startLine(0);
  }

  private void startLine(int prefixOffset) {
    if (currentLevel > 0) {
      out.print(this.createLevelPrefix(this.currentLevel, prefixOffset));
    }

    this.newOutputLine = false;
  }

  private String createLevelPrefix(int level, int prefixOffset) {
    int len = ( level * 4 ) + prefixOffset;
    if (len <= 0) {
      return "";
    }

    char[] chars = new char[len];

    Arrays.fill(chars, ' ');

    return new String(chars);
  }
}
