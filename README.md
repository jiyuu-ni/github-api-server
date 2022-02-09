# Automating the security of new branches in organizational repositories

This project demonstrates one path to securing your organization and its repositories using automation via GitHub APIs and REST calls.

## What Would This Program Do?
The very short version? If you have set everything up right, this program will automatically require reviews before any PR can be merged into the `main` branch of every new repository you create.
<br><br>The more in-depth explanation: This program is a basic REST server, capable of listening for events which GitHub emits via [GitHub Webhooks](https://docs.github.com/en/developers/webhooks-and-events/webhooks/about-webhooks).
<br>Once this basic server detects the webhook which indicates a repository has been created, it is able to create a `main` branch for that same repository.
<br>With a branch in place, all future pull requests filed against that branch will then be required to undergo a review. The ability to restrict such things is part of the greater ["Branch Protection"](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches) feature set offered by GitHub.
<br><br>The authentication of this program is tied to a user via their Personal Access Token. Because of this, it is possible to automatically create the first [GitHub Issue](https://docs.github.com/en/issues) in the `main` branch of any new repository to describe what has automatically occurred. This first issue will also use the ["mention" functionality](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax#mentioning-people-and-teams) of GitHub to indicate which user the program was authenticated as when the automation process ran.

## What Will I Need?
- Java 8 or higher
- Maven 3.x (any version in the series should work)
- A GitHub organization [[Instructions]](https://docs.github.com/en/organizations/collaborating-with-groups-in-organizations/creating-a-new-organization-from-scratch)
- Create a GitHub app [[Instructions]](https://docs.github.com/en/developers/apps/building-github-apps/creating-a-github-app)
- Capability to expose a port to the Internet [[Instructions]](https://docs.github.com/en/developers/webhooks-and-events/webhooks/configuring-your-server-to-receive-payloads)
- GitHub Personal Access Token [[Instructions]](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

## What Libraries Are Used?
- [Jersey](https://eclipse-ee4j.github.io/jersey/) is used for JAX-RS support and the Grizzly web container
- [GitHub-API](https://github-api.kohsuke.org/) is used for an object-oriented Java wrapper around most GitHub APIs

## How Do I Run It?
- Clone this repository to your local environment
- Alter line 29 in `src/main/java/jiyuu_ni/github_api_server/resources/GitHubResource.java` to input your Personal Access Token
- Alter line 30 in the same file to input your organization name
- From a local console inside the cloned directory, run the command `mvn clean package`
- From the console again, run the command `java -jar target/github-api-server.jar`
- The server will now listen on `http://localhost:56565` until it is stopped

## What If...?
- "...I want it to run on a different protocol/host/port?"
  - That's fine, you can do that by altering line 16 in `src/main/java/jiyuu_ni/github_api_server/Main.java`<br><br>
- "...I can't directly expose my machine to the internet?"
  - Many companies have a standard approach for exposing services, such as reverse proxies or cloud containers. It will be best to ask within your organization to determine how this can be done while complying with your company's policies.
