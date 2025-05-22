package teledon.persistence.hibernate;

import org.hibernate.Session;
import teledon.model.Volunteer;
import teledon.persistence.interfaces.IVolunteerRepository;
import teledon.persistence.utils.HibernateUtils;

import java.util.Objects;

public class VolunteerHibernateRepository implements IVolunteerRepository {
    public Volunteer findOne(Integer id){
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Volunteer where id=:id_volunteer ", Volunteer.class)
                    .setParameter("id_volunteer", id)
                    .getSingleResultOrNull();
        }
    }
    public Iterable<Volunteer> findAll(){
        try( Session session=HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer ", Volunteer.class).getResultList();
        }
    }
    public Volunteer save(Volunteer entity){
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(entity));
        return entity;
    }
    public Volunteer delete(Integer id){
        Volunteer volunteer = findOne(id);
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Volunteer v=session.createQuery("from Volunteer where id=?1",Volunteer.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("In delete am gasit voluntarul "+v);
            if (v!=null) {
                session.remove(v);
                session.flush();
            }
        });
        return volunteer;
    }
    public Volunteer update(Volunteer entity){
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (!Objects.isNull(session.find(Volunteer.class, entity.getId()))) {
                System.out.println("In update, am gasit voluntarul cu id-ul "+entity.getId());
                session.merge(entity);
                session.flush();
            }
        });
        return entity;
    }
    public Volunteer findByUsername(String username){
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Volunteer where username=:username ", Volunteer.class)
                    .setParameter("username", username)
                    .getSingleResultOrNull();
        }
    }
}
