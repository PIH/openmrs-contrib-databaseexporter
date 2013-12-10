/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.contrib.databaseexporter;

import org.junit.Test;
import org.openmrs.contrib.databaseexporter.util.Util;

import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseExporterTest {

	private boolean exportTestsEnabled = false;

	@Test
	public void shouldExportRwandaConfigurationFiles() throws Exception {
		writeConfiguration(ConfigurationConstants.RWANDA_FULL_DATA, "rwandaFullData.json");
		writeConfiguration(ConfigurationConstants.RWANDA_LARGE_DEIDENTIFIED_DATA, "rwandaLargeDeidData.json");
		writeConfiguration(ConfigurationConstants.RWANDA_SMALL_DEIDENTIFIED_DATA, "rwandaSmallDeidData.json");
		writeConfiguration(ConfigurationConstants.RWANDA_MINIMUM_DATA, "rwandaMinimumData.json");
	}

	@Test
	public void shouldExportMetadataForUnitTests() throws Exception {
		List<String> config = new ArrayList<String>();
		config.addAll(Arrays.asList(ConfigurationConstants.RWANDA_MINIMUM_DATA));
		config.add("rwanda/trimExtraForUnitTests");
		exportForRwanda(config);
	}

	@Test
	public void shouldExportPatientsForUnitTests() throws Exception {
		List<String> config = new ArrayList<String>();
		config.add("rwanda/deidentify");
		config.add("rwanda/keepPatientsForUnitTests");
		exportForRwanda(config);
	}

	@Test
	public void shouldExportMinimumRwandaData() throws Exception {
		List<String> config = new ArrayList<String>();
		config.addAll(Arrays.asList(ConfigurationConstants.RWANDA_MINIMUM_DATA));
		exportForRwanda(config);
	}

	@Test
	public void shouldExportSmallPatientsRwandaData() throws Exception {
		List<String> config = new ArrayList<String>();
		config.addAll(Arrays.asList(ConfigurationConstants.RWANDA_SMALL_DEIDENTIFIED_DATA));
		exportForRwanda(config);
	}

	@Test
	public void shouldExportMaximumRwandaData() throws Exception {
		List<String> config = new ArrayList<String>();
		config.addAll(Arrays.asList(ConfigurationConstants.RWANDA_FULL_DATA));
		exportForRwanda(config);
	}

	@Test
	public void shouldExportDeidentifiedMirebalaisData() throws Exception {
		if (exportTestsEnabled) {
			List<String> config = new ArrayList<String>();
			config.add("mirebalais/deidentify");
			config.add("-localDbName=openmrs_mirebalais");
			config.add("-user=openmrs");
			config.add("-password=openmrs");
			config.add("-logSql=false");
			config.add("-targetDirectory=" + System.getProperty("java.io.tmpdir"));
			DatabaseExporter.main(config.toArray(new String[] {}));
		}
	}

	public void exportForRwanda(List<String> config) throws Exception {
		if (exportTestsEnabled) {
			config.add("-localDbName=openmrs_rwink");
			config.add("-user=openmrs");
			config.add("-password=openmrs");
			config.add("-logSql=false");
			config.add("-targetDirectory=" + System.getProperty("java.io.tmpdir"));
			DatabaseExporter.main(config.toArray(new String[] {}));
		}
	}

	public void writeConfiguration(String[] configArguments, String fileName) throws Exception {
		List<String> resourceNames = new ArrayList<String>();
		resourceNames.add("defaults");
		resourceNames.addAll(Arrays.asList(configArguments));
		Configuration config = Util.loadConfiguration(resourceNames);
		Util.writeConfiguration(config, new File(System.getProperty("java.io.tmpdir"), fileName));
	}
}
