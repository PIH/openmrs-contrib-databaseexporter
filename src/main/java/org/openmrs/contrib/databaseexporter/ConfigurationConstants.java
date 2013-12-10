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

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.contrib.databaseexporter.filter.DependencyFilter;
import org.openmrs.contrib.databaseexporter.filter.PatientFilter;
import org.openmrs.contrib.databaseexporter.filter.ProviderFilter;
import org.openmrs.contrib.databaseexporter.filter.TableFilter;
import org.openmrs.contrib.databaseexporter.filter.UserFilter;
import org.openmrs.contrib.databaseexporter.transform.RowTransform;
import org.openmrs.contrib.databaseexporter.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationConstants {

	public static String[] RWANDA_FULL_DATA = {
		"rwanda/trimArchiveData"
	};

	public static String[] RWANDA_LARGE_DEIDENTIFIED_DATA = {
		"removeSyncData",
		"rwanda/deidentify",
		"rwanda/trimPatientsLarge",
		"rwanda/trimArchiveData"
	};

	public static String[] RWANDA_SMALL_DEIDENTIFIED_DATA = {
		"removeSyncData",
		"rwanda/deidentify",
		"rwanda/trimPatientsSmall",
		"rwanda/trimArchiveData"
	};

	public static String[] RWANDA_MINIMUM_DATA = {
		"removeSyncData",
		"removeAllPatients",
		"rwanda/deidentify",
		"rwanda/trimUsers",
		"rwanda/trimProviders",
		"rwanda/trimArchiveData"
	};
}
