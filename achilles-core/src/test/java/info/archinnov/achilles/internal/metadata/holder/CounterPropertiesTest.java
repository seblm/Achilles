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
package info.archinnov.achilles.internal.metadata.holder;

import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.internal.metadata.holder.CounterProperties;
import info.archinnov.achilles.internal.metadata.holder.PropertyMeta;
import info.archinnov.achilles.internal.metadata.holder.PropertyType;
import info.archinnov.achilles.test.builders.PropertyMetaTestBuilder;
import info.archinnov.achilles.type.ConsistencyLevel;
import info.archinnov.achilles.type.Pair;

import org.junit.Test;

public class CounterPropertiesTest {
	@Test
	public void should_to_string() throws Exception {
		PropertyMeta idMeta = PropertyMetaTestBuilder.completeBean(Void.class, Long.class).field("id")
				.type(PropertyType.SIMPLE).consistencyLevels(Pair.create(ConsistencyLevel.ALL, ConsistencyLevel.ALL))//
				.build();
		CounterProperties props = new CounterProperties("fqcn", idMeta);

		assertThat(props.toString()).isEqualTo("CounterProperties{fqcn=fqcn, idMeta=" + idMeta.toString() + "}");
	}
}
