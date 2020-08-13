
# Eclipse Foundation processes

## Create CQ:

1. Create a Git patch

   ```
   git format-patch -1 <sha>
   ```

   [More details](https://stackoverflow.com/questions/6658313/how-to-generate-a-git-patch-for-a-specific-commit/6658352#6658352)

2. Got to CQ create page:

   <https://projects.eclipse.org/projects/soa.winery/cq/create>

3. Select "Project Content" and follow the instructions

   * Copy CQ details from PR (title, description)
   * Use link to PR for "location" field

4. Attach patch file to the created CQ

5. Wait for completion ;)
