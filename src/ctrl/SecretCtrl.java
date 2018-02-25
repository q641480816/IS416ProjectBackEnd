package ctrl;

import java.util.HashMap;

public class SecretCtrl {

    //Hashmap<secret, email> 
    //add email to the secret. 
    public static HashMap<String, String> accountSecret;

    public static void addAcc (String secret, String email){
        if(accountSecret == null){
            accountSecret = new HashMap<>();
        }

        accountSecret.put(secret,email);
    }

    //check if the secret is correctly tired to the email
    public static boolean check(String secret, String email){
        if(accountSecret == null){
            accountSecret = new HashMap<>();
        }

        return accountSecret.containsKey(secret) && email.equals(accountSecret.get(secret));
    }
}
