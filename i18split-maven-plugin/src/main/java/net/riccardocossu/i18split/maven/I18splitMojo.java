package net.riccardocossu.i18split.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.Map;

import net.riccardocossu.i18split.base.config.ConfigKeys;
import net.riccardocossu.i18split.base.service.Engine;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which executes i18split main engine, configuring it based on its
 * configuration.
 *
 * @goal convert
 *
 * @phase process-resources
 */
public class I18splitMojo extends AbstractMojo {
	/**
	 * Output directory
	 *
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private String outputBasePath;

	/**
	 * Input plugin
	 *
	 * @parameter
	 * @required
	 */
	private String inputPlugin;
	/**
	 * Output plugin
	 *
	 * @parameter
	 * @required
	 */
	private String outputPlugin;

	/**
	 * Specific configuration for plugins, if needed
	 *
	 * @parameter
	 */
	@SuppressWarnings("rawtypes")
	private Map pluginsConfig;

	/**
	 * Base path for input files (without final slash)
	 *
	 * @parameter
	 * @required
	 */
	private String inputBasePath;

	/**
	 * Input encoding
	 *
	 * @parameter expression="UTF-8"
	 * @required
	 */
	private String inputEncoding;

	/**
	 * Output encoding
	 *
	 * @parameter expression="UTF-8"
	 * @required
	 */
	private String outputEncoding;



	public void execute() throws MojoExecutionException {
		File f = new File(outputBasePath);

		if (!f.exists()) {
			f.mkdirs();
		}

		BaseConfiguration conf = new BaseConfiguration();
		conf.addProperty(ConfigKeys.INPUT_DRIVER, inputPlugin);
		conf.addProperty(ConfigKeys.OUTPUT_DRIVER, outputPlugin);
		conf.addProperty(ConfigKeys.OUTPUT_BASE_PATH, outputBasePath);
		conf.addProperty(ConfigKeys.INPUT_BASE_PATH, inputBasePath);
		conf.addProperty(ConfigKeys.INPUT_ENCODING, inputEncoding);
		conf.addProperty(ConfigKeys.OUTPUT_ENCODING, outputEncoding);
		if(pluginsConfig != null) {
			@SuppressWarnings("unchecked")
			MapConfiguration mc = new MapConfiguration(pluginsConfig);
			conf.append(mc);
		}
		Engine eng = new Engine(conf);
		eng.process();

	}
}
