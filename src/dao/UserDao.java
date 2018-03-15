package dao;

import ctrl.EncrypCtrl;
import model.User;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Key;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static User getAccountById(long id) {
        User u = (User) HibernateUtil.get(User.class, id);
        return u;
    }

    public static void addAccount(User user) {
        HibernateUtil.save(user);
    }

    public static void updateAccount(User user) {
        HibernateUtil.update(user);
    }

    public static void deleteAccount(User user) {
        HibernateUtil.delete(user);
    }

    public static User getAccountByEmail(String email){
        User user = null;
        DetachedCriteria dc = DetachedCriteria.forClass(User.class);
        dc.add(Restrictions.eq(Key.EMAIL, email));
        List<Object> list = HibernateUtil.detachedCriteriaReturnLimitedList(dc, 1);
        for(Object o: list) {
            user = (User) o;
            break;
        }
        return user;
    }
    
    public static User verifyAccount(String email, String password){
        User user = getAccountByEmail(email);
        
        //can find user, but need verify password
        if(user != null){
            //get salt and hash it. 
            String userPasswordSalt = user.getPasswordSalt();
            String passwordHash = EncrypCtrl.generateSaltedHash(password, userPasswordSalt);

            //correctly validated the hashed password
            //return user if true else null
            if(passwordHash.equals(user.getPasswordHash())){
                return user;
            }
            else{
                return null;
            }
        }
                
        //hit below directly if email is wrong
        return user;
    }

    public static ArrayList<User> getUsersByIds(ArrayList<Long> ids){
        ArrayList<User> users = new ArrayList<>();
        System.out.println(ids);
        DetachedCriteria dc = DetachedCriteria.forClass(User.class);
        dc.add(Restrictions.in("accountId", ids));
        List<Object> list = HibernateUtil.detachedCriteriaReturnList(dc);
        for(Object o: list){
            users.add((User) o);
        }

        return users;
    }
}
