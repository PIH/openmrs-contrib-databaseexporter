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
package org.openmrs.contrib.databaseexporter.util;

/**
 * Encapsulates a database column value
 */
public class AgeRange {

	//***** PROPERTIES *****

	private Integer minAge;
	private Integer maxAge;

	//***** CONSTRUCTORS *****

	public AgeRange() {}

    public AgeRange(Integer minAge, Integer maxAge) {
		this.minAge = minAge;
		this.maxAge = maxAge;
    }

	//***** INSTANCE METHODS *****

	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
}
