package util;

import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;

public class Config {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static final JSONParser JPARSER = new JSONParser();
    //T: separator between date and time; Z: time zone, convert client time to server time

    public static final String ENCODING = "UTF-8";
    public static final String CONTENTTYPE = "application/json";

    public static final String W3BUCKET = "viatickassetmanagement";	
}