package io.github.thecsdev.tcdcommons.api.util.io.repo.github;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.literal;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import io.github.thecsdev.tcdcommons.TCDCommons;
import io.github.thecsdev.tcdcommons.api.util.io.repo.RepositoryInfo;
import net.minecraft.text.Text;

public final class GitHubRepositoryInfo extends RepositoryInfo
{
	// ==================================================
	private String id;
	private String owner_id;
	private Text full_name, description;
	private Text[] topics;
	private boolean has_issues, allow_forking;
	private @Nullable Integer open_issues_count, forks;
	// ==================================================
	GitHubRepositoryInfo() {}
	// ==================================================
	public final @Override String getID() { return this.id; }
	public final @Override String getAuthorUserID() { return this.owner_id; }
	// --------------------------------------------------
	public final @Override Text getName() { return this.full_name; }
	public final @Override Text getDescription() { return this.description; }
	// --------------------------------------------------
	public final @Override Text[] getTags() { return this.topics; }
	// --------------------------------------------------
	public boolean hasIssues() { return this.has_issues; }
	public boolean hasForks() { return this.allow_forking; }
	public final @Override @Nullable Integer getOpenIssuesCount() { return this.open_issues_count; }
	public final @Override Integer getForkCount() { return this.forks; }
	// ==================================================
	public static final GitHubRepositoryInfo getRepositoryInfoSync(String username, String repository)
			throws NullPointerException, URISyntaxException, ClientProtocolException, IOException
	{
		//prepare http get
		final var apiEndpoint = String.format(
				"https://api.github.com/repos/%s/%s",
				Objects.requireNonNull(username),
				Objects.requireNonNull(repository));
		final var httpGet = new HttpGet(new URI(apiEndpoint));
		httpGet.addHeader("User-Agent", TCDCommons.getInstance().userAgent);
		
		final var reqConfig = RequestConfig.custom()
				.setSocketTimeout(3000)
				.setConnectTimeout(3000)
				.setConnectionRequestTimeout(3000)
				.build();
		final var httpClient = HttpClients.custom()
				.setDefaultRequestConfig(reqConfig)
				.build();
		
		//execute
		final var response = httpClient.execute(httpGet);
		final var responseEntity = response.getEntity();
		
		final var responseSL = response.getStatusLine();
		if(responseSL.getStatusCode() != 200)
			throw new HttpResponseException(responseSL.getStatusCode(), responseSL.getReasonPhrase());
		final String content = EntityUtils.toString(responseEntity);
		
		httpClient.close();
		
		//parse JSON
		try
		{
			//parse json
			final JsonObject json = new Gson().fromJson(content, JsonObject.class);
			
			//create, construct, and return repository info
			final var result = new GitHubRepositoryInfo();
			result.id = Integer.toString(json.get("id").getAsInt());
			result.owner_id = Integer.toString(json.get("owner").getAsJsonObject().get("id").getAsInt());
			result.full_name = literal(json.get("full_name").getAsString());
			result.description = literal(json.get("description").getAsString());
			List<Text> topics = new ArrayList<>();
			if(json.has("topics"))
				json.get("topics").getAsJsonArray().forEach(topic -> topics.add(literal(topic.getAsString())));
			result.topics = topics.toArray(new Text[] {});
			result.has_issues = json.get("has_issues").getAsBoolean(); 
			result.allow_forking = json.get("allow_forking").getAsBoolean();
			if(result.has_issues)
				result.open_issues_count = json.get("open_issues_count").getAsInt();
			if(result.allow_forking)
				result.forks = json.get("forks").getAsInt(); 
			return result;
		}
		catch(JsonSyntaxException jse) { throw new IOException("Failed to parse HTTP response JSON.", jse); }
	}
	// ==================================================
}