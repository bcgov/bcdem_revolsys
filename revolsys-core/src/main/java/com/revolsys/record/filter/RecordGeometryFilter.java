/*
 * $URL:$
 * $Author:$
 * $Date:$
 * $Revision:$

 * Copyright 2004-2007 Revolution Systems Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.revolsys.record.filter;

import java.util.function.Predicate;

import com.revolsys.geometry.model.Geometry;
import com.revolsys.record.Record;

public class RecordGeometryFilter<G extends Geometry> implements Predicate<Record> {
  private Predicate<G> filter;

  public RecordGeometryFilter() {
  }

  public RecordGeometryFilter(final Predicate<G> filter) {
    this.filter = filter;
  }

  public Predicate<G> getFilter() {
    return this.filter;
  }

  public void setFilter(final Predicate<G> filter) {
    this.filter = filter;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean test(final Record record) {
    final G geometry = (G)record.getGeometry();
    if (this.filter.test(geometry)) {
      return true;
    } else {
      return false;
    }
  }

}
