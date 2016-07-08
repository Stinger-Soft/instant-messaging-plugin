package hudson.plugins.im.bot;

import hudson.model.View;
import hudson.plugins.im.util.BuildableItemDelegator;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface JobProvider {

    /**
     * Returns the Jenkins job with the given name or null if no job with that
     * name exists.
     *
     * @param name
     * @return
     */
    @CheckForNull
    BuildableItemDelegator getJobByName(String name);

    /**
     * Returns the Jenkins job with the given display name or null if no job
     * with that display name exists.
     * <p>
     * Attention: this might be relativley expensive, if you've a lot of Jenkins
     * jobs!
     *
     * @param displayName
     * @return
     */
    @CheckForNull
    BuildableItemDelegator getJobByDisplayName(String displayName);

    /**
     * Convenience method to 1st try to get by name and then by display name.
     *
     * @param displayName
     * @return
     */
    @CheckForNull
    BuildableItemDelegator getJobByNameOrDisplayName(String displayName);

    /**
     * Returns all Jenkins jobs.
     *
     * @return a list with all Jenkins jobs. Never null.
     */
    @Nonnull
    List<BuildableItemDelegator> getAllJobs();

    /**
     * Returns all top-level Jenkins jobs.
     *
     * @return a list with the top-level jobs. Never null.
     */
    @Nonnull
    List<BuildableItemDelegator> getTopLevelJobs();

    boolean isTopLevelJob(BuildableItemDelegator job);

    /**
     * Return the view by name.
     *
     * @param viewName the view name
     * @return the view or null, if no view by that name exists.
     */
    @CheckForNull
    View getView(String viewName);
}
