package ctrl;

import java.util.HashMap;

public class SecretCtrl {

    public static HashMap<String, String> as;

    public static void addAcc (String s, String e){
        if(as == null){
            as = new HashMap<>();
        }

        as.put(s,e);
    }

    public static boolean check(String m, String s){
        if(as == null){
            as = new HashMap<>();
        }

        return as.containsKey(s) && m.equals(as.get(s));
    }
}
