package com.jindi.land.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author code4crafter@gmail.com Date: 17/3/11 Time: 10:36
 * @since 0.6.2
 */
public abstract class CharsetUtils {

  private static final Pattern patternForCharset = Pattern
      .compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)",
          Pattern.CASE_INSENSITIVE);
  private static Logger logger = LoggerFactory.getLogger(CharsetUtils.class);

  public static String getCharset(String contentType) {
    Matcher matcher = patternForCharset.matcher(contentType);
    if (matcher.find()) {
      String charset = matcher.group(1);
      if (Charset.isSupported(charset)) {
        return charset;
      }
    }
    return null;
  }

  public static String detectCharset(String contentType, byte[] contentBytes) {
    String charset;
    // charset
    // 1、encoding in http header Content-Type
    charset = getCharset(contentType);
    if (StringUtils.isNotBlank(contentType) && StringUtils.isNotBlank(charset)) {
      logger.debug("Auto get charset: {}", charset);
      return charset;
    }
    // use default charset to decode first time
    Charset defaultCharset = Charset.defaultCharset();
    String content = new String(contentBytes, defaultCharset);
    // 2、charset in meta
    if (StringUtils.isNotEmpty(content)) {
      Document document = Jsoup.parse(content);
      Elements links = document.select("meta");
      for (Element link : links) {
        // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html;
        // charset=UTF-8" />
        String metaContent = link.attr("content");
        String metaCharset = link.attr("charset");
        if (metaContent.indexOf("charset") != -1) {
          metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
          charset = metaContent.split("=")[1];
          break;
        }
        // 2.2、html5 <meta charset="UTF-8" />
        else if (StringUtils.isNotEmpty(metaCharset)) {
          charset = metaCharset;
          break;
        }
      }
    }
    logger.debug("Auto get charset: {}", charset);
    return charset;
  }

}
