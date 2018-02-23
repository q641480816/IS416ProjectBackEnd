package dao;

import model.User.User;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Key;

import java.util.List;

public class UserDao {

    public static User getAccountById(long id) {
        return (User) HibernateUtil.get(User.class, id);
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
}
