/*
 * Copyright (C) 2012-2014 DuyHai DOAN
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package info.archinnov.achilles.test.integration.tests;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Rule;
import org.junit.Test;
import info.archinnov.achilles.persistence.PersistenceManager;
import info.archinnov.achilles.junit.AchillesTestResource.Steps;
import info.archinnov.achilles.test.integration.AchillesInternalCQLResource;
import info.archinnov.achilles.test.integration.entity.ValuelessClusteredEntity;
import info.archinnov.achilles.test.integration.entity.ValuelessClusteredEntity.CompoundKey;
import info.archinnov.achilles.type.OptionsBuilder;

public class ValuelessClusteredEntityIT {

	@Rule
	public AchillesInternalCQLResource resource = new AchillesInternalCQLResource(Steps.AFTER_TEST,
			"ValuelessClusteredEntity");

	private PersistenceManager manager = resource.getPersistenceManager();

	@Test
	public void should_persist_and_find() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name = "name";
		CompoundKey compoundKey = new CompoundKey(id, name);
		ValuelessClusteredEntity entity = new ValuelessClusteredEntity(compoundKey);

		manager.insert(entity);

		ValuelessClusteredEntity found = manager.find(ValuelessClusteredEntity.class, compoundKey);

		assertThat(found).isNotNull();
	}

	@Test
	public void should_persist_and_get_proxy() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name = "name";
		CompoundKey compoundKey = new CompoundKey(id, name);
		ValuelessClusteredEntity entity = new ValuelessClusteredEntity(compoundKey);

		manager.insert(entity);

		ValuelessClusteredEntity found = manager.getProxy(ValuelessClusteredEntity.class, compoundKey);

		assertThat(found).isNotNull();
	}

	@Test
	public void should_persist_with_ttl() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name = "name";
		CompoundKey compoundKey = new CompoundKey(id, name);
		ValuelessClusteredEntity entity = new ValuelessClusteredEntity(compoundKey);

		manager.insert(entity, OptionsBuilder.withTtl(1));

		Thread.sleep(1000);

		assertThat(manager.find(ValuelessClusteredEntity.class, compoundKey)).isNull();
	}

	@Test
	public void should_find_by_slice_query() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name1 = "name1";
		String name2 = "name2";
		String name3 = "name3";
		String name4 = "name4";
		String name5 = "name5";

		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name1)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name2)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name3)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name4)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name5)));

		List<ValuelessClusteredEntity> result = manager.sliceQuery(ValuelessClusteredEntity.class)
                .forSelect()
                .withPartitionComponents(id)
				.fromClusterings(name2)
                .toClusterings(name5)
                .fromExclusiveToInclusiveBounds()
				.orderByDescending()
                .limit(3)
                .get();

		assertThat(result).hasSize(3);
		assertThat(result.get(0).getId().getName()).isEqualTo(name5);
		assertThat(result.get(1).getId().getName()).isEqualTo(name4);
		assertThat(result.get(2).getId().getName()).isEqualTo(name3);
	}

	@Test
	public void should_iterate_by_slice_query() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name1 = "name1";
		String name2 = "name2";
		String name3 = "name3";
		String name4 = "name4";
		String name5 = "name5";

		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name1)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name2)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name3)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name4)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name5)));

		Iterator<ValuelessClusteredEntity> iterator = manager.sliceQuery(ValuelessClusteredEntity.class)
                .forIteration()
				.withPartitionComponents(id)
                .fromClusterings(name2)
                .toClusterings(name5)
				.fromExclusiveToInclusiveBounds()
                .orderByDescending()
                .iterator();

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next().getId().getName()).isEqualTo(name5);
		assertThat(iterator.next().getId().getName()).isEqualTo(name4);
		assertThat(iterator.next().getId().getName()).isEqualTo(name3);
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void should_delete_by_slice_query() throws Exception {
		Long id = RandomUtils.nextLong(0,Long.MAX_VALUE);
		String name1 = "name1";
		String name2 = "name2";
		String name3 = "name3";

		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name1)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name2)));
		manager.insert(new ValuelessClusteredEntity(new CompoundKey(id, name3)));

		manager.sliceQuery(ValuelessClusteredEntity.class)
                .forDelete()
                .withPartitionComponents(id)
				.deleteMatching(name2);

		List<ValuelessClusteredEntity> result = manager.sliceQuery(ValuelessClusteredEntity.class)
                .forSelect()
                .withPartitionComponents(id)
				.get();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getId().getName()).isEqualTo(name1);
		assertThat(result.get(1).getId().getName()).isEqualTo(name3);
	}
}
