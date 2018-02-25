package util;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class HibernateUtil {

    /**
     * @author 
     */
    // configuration
    private static final SessionFactory sessionFactory;

    static {
        System.out.println("Come lah");
        try {
            // create the SessionFactory from hibernate.cfg.xml
            Configuration config = new Configuration();
            // config.configure("/hibernate.cfg.xml");
            sessionFactory = config.configure("/hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            // make sure you log the exception, as it might be swallowed
            ex.printStackTrace();
            System.err.println("Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(ex);
        }
    }

    // methods
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object get(Class aClass, long id) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Object obj = session.get(aClass, id);
        session.close();
        return obj;
    }   

    public static void save(Object object) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(object);
        session.getTransaction().commit();
        session.close();
    }

    public static void update(Object modifyObj) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(modifyObj);
        session.getTransaction().commit();
        session.close();
    }

    public static void delete(Object obj) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(obj);
        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public static List<Object> detachedCriteriaReturnList(DetachedCriteria dc) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = dc.getExecutableCriteria(session);
        List<Object> list = criteria.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> detachedCriteriaReturnLimitedList(DetachedCriteria dc, int max) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = dc.getExecutableCriteria(session).setMaxResults(max);
        List<Object> list = criteria.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> detachedCriteriaReturnObjectArrayList(DetachedCriteria dc) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = dc.getExecutableCriteria(session);
        List<Object[]> list = criteria.list();
        session.getTransaction().commit();
        session.close();
        return list;
    }
}
