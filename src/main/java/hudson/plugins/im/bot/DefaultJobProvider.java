package hudson.plugins.im.bot;

import hudson.model.Job;
import hudson.model.View;
import hudson.plugins.im.util.BuildableItemDelegator;
import java.util.ArrayList;

import java.util.List;

import jenkins.model.Jenkins;

/**
 * Default {@link JobProvider} which directly accesses
 * {@link Jenkins#getInstance()}.
 *
 * @author kutzi
 */
public class DefaultJobProvider implements JobProvider {

    @Override
    public BuildableItemDelegator getJobByName(String name) {
        return new BuildableItemDelegator(Jenkins.getActiveInstance().getItemByFullName(name, Job.class));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public BuildableItemDelegator getJobByDisplayName(String displayName) {
        List<Job> allItems = Jenkins.getActiveInstance().getAllItems(Job.class);
        for (Job job : allItems) {
            if (displayName.equals(job.getDisplayName())) {
                return new BuildableItemDelegator(job);
            }
        }
        return null;
    }

    @Override
    public BuildableItemDelegator getJobByNameOrDisplayName(String name) {
        BuildableItemDelegator jobByName = getJobByName(name);
        return jobByName != null ? jobByName : getJobByDisplayName(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BuildableItemDelegator> getAllJobs() {
        @SuppressWarnings("rawtypes")
        List<Job> items = Jenkins.getActiveInstance().getAllItems(Job.class);
        List<BuildableItemDelegator> delegators = new ArrayList<>();
        for (Job job : items) {
            delegators.add(new BuildableItemDelegator((job)));
        }
        return delegators;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BuildableItemDelegator> getTopLevelJobs() {
        @SuppressWarnings("rawtypes")
        List<Job> items = Jenkins.getInstance().getItems(Job.class);
        List<BuildableItemDelegator> delegators = new ArrayList<>();
        for (Job job : items) {
            delegators.add(new BuildableItemDelegator((job)));
        }
        return delegators;
    }

    @Override
    public boolean isTopLevelJob(BuildableItemDelegator job) {
        return Jenkins.getActiveInstance().equals(job.getParent());
    }

    @Override
    public View getView(String viewName) {
        return Jenkins.getActiveInstance().getView(viewName);
    }
}
