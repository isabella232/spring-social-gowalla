/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.gowalla;

import java.util.List;

/**
 * Interface specifying a basic set of operations for interacting with Gowalla.
 * Implemented by {@link GowallaTemplate}. Not often used directly, but a useful
 * option to enhance testability, as it can easily be mocked or stubbed.
 * 
 * Many of the methods contained in this interface require OAuth authentication
 * with Gowalla. When a method's description speaks of the "current user", it is
 * referring to the user for whom the access token has been issued.
 * 
 * @author Craig Walls
 */
public interface GowallaOperations {

	/**
	 * Retrieves the user's Gowalla profile ID.
	 * 
	 * @return the user's Gowalla profile ID.
	 */
	String getProfileId();

	/**
	 * Retrieves a URL to the user's public profile page.
	 * 
	 * @return a URL to the user's public profile page.
	 */
	String getProfileUrl();

	List<Checkin> getTopCheckins(String userId);

}