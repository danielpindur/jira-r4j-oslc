/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Filter;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.FilterJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.SearchResultJsonParser;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.Promise;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * Extended version of the AsynchronousSearchRestClient from Jira API SDK.
 */
public class SearchRestClientExtended extends AsynchronousSearchRestClient implements Closeable {
    private static final Function<IssueRestClient.Expandos, String> EXPANDO_TO_PARAM = new Function<IssueRestClient.Expandos, String>() {
        public String apply(IssueRestClient.Expandos from) {
            return from.name().toLowerCase();
        }
    };

    private final SearchResultJsonParser searchResultJsonParser = new SearchResultJsonParser();
    private final FilterJsonParser filterJsonParser = new FilterJsonParser();
    private final GenericJsonArrayParser<Filter> filtersParser = GenericJsonArrayParser.create(new FilterJsonParser());
    private final URI searchUri;
    private final DisposableHttpClient client;

    public SearchRestClientExtended(URI baseUri, DisposableHttpClient asyncHttpClient) {
        super(baseUri, asyncHttpClient);

        this.searchUri = UriBuilder.fromUri(baseUri).path("search").build(new Object[0]);
        this.client = asyncHttpClient;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    /**
     * Search for issues using JQL (Jira Query Language).
     * 
     * @param jql JQL query string
     * @param maxResults maximum number of results to return
     * @param startAt index of the first issue to return 
     * @param fields set of fields to return
     * 
     * @return promise of search result
     */
    @Override
    public Promise<SearchResult> searchJql(@Nullable String jql, @Nullable Integer maxResults, @Nullable Integer startAt, @Nullable Set<String> fields) {
        Iterable<String> expandosValues = Iterables.transform(ImmutableList.of(IssueRestClient.Expandos.SCHEMA, IssueRestClient.Expandos.NAMES, IssueRestClient.Expandos.CHANGELOG), EXPANDO_TO_PARAM);
        String notNullJql = StringUtils.defaultString(jql);
        return notNullJql.length() > 500 ? this.searchJqlImplPost(maxResults, startAt, expandosValues, notNullJql, fields) : this.searchJqlImplGet(maxResults, startAt, expandosValues, notNullJql, fields);
    }

    /**
     * Search for issues using JQL (Jira Query Language).
     * 
     * @param jql JQL query string
     * 
     * @return promise of search result
     */
    @Override
    public Promise<SearchResult> searchJql(@Nullable String jql) {
        return this.searchJql(jql, (Integer)null, (Integer)null, (Set)null);
    }

    /**
     * Search for issues using JQL (Jira Query Language).
     * 
     * @param jql JQL query string
     * @param maxResults maximum number of results to return
     * @param startAt index of the first issue to return
     * 
     * @return promise of search result
     */
    public Promise<SearchResult> searchJql(@Nullable String jql, @Nullable Integer maxResults, @Nullable Integer startAt) {
        return this.searchJql(jql, maxResults, startAt, (Set)null);
    }

    /**
     * Search for issues using JQL (Jira Query Language) using the GET version.
     * 
     * @param jql JQL query string
     * @param maxResults maximum number of results to return
     * @param startAt index of the first issue to return
     * @param expandosValues set of expandos to return
     * @param fields set of fields to return
     * 
     * @return promise of search result
     */
    private Promise<SearchResult> searchJqlImplGet(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.searchUri).queryParam("jql", new Object[]{jql}).queryParam("expand", new Object[]{Joiner.on(",").join(expandosValues)});
        if (fields != null) {
            uriBuilder.queryParam("fields", new Object[]{Joiner.on(",").join(fields)});
        }

        this.addOptionalQueryParam(uriBuilder, "maxResults", maxResults);
        this.addOptionalQueryParam(uriBuilder, "startAt", startAt);
        return this.getAndParse(uriBuilder.build(new Object[0]), this.searchResultJsonParser);
    }

    /**
     * Add a query parameter to the URI builder if the value is not null.
     * 
     * @param uriBuilder URI builder
     * @param key query parameter key
     * @param values query parameter values
     */
    private void addOptionalQueryParam(UriBuilder uriBuilder, String key, Object... values) {
        if (values != null && values.length > 0 && values[0] != null) {
            uriBuilder.queryParam(key, values);
        }

    }

    /**
     * Search for issues using JQL (Jira Query Language) using the POST version.
     * 
     * @param maxResults maximum number of results to return
     * @param startAt index of the first issue to return
     * @param expandosValues set of expandos to return
     * @param jql JQL query string
     * @param fields set of fields to return
     * 
     * @return promise of search result
     */
    private Promise<SearchResult> searchJqlImplPost(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
        JSONObject postEntity = new JSONObject();

        try {
            postEntity.put("jql", jql).put("expand", ImmutableList.copyOf(expandosValues)).putOpt("startAt", startAt).putOpt("maxResults", maxResults);
            if (fields != null) {
                postEntity.put("fields", fields);
            }
        } catch (JSONException var8) {
            throw new RestClientException(var8);
        }

        return this.postAndParse(this.searchUri, postEntity, this.searchResultJsonParser);
    }
}
