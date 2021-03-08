package tech.turl.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaDaoHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
