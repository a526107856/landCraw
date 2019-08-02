package com.jindi.land.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ResourceUtil {

  public static String readResource(String path) throws FileNotFoundException {
    Reader reader = null;
    try {
      reader = getResourceReader(path);
      if (reader == null) {
        throw new FileNotFoundException("no file found" + path);
      }
      Scanner s = new Scanner(reader).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
    } finally {
      if (null != reader) {
        try {
          reader.close();
        } catch (IOException ex2) {
          throw new RuntimeException(ex2);
        }
      }
    }

  }

  public static Reader getResourceReader(String name) throws FileNotFoundException {
    InputStream is = null;
    try {
      is = ResourceUtil.class.getClassLoader().getResourceAsStream(getCPResourcePath(name));
      if (is == null) {
        is = new FileInputStream(new File(name));
      }
      return new InputStreamReader(is);
    } catch (FileNotFoundException ex) {
      if (null != is) {
        try {
          is.close();
        } catch (IOException ex2) {
          throw new RuntimeException(ex2);
        }
      }

      throw ex;
    }
  }

  public static String getCPResourcePath(String name) {
    if (!"/".equals(File.separator)) {
      return name.replaceAll(Pattern.quote(File.separator), "/");
    }
    return name;
  }
}