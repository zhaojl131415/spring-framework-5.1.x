/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.env;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * 以Map为配置源的PropertySource
 * {@link PropertySource} that reads keys and values from a {@code Map} object.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertiesPropertySource
 */
public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {

	public MapPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}


	/**
	 * 根据配置源key获取value值
	 * @param name the property to find
	 * @return
	 */
	@Override
	@Nullable
	public Object getProperty(String name) {
		return this.source.get(name);
	}

	/**
	 * 根据配置源去判断key是否存在
	 * @param name the name of the property to find
	 * @return
	 */
	@Override
	public boolean containsProperty(String name) {
		return this.source.containsKey(name);
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.keySet());
	}

}
