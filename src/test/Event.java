/**
 * 
 */
package test;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.idch.util.PersistenceUtil;


/**
 * @author Neal Audenaert
 */
@Entity
@Table( name = "EVENTS" )
public class Event {
    
    private Long id;
    private String title;
    private Date date;
    
    @Id
    @GeneratedValue()
    public Long getId() {
        return id;
    }
    
    @SuppressWarnings("unused")
    private void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EVENT_DATE")
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date d) { 
        this.date = d;
    }
    
    @SuppressWarnings({"unchecked"})
    public static void main(String [] args) {
        EntityManagerFactory emf = PersistenceUtil.getEMFactory("org.idch.afed");
        
        String title = "New event at: ";
        Event e = null;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        try {
            e = new Event();
            e.date = new Date();
            title = title + e.date.getTime();
            e.title = title;
            em.persist(e);
            
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }


        // try to list the events
        tx = em.getTransaction();
        tx.begin();
        Session session = (Session) em.getDelegate();
        try {
            List<Event> events = (List<Event>)session.createCriteria(Event.class)
                .list();

            if ((events != null) && events.size() > 0) {
                System.out.println("Number of Events: " + events.size());
                for (Event event : events) {
                    System.out.println(event.getId() + ": " + event.getTitle());
                }
            } else { 
                System.out.println("No Events Found");
            }

            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }

        em.close();


        System.out.println("done.");
        // return (e != null) ? e : new ArrayList<Event>();
    }
}
