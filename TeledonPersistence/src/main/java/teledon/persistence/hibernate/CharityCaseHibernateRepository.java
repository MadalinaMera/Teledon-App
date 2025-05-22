package teledon.persistence.hibernate;

import org.hibernate.Session;
import teledon.model.CharityCase;
import teledon.persistence.interfaces.ICharityCaseRepository;
import teledon.persistence.utils.HibernateUtils;

import java.util.Objects;

public class CharityCaseHibernateRepository implements ICharityCaseRepository {
    public CharityCase findOne(Integer id){
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from CharityCase where id=:id_case ", CharityCase.class)
                    .setParameter("id_case", id)
                    .getSingleResultOrNull();
        }
    }
    public Iterable<CharityCase> findAll(){
        try( Session session= HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from CharityCase ", CharityCase.class).getResultList();
        }
    }
    public CharityCase save(CharityCase entity){
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return entity;
    }
    public CharityCase delete(Integer id){
        CharityCase chrtCase = findOne(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            CharityCase charityCase=session.createQuery("from CharityCase where id=?1",CharityCase.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("In delete am gasit mesajul "+charityCase);
            if (charityCase!=null) {
                session.remove(charityCase);
                session.flush();
            }
        });
        return chrtCase;
    }
    public CharityCase update(CharityCase entity){
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(CharityCase.class, entity.getId()))) {
                System.out.println("In update, am gasit cazul caritabil cu id-ul "+entity.getId());
                session.merge(entity);
                session.flush();
            }
        });
        return entity;
    }
    public CharityCase findByName(String name){
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from CharityCase where name=:name ", CharityCase.class)
                    .setParameter("name", name)
                    .getSingleResultOrNull();
        }
    }
}
