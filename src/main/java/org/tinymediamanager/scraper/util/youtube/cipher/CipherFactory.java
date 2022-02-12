/*
 * Copyright 2012 - 2020 Manuel Laggner
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
package org.tinymediamanager.scraper.util.youtube.cipher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinymediamanager.scraper.util.UrlUtil;
import org.tinymediamanager.scraper.util.youtube.exception.YoutubeCipherException;
import org.tinymediamanager.scraper.util.youtube.model.YoutubeMedia;

public class CipherFactory {

  private static String[]              INITIAL_FUNCTION_PATTERNS    = new String[] {
      "\\b[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
      "\\b[a-zA-Z0-9]+\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*encodeURIComponent\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
      "\\b([a-zA-Z0-9$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)",
      "([a-zA-Z0-9$]+)\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)",
      "([\"'])signature\\1\\s*,\\s*([a-zA-Z0-9$]+)\\(", "\\.sig\\|\\|([a-zA-Z0-9$]+)\\(",
      "yt\\.akamaized\\.net/\\)\\s*\\|\\|\\s*.*?\\s*[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*(?:encodeURIComponent\\s*\\()?\\s*()$",
      "\\b[cs]\\s*&&\\s*[adf]\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(",
      "\\b[a-zA-Z0-9]+\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*([a-zA-Z0-9$]+)\\(",
      "\\bc\\s*&&\\s*a\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
      "\\bc\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\(",
      "\\bc\\s*&&\\s*[a-zA-Z0-9]+\\.set\\([^,]+\\s*,\\s*\\([^)]*\\)\\s*\\(\\s*([a-zA-Z0-9$]+)\\(" };

  private static String                FUNCTION_REVERSE_PATTERN     = "\\{\\w\\.reverse\\(\\)\\}";
  private static String                FUNCTION_SPLICE_PATTERN      = "\\{\\w\\.splice\\(0,\\w\\)\\}";
  private static String                FUNCTION_SWAP1_PATTERN       = "\\{var\\s\\w=\\w\\[0];\\w\\[0]=\\w\\[\\w%\\w.length];\\w\\[\\w]=\\w\\}";
  private static String                FUNCTION_SWAP2_PATTERN       = "\\{var\\s\\w=\\w\\[0];\\w\\[0]=\\w\\[\\w%\\w.length];\\w\\[\\w%\\w.length]=\\w\\}";

  private static Pattern               JS_FUNCTION_PATTERN          = Pattern.compile("\\w+\\.(\\w+)\\(\\w,(\\d+)\\)");

  private List<Pattern>                knownInitialFunctionPatterns = new ArrayList<>();
  private Map<Pattern, CipherFunction> functionsEquivalentMap       = new HashMap<>();


  public CipherFactory() {

    for (String pattern : INITIAL_FUNCTION_PATTERNS) {
      addInitialFunctionPattern(pattern);
    }

    addFunctionEquivalent(FUNCTION_REVERSE_PATTERN, new ReverseFunction());
    addFunctionEquivalent(FUNCTION_SPLICE_PATTERN, new SpliceFunction());
    addFunctionEquivalent(FUNCTION_SWAP1_PATTERN, new SwapFunctionV1());
    addFunctionEquivalent(FUNCTION_SWAP2_PATTERN, new SwapFunctionV2());
  }

  public void addInitialFunctionPattern(String regex) {
    knownInitialFunctionPatterns.add(Pattern.compile(regex));
  }

  public void addFunctionEquivalent(String regex, CipherFunction function) {
    functionsEquivalentMap.put(Pattern.compile(regex), function);
  }

  public Cipher createCipher(String jsUrl) throws Exception {
    Cipher cipher = YoutubeMedia.ciphers.get(jsUrl);


    if (cipher == null) {
      String js = UrlUtil.getStringFromUrl(jsUrl);

      List<JsFunction> transformFunctions = getTransformFunctions(js);
      String var = transformFunctions.get(0).getVar();

      Map<String, CipherFunction> transformFunctionsMap = getTransformFunctionsMap(var, js);

      cipher = new DefaultCipher(transformFunctions, transformFunctionsMap);
      YoutubeMedia.ciphers.put(jsUrl, cipher);
    }

    return cipher;
  }

  private List<JsFunction> getTransformFunctions(String js) throws Exception {
    String name = getInitialFunctionName(js).replaceAll("[^A-Za-z0-9_]", "");

    Pattern pattern = Pattern.compile(name + "=function\\(\\w\\)\\{[a-z=\\.\\(\\\"\\)]*;(.*);(?:.+)\\}");

    Matcher matcher = pattern.matcher(js);
    if (matcher.find()) {
      String[] split = matcher.group(1).split(";");
      List<JsFunction> jsFunctions = new ArrayList<>(split.length);
      for (String jsFunction : split) {
        String funVar = jsFunction.split("\\.")[0];

        String[] parsedFunction = parseFunction(jsFunction);
        String funName = parsedFunction[0];
        String funArgument = parsedFunction[1];

        jsFunctions.add(new JsFunction(funVar, funName, funArgument));
      }
      return jsFunctions;
    }

    throw new YoutubeCipherException("Transformation functions not found");

  }

  private String getInitialFunctionName(String js) throws Exception {
    for (Pattern pattern : knownInitialFunctionPatterns) {
      Matcher matcher = pattern.matcher(js);
      if (matcher.find()) {
        return matcher.group(1);
      }
    }
    throw new YoutubeCipherException("Initial function name not found");
  }

  private Map<String, CipherFunction> getTransformFunctionsMap(String var, String js) throws Exception {
    String[] transformObject = getTransformObject(var, js);
    Map<String, CipherFunction> mapper = new HashMap<>();
    for (String obj : transformObject) {
      String[] split = obj.split(":", 2);
      String name = split[0];
      String jsFunction = split[1];

      CipherFunction function = mapFunction(jsFunction);
      mapper.put(name, function);
    }
    return mapper;
  }

  private String[] getTransformObject(String var, String js) throws Exception {
    var = var.replaceAll("[^A-Za-z0-9_]", "");
    Pattern pattern = Pattern.compile(String.format("var %s=\\{(.*?)\\};", var), Pattern.DOTALL);
    Matcher matcher = pattern.matcher(js);
    if (matcher.find()) {
      return matcher.group(1).replaceAll("\n", " ").split(", ");
    }

    throw new YoutubeCipherException("Transofrm object not found");
  }

  private CipherFunction mapFunction(String jsFunction) throws Exception {
    for (Map.Entry<Pattern, CipherFunction> entry : functionsEquivalentMap.entrySet()) {
      Matcher matcher = entry.getKey().matcher(jsFunction);
      if (matcher.find()) {
        return entry.getValue();
      }
    }

    throw new YoutubeCipherException("Map function not found");
  }

  private String[] parseFunction(String jsFunction) throws Exception {
    Matcher matcher = JS_FUNCTION_PATTERN.matcher(jsFunction);

    String[] nameAndArgument = new String[2];
    if (matcher.find()) {
      nameAndArgument[0] = matcher.group(1);
      nameAndArgument[1] = matcher.group(2);
      return nameAndArgument;
    }
    throw new YoutubeCipherException("Could not parse js function");
  }

}
