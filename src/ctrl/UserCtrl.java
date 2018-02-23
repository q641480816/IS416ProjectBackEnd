package ctrl;

import dao.UserDao;
import model.User.User;
import org.json.simple.JSONObject;
import util.Key;
import util.Message;
import util.Value;

import java.util.Date;

public class UserCtrl {

    public static JSONObject createAccount(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            String email = (String) inputJson.get(Key.EMAIL);
            String password = (String) inputJson.get(Key.PASSWORD);

            String passwordSalt = EncrypCtrl.nextSalt();
            String passwordHash = EncrypCtrl.generateSaltedHash(password, passwordSalt);

            //if (UserDao.getAccountByEmail(email) == null){
                User user = new User(email,passwordHash,passwordSalt,null,null,-1,new Date(),null);
                UserDao.addAccount(user);
                String secret = EncrypCtrl.generatePasswordSecret(email);

                JSONObject content = new JSONObject();
                content.put(Key.SECRET, secret);
                content.put(Key.USER, user.toJson());
                returnJson.put(Key.STATUS, Value.SUCCESS);
                returnJson.put(Key.DATA, content);
            //}else{
                returnJson.put(Key.STATUS, Value.FAIL);
                returnJson.put(Key.MESSAGE, Message.USER_EXIST);
           //}

        }catch (Exception e){
            e.printStackTrace();
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }
}
