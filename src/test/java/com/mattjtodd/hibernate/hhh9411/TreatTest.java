/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mattjtodd.hibernate.hhh9411;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Test;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

/**
 * @author Matt Todd
 *
 */
public class TreatTest
{
	
	
	@Test
	public void getOnlyFastDogsFromAnimalRoot()
	{
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hh9411");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();

		// create and persist a grehounf and a dachshund
		Greyhound greyhound = new Greyhound();
		Dachshund dachshund = new Dachshund();
		entityManager.persist(greyhound);
		entityManager.persist(dachshund);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Animal> criteriaQuery = cb.createQuery(Animal.class);
		
		// root the query on animals
		Root<Animal> animal = criteriaQuery.from(Animal.class);
		
		// downcast to add predicates to Dog type
		Root<Dog> dog = cb.treat(animal, Dog.class);

		// only fast dogs
		criteriaQuery.where(cb.isTrue(dog.<Boolean> get("fast")));

		List<Animal> results = entityManager.createQuery(criteriaQuery).getResultList();

		// we should only have a single Greyhound here, not slow long dogs!
		assertEquals(asList(greyhound), results);

		entityTransaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}
}
