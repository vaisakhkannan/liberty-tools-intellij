# How to get Slack notifications for LSP4IJ PRs

This `pr-slack-notification.yaml` GitHub Actions workflow automates the process of fetching and notifying about `LSP4IJ` pull requests (PRs) using a cron job.

1. #### Trigger
   - The workflow is triggered manually via **workflow_dispatch** or on a **schedule** (cron) at `15:30 UTC` on weekdays (Monday to Friday).
2. #### Job Setup
   - Runs on an `ubuntu-latest` environment. 
   - Sets up Environment Variables:
     - **REPO**: The repository name where the workflow runs. 
     - **BRANCH**: The branch of the repository to be used for running the cron job and clearing the cache, which is currently the `main` branch. 
     - **GH_TOKEN**: A GitHub token used for authenticating API requests. 
     - **API_URL**: The URL to fetch pull requests from the [GitHub API](https://api.github.com/repos/redhat-developer/lsp4ij/pulls).
     - **SLACK_WEBHOOK_URL**: Specifying secret→  The webhook URL for sending notifications to Slack. 
     - **WORKFLOW_BUILDER_WEBHOOK**: Specifying secret→ The webhook URL for triggering Slack Workflow Builder when at least one PR is present to display. 
     - **NO_PR_WORKFLOW_BUILDER_WEBHOOK**: Specifying secret→ The webhook URL used when no pull requests are present
3. #### Steps
   - **Checkout Repository**: Checks out the code from the repository. 
   - **Fetch Recent Cache**: Uses the GitHub CLI to list and select the most recent cache key or generates a new one if none exists. 
   - **Restore Cache**: Attempts to restore cache from the selected cache key. 
   - **Ensure Cache Directory Exists**: Checks if the cache directory exists and if the cache file is present. 
   - **Fetch Open Pull Requests**: Fetches details of open PRs using the GitHub API and compares with cached PRs to identify new or updated PRs. Saves the new or updated PRs to environment variables for the retrieval of values in the remaining steps. 
   - **Fetch Closed Pull Requests**: Retrieves closed PRs since the last run. Uses a timestamp to determine which PRs were closed since the last check. 
   - **Send Slack Notification**: Sends a Slack notification with details of open/new/updated PRs and closed PRs, if there are any. Builds the notification payload based on the presence of open and closed PRs. 
   - **Save Current Timestamp**: Saves the current timestamp to a file for future reference. 
   - **Verify Cache Save**: Verifies and displays the saved cache content. 
   - **Save Cache**: Saves the cache with a new key for future use. 
   - **Cleanup Restored Cache Key**: Deletes the cache key used in the current run to manage cache size and prevent overflow. 
   - **Slack Notification for Response Message**:  This job sends a Slack notification with a predefined message indicating a workflow action, selecting the appropriate webhook URL based on whether there are new or closed pull requests; if no PRs are found, it uses a specific webhook for such cases. Both webhook URLs are created using the Slack Webhook Builder. The main reason for using the Slack Webhook Builder is to allow changes to the Slack message text at the Slack level and not depend on the code in the repository.

4. #### Permissions
   - The workflow has write access to actions to manage cache keys. If additional permissions are needed, a Personal Access Token (PAT) might be required.

The workflow helps automate monitoring and notifying about PRs, ensuring updates are communicated efficiently while managing cache effectively.

> Note: The `secrets.GITHUB_TOKEN` is automatically provided by GitHub Actions for each workflow run. We don't need to manually create or manage this token.

```java
   permissions:
     actions: write
```

The lines above mean the workflow is granting `write` access to the actions scope of the `GITHUB_TOKEN`. This enables the workflow to be able to delete cache keys.
If the `write` permission does not enable the ability to delete the cache keys, then a repository admin will need to create a `Personal Access Token (PAT)` with write permissions, add it as a secret, and then specify the name in `GH_TOKEN`.