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
package org.springframework.social.connect;

import java.io.Serializable;
import java.util.Collection;


/**
 * Models the provider of a service that local member accounts may connect to and invoke.
 * Exposes service provider metadata along with connection management operations that allow for account connections to be established.
 * Also acts as a factory for a strongly-typed service API (S).
 * Once a connection with this provider is established, the service API can be used by the application to invoke the service on behalf of the member.
 * @author Keith Donald
 * @param <S> The service API hosted by this service provider.
 */
public interface ServiceProvider<S> {

	// provider meta-data
	
	/**
	 * The unique name or id of the service provider e.g. twitter.
	 * Unique across all service providers.
	 */
	String getName();
	
	/**
	 * A label suitable for display in a UI, typically used to inform the user which service providers he or she has connected with / may connect with. e.g. Twitter.
	 */
	String getDisplayName();

	/**
	 * The key used to identify the local application with the remote service provider.
	 * Used when establishing an account connection with the service provider.
	 * Available as a public property to support client code that wishes to manage the service connection process itself, for example, in JavaScript.
	 * The term "API key" is derived from the OAuth 2 specification. 
	 */
	String getApiKey();
	
	/**
	 * An alternate identifier for the local application in the remote service provider's system.
	 * May be null of no such alternate identifier exists.
	 * Used by ServiceProvider&lt;FacebookOperations&gt; to support "Like" functionality.
	 * @return an alternate app id, or null if no alternate id exists (null is the typical case, as the {@link #getApiKey()} is the primary means of consumer identification)
	 */
	Long getAppId();

	// connection management
	
	/**
	 * Begin the account connection process by fetching a new request token from this service provider.
	 * The new token should be stored in the member's session up until the authorization callback is made and it's time to {@link #connect(Long, AuthorizedRequestToken) connect}.
	 * @param callbackUrl the URL the provider should redirect to after the member authorizes the connection (may be null for OAuth 1.0-based service providers) 
	 */
	OAuthToken fetchNewRequestToken(String callbackUrl);

	/**
	 * Construct the URL to redirect the member to for OAuth 1 connection
	 * authorization.
	 * 
	 * @param requestToken
	 *            the request token value, to be encoded in the authorize URL
	 * @return the absolute authorize URL to redirect the member to for
	 *         authorization
	 */
	String buildAuthorizeUrl(String requestToken);

	/**
	 * Connects a member account to this service provider. Called after the user
	 * authorizes the connection at the {@link #buildAuthorizeUrl(String)
	 * authorizeUrl} and the service provider calls us back. Internally,
	 * exchanges the authorized request token for an access token, then stores
	 * the awarded access token with the member account. This access token
	 * identifies the connection between the member account and this service
	 * provider.
	 * <p>
	 * This method completes the OAuth-based account connection process.
	 * {@link #getServiceOperations(Long)} may now be called to get and invoke
	 * the service provider's API. The requestToken required during the
	 * connection handshake is no longer valid and cannot be reused.
	 * 
	 * @param requestToken
	 *            the OAuth request token that was authorized by the member.
	 */
	void connect(Serializable accountId, AuthorizedRequestToken requestToken);

	void connect(Serializable accountId, String redirectUri, String code);

	/**
	 * Records an existing connection between a member account and this service provider.
	 * Use when the connection process happens outside of the control of this package; for example, in JavaScript.
	 * @param accessToken the access token that was granted as a result of the connection
	 * @param providerAccountId the id of the user in the provider's system; may be an assigned number or a user-selected screen name.
	 */
	void addConnection(Serializable accountId, String accessToken, String providerAccountId);

	/**
	 * Returns true if the member account is connected to this provider, false otherwise.
	 */
	boolean isConnected(Serializable accountId);

	/**
	 * <p>
	 * Gets a handle to the API offered by this service provider. This API may
	 * be used by the application to invoke the service on behalf of a member.
	 * </p>
	 * 
	 * <p>
	 * This method assumes that the user has established a connection with the
	 * provider via the connect() method and will create the operations instance
	 * based on that previously created connection. In the case where the user
	 * has established multiple connections with the provider, the first one
	 * found will be used to create the service operations instance.
	 * </p>
	 */
	S getServiceOperations(Serializable accountId);

	/**
	 * <p>
	 * Gets a handle to the API offered by this service provider for a given
	 * access token. This API may be used by the application to invoke the
	 * service on behalf of a member.
	 * </p>
	 * 
	 * <p>
	 * This method does not assume that a connection has been previously made
	 * through the connect() method.
	 * </p>
	 * 
	 * @param accessToken
	 *            An access token through which the service operations will be
	 *            granted authority to the provider.
	 */
	S getServiceOperations(OAuthToken accessToken);

	S getServiceOperations(Serializable accountId, String providerAccountId);

	/**
	 * Retrieves all connections that the user has made with the provider.
	 * Commonly, this collection would contain a single entry, but it is
	 * possible that the user may have multiple profiles on a provider and has
	 * created connections for all of them.
	 * 
	 * @return a collection of {@link AccountConnection}s that the user has
	 *         established with the provider.
	 */
	Collection<AccountConnection> getConnections(Serializable accountId);

	/**
	 * Severs all connections between the member account and this service
	 * provider. Has no effect if no connection is established to begin with.
	 */
	void disconnect(Serializable accountId);
	
	/**
	 * Severs a specific connection between the member account and this service
	 * provider.
	 */
	void disconnect(Serializable accountId, String providerAccountId);

	// additional finders

	/**
	 * The id of the member in the provider's system.
	 * May be an assigned internal identifier, such as a sequence number, or a user-selected screen name.
	 * Generally unique across accounts registered with this provider.
	 */
	String getProviderAccountId(Serializable accountId);

	OAuthVersion getOAuthVersion();
}