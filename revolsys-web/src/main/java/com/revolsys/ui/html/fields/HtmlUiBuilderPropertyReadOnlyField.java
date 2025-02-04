/*
 * Copyright 2004-2005 Revolution Systems Inc.
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
package com.revolsys.ui.html.fields;

import javax.servlet.http.HttpServletRequest;

import com.revolsys.record.io.format.xml.XmlWriter;
import com.revolsys.ui.html.builder.HtmlUiBuilder;
import com.revolsys.ui.html.form.Form;

public class HtmlUiBuilderPropertyReadOnlyField extends Field {

  private HtmlUiBuilder builder;

  /**
   * @param name
   * @param required
   */
  public HtmlUiBuilderPropertyReadOnlyField(final String name, final boolean required) {
    super(name, false);
    setReadOnly(true);
  }

  public HtmlUiBuilder getBuilder() {
    return this.builder;
  }

  @Override
  public boolean hasValue() {
    return true;
  }

  @Override
  public void initialize(final Form form, final HttpServletRequest request) {
    setValue(getInitialValue(request));
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void serializeElement(final XmlWriter out) {
    if (getValue() != null) {
      this.builder.serialize(out, getValue(), getName());
    } else {
      out.text("-");
    }
  }

  public void setBuilder(final HtmlUiBuilder uiBuilder) {
    this.builder = uiBuilder;
  }
}
