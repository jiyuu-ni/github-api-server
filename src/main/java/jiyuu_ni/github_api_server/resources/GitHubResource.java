package jiyuu_ni.github_api_server.resources;

import java.io.IOException;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.CompletionCallback;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "/webhook/github" path)
 */
@Path("/webhook/github")
public class GitHubResource {
	private static int numberOfSuccessResponses = 0;
    private static int numberOfFailures = 0;
    private static Throwable lastException = null;
	
    /**
     * Method handling HTTP POST requests. The returned object is pretty
     * meaningless for a webhook, but the "text/plain" media type will be used.
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public void getIt(@Suspended final AsyncResponse asyncResponse) {
    	// Pulled from https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/async.html#d0e10417
    	// Async is used here to allow invoking a callback when the webhook is finished processing
    	asyncResponse.register(new CompletionCallback() {
            @Override
            public void onComplete(Throwable throwable) {
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    numberOfSuccessResponses++;
					
                // Begin GitHub-related code
				try {
					// UI already has "main" as default branch, but don't
					//    rely solely on the UI
					final String DEFAULT_BRANCH = "main";
					  
					// Build GitHub API using personal access token
					// PAT was chosen because it doesn't expire as quickly
					//    as a JWT or OAuth token
					GitHub github = new GitHubBuilder()
						  .withOAuthToken("NOT_A_REAL_TOKEN", "jiyuu-ni").build();
					github.checkApiUrlValidity();
					  
					/*
					 * Process:
					 * 1.) Gather repository which has just been created by
					 *       pulling the ID out of the webhook payload
					 * 2.) Commit a default "README.md" file to the repository
					 *       in order to create the "main" branch
					 * 3.) Enable required reviews on the "main" branch 
					 */
					
					// The ID would actually be pulled from the webhook payload
					GHRepository currentRepo = github.getRepositoryById(1);
					GHContentUpdateResponse contentResponse =
						  currentRepo.createContent().branch(DEFAULT_BRANCH).content("# This is a default README").path("README.md").message("Create initial README").commit();
					  
					if(contentResponse.getContent().isFile()) {
					  GHBranch defaultBranch = currentRepo.getBranch(DEFAULT_BRANCH);
					  GHBranchProtection branchProtector =
							  defaultBranch.enableProtection().requireReviews().enable();
					}
				} catch (IOException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
				}
					 
                } else {
                    numberOfFailures++;
                    lastException = throwable;
                }
            }
        });
    	
    	new Thread(new Runnable() {
            @Override
            public void run() {
                String result = trivialString();
                asyncResponse.resume(result);
            }
 
            private String trivialString() {
                return "Got it!";
            }
        }).start();
    }
}
