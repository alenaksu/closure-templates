/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.template.soy.passes;

import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.error.ErrorReporter;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.TemplateElementNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.soytree.defn.TemplatePropVar;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.ast.TypeNodeConverter;

/** Resolve the TypeNode objects in HeaderParams to SoyTypes */
final class ResolveHeaderParamTypesPass extends CompilerFilePass {
  private final TypeNodeConverter converter;

  ResolveHeaderParamTypesPass(SoyTypeRegistry typeRegistry, ErrorReporter errorReporter) {
    this.converter = new TypeNodeConverter(errorReporter, typeRegistry);
  }

  @Override
  public void run(SoyFileNode file, IdGenerator nodeIdGen) {
    for (TemplateNode template : file.getChildren()) {
      for (TemplateParam param : template.getAllParams()) {
        if (param instanceof HeaderParam) {
          HeaderParam hp = (HeaderParam) param;
          if (hp.getTypeNode() != null) {
            hp.setType(converter.getOrCreateType(hp.getTypeNode()));
          }
        }
      }
      if (template instanceof TemplateElementNode) {
        for (TemplatePropVar prop : ((TemplateElementNode) template).getPropVars()) {
          if (prop.getTypeNode() != null) {
            prop.setType(converter.getOrCreateType(prop.getTypeNode()));
          }
        }
      }
    }
  }
}
