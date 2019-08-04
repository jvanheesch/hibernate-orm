package org.hibernate.test.annotations.embedded;

import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

@TestForIssue(jiraKey = "HHH-13537")
public class AccessTypeTest extends BaseEntityManagerFunctionalTestCase {

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{MyEntity.class};
    }

    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, entityManager -> {
            MyEmbeddable myEmbeddable = new MyEmbeddable();
            myEmbeddable.description = "desc";

            MyEntity myEntity = new MyEntity();
            Set<MyEmbeddable> myEmbeddables = Collections.singleton(myEmbeddable);
            myEntity.myEmbeddables = myEmbeddables;

            entityManager.persist(myEntity);
        });
    }

    @Entity
    public class MyEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Access(AccessType.PROPERTY)
        private Long id;

        @ElementCollection
        @CollectionTable(
                name = "MY_EMBEDDABLE",
                joinColumns = @JoinColumn(name = "MY_ENTITY_ID")
        )
        private Set<MyEmbeddable> myEmbeddables;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            System.out.println("MyEntity.setId");
            this.id = id;
        }

        public String getTest() {
            System.out.println("MyEntity.getTest");
            return "";
        }
    }

    // "solves" the issue
    // @Access(AccessType.FIELD)
    @Embeddable
    public static class MyEmbeddable {
        @Column(name = "DESCRIPTION")
        private String description;

        // also "solves" the issue
//        public String getDescription() {
//            return description;
//        }
//
//        public void setDescription(String description) {
//            this.description = description;
//        }
    }
}
