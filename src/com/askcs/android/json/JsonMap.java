package com.askcs.android.json;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMap extends LinkedHashMap<String, String> {
  
  static public TreeNode parse( String string ) throws JsonParseException,
      IOException {
    JsonFactory factory = new JsonFactory();
    JsonParser parser = factory.createParser( string );
    ObjectMapper mapper = new ObjectMapper();
    TreeNode tree = mapper.readTree( parser );
    return tree;
  }
  
  private JsonMap() {
  }
  
}
