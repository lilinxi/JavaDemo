import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Idea 中 DemoBean.hbm.xml 与数据库的连接：View->Tool Windows->DataSource->Add MySQL->Test; Persistence->Assign DataSource
 * 新旧版 Hibernate 创建 SessionFactory 的不同：addClass(DemoBean.class)
 * 事务隔离级别：控制并发访问：https://blog.csdn.net/partner4java/article/details/6926546
 * hibernate.connection.isolation = 4
 * 1-读取未提交隔离性
 * 2-读取提交隔离性
 * 3-可重复读取隔离性
 * 4-可序列化隔离性
 * 主键生成策略：https://www.cnblogs.com/hoobey/p/5508992.html
 */
public class HibernateDemo {
    private SessionFactory sessionFactory;

    public static void main(String[] args) {
        HibernateDemo demo = new HibernateDemo();
        demo.run();
    }

    public void run() {
        // 构建sessionFactory
        System.out.println("====================sessionFactory=======================");
        sessionFactory = buildSessionFactory();
        // 添加/修改数据
        System.out.println("====================添加/修改数据=======================");
        DemoBean bean = new DemoBean();
        bean.setId(1);
        bean.setName("姓名11");
        bean.setSex("男");
        bean.setCreatetime(new Date());
        DemoBean bean2 = new DemoBean();
        bean2.setId(2);
        bean2.setName("姓名22");
        bean2.setSex("男");
        bean2.setCreatetime(new Date());
        DemoBean bean3 = new DemoBean();
        bean3.setId(3);
        bean3.setName("姓名33");
        bean3.setSex("男");
        bean3.setCreatetime(new Date());
        save(bean);
        save(bean2);
        save(bean3);
        // 查询数据（一条）
        System.out.println("====================查询数据（一条）=======================");
        DemoBean findBean = find("where id=2");
        System.out.println(findBean.getId());
        System.out.println(findBean.getName());
        System.out.println(findBean.getSex());
        System.out.println(findBean.getCreatetime());
        // 删除数据
        System.out.println("====================删除数据=======================");
        DemoBean delBean = find("where id=3");
        delete(delBean);
        // 查询数据（列表）
        System.out.println("====================查询数据（列表）=======================");
        List<DemoBean> list = list("where 1=1");
        for (DemoBean demoBean : list) {
            System.out.println("list=================");
            System.out.println(demoBean.getId());
            System.out.println(demoBean.getName());
            System.out.println(demoBean.getSex());
            System.out.println(demoBean.getCreatetime());
        }
        System.out.println("========================释放资源=============================");
        sessionFactory.close();
    }

    /**
     * 查找数据（一条）
     *
     * @param where
     * @return
     */
    public DemoBean find(String where) {
        // 获取操作session
        Session session = sessionFactory.openSession();
        // 开始事务
        Transaction tr = session.beginTransaction();
        DemoBean ret = null;
        try {
            String hql = "from DemoBean " + where;
            org.hibernate.query.Query query = session.createQuery(hql);
            ret = (DemoBean) query.uniqueResult();
            // 提交事务
            tr.commit();
        } catch (Exception e) {
            // 回滚事务
            tr.rollback();
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 查询数据（列表）
     *
     * @param where
     * @return
     */
    public List<DemoBean> list(String where) {
        // 获取操作session
        Session session = sessionFactory.openSession();
        // 开始事务
        Transaction tr = session.beginTransaction();
        List<DemoBean> list = Collections.EMPTY_LIST;
        try {
            String hql = "from DemoBean " + where;
            org.hibernate.query.Query query = session.createQuery(hql);
            list = query.list();
            // 提交事务
            tr.commit();
        } catch (Exception e) {
            // 回滚事务
            tr.rollback();
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 添加/修改数据
     *
     * @param bean
     */
    public void save(DemoBean bean) {
        // 获取操作session
        Session session = sessionFactory.openSession();
        // 开始事务
        session.beginTransaction();
        try {
            session.saveOrUpdate(bean);
            // 提交事务
            session.getTransaction().commit();
            System.out.println("保存数据：");
            System.out.println(bean.getId());
            System.out.println(bean.getName());
            System.out.println(bean.getSex());
            System.out.println(bean.getCreatetime());
        } catch (Throwable e) {
            // 回滚事务
            session.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    /**
     * 删除数据
     *
     * @param bean
     */
    public void delete(DemoBean bean) {
        // 获取操作session
        Session session = sessionFactory.openSession();
        // 开始事务
        session.beginTransaction();
        try {
            session.delete(bean);
            // 提交事务
            session.getTransaction().commit();
            System.out.println("删除数据：");
            System.out.println(bean.getId());
            System.out.println(bean.getName());
            System.out.println(bean.getSex());
            System.out.println(bean.getCreatetime());
        } catch (Throwable e) {
            // 回滚事务
            session.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    /**
     * 构建SessionFactory
     *
     * @return
     */
    public SessionFactory buildSessionFactory() {
//        Hibernate会自己主动在当前的CLASSPATH中搜寻hibernate.cfg.xml文件并将其读取到内存作为后继操作的基础配置。
        Configuration cfg = new Configuration().configure();
        cfg.addClass(DemoBean.class);
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
        ServiceRegistry service = ssrb.build();
//        SessionFactory一旦构建完成，即被赋予特定的配置信息。也就是说，之后config的不论什么变更将不会影响到已经创建的SessionFactory实例（SessionFactory）。假设须要使用基于修改后的config实例的SessionFactory，须要从config又一次构建一个SessionFactory实例。
        SessionFactory factory = cfg.buildSessionFactory(service);
        return factory;
    }

}
