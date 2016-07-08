/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.im.util;

import com.infradna.tool.bridge_method_injector.WithBridgeMethods;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.HealthReport;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import hudson.model.queue.QueueTaskFuture;
import hudson.search.QuickSilver;
import hudson.search.Search;
import hudson.search.SearchIndex;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import jenkins.model.lazy.LazyBuildMixIn;
import org.acegisecurity.AccessDeniedException;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.kohsuke.stapler.export.Exported;

/**
 *
 * @author oliver.kotte
 */
public class BuildableItemDelegator implements AccessControlled, TopLevelItem {

    private final Job<?, ?> job;

    public BuildableItemDelegator(@Nonnull Job<?, ?> job) {
        this.job = job;
    }

    @Override
    public ACL getACL() {
        return this.job.getACL();
    }

    @Override
    public void checkPermission(Permission permission) throws AccessDeniedException {
        this.job.checkPermission(permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.job.hasPermission(permission);
    }

    @Override
    public ItemGroup<? extends Item> getParent() {
        return this.job.getParent();
    }

    @Override
    public Collection<? extends Job> getAllJobs() {
        return this.job.getAllJobs();
    }

    @Override
    public String getName() {
        return this.job.getName();
    }

    @Override
    public String getFullName() {
        return this.job.getFullName();
    }

    @Override
    public String getDisplayName() {
        return this.job.getDisplayName();
    }

    @Override
    public String getFullDisplayName() {
        return this.job.getFullDisplayName();
    }

    @Override
    public String getRelativeNameFrom(ItemGroup g) {
        return this.job.getRelativeNameFrom(g);
    }

    @Override
    public String getRelativeNameFrom(Item item) {
        return this.job.getRelativeNameFrom(item);
    }

    @Override
    public String getUrl() {
        return this.job.getUrl();
    }

    @Override
    public String getShortUrl() {
        return this.job.getShortUrl();
    }

    @Override
    public String getAbsoluteUrl() {
        return this.job.getAbsoluteUrl();
    }

    @Override
    public void onLoad(ItemGroup<? extends Item> parent, String name) throws IOException {
        this.job.onLoad(parent, name);
    }

    @Override
    public void onCopiedFrom(Item src) {
        this.job.onCopiedFrom(src);
    }

    @Override
    public void onCreatedFromScratch() {
        this.job.onCreatedFromScratch();
    }

    @Override
    public void save() throws IOException {
        this.job.save();
    }

    @Override
    public void delete() throws IOException, InterruptedException {
        this.job.delete();
    }

    @Override
    public File getRootDir() {
        return this.job.getRootDir();
    }

    @Override
    public Search getSearch() {
        return this.job.getSearch();
    }

    @Override
    public String getSearchName() {
        return this.job.getSearchName();
    }

    @Override
    public String getSearchUrl() {
        return this.job.getSearchUrl();
    }

    @Override
    public SearchIndex getSearchIndex() {
        return this.job.getSearchIndex();
    }

    public Run<?, ?> getBuildByNumber(int n) {
        return this.job.getBuildByNumber(n);
    }

    public Queue.Item getQueueItem() {
        return this.job.getQueueItem();
    }

    public int getQuietPeriod() {
        if (this.job instanceof ParameterizedJobMixIn.ParameterizedJob) {
            return ((ParameterizedJobMixIn.ParameterizedJob) this.job).getQuietPeriod();
        }
        return Jenkins.getActiveInstance().getQuietPeriod();
    }

    public boolean isBuildable() {
        return this.job.isBuildable();
    }

    public boolean isParameterized() {
        return getParameterizedJobMixIn() != null ? getParameterizedJobMixIn().isParameterized() : false;
    }

    private ParameterizedJobMixIn<?, ?> getParameterizedJobMixIn() {
        if (this.job instanceof AbstractProject) {
            return new ParameterizedJobMixIn() {
                @SuppressWarnings("unchecked") // untypable
                @Override
                protected AbstractProject asJob() {
                    return (AbstractProject) BuildableItemDelegator.this.job;
                }
            };
        } else if (this.job instanceof WorkflowJob) {
            return new ParameterizedJobMixIn<WorkflowJob, WorkflowRun>() {
                @SuppressWarnings("unchecked") // untypable
                @Override
                protected WorkflowJob asJob() {
                    return (WorkflowJob) BuildableItemDelegator.this.job;
                }
            };
        }
        return null;
    }

    /**
     * Gets the specific property, or null if the propert is not configured for
     * this job.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T extends JobProperty> T getProperty(Class<T> clazz) {
        return this.job.getProperty(clazz);
    }

    /**
     * Schedules a build of this project.
     *
     * @param c
     * @return true if the project is added to the queue. false if the task was
     * rejected from the queue (such as when the system is being shut down.)
     */
    public boolean scheduleBuild(Cause c) {
        return getParameterizedJobMixIn().scheduleBuild(c);
    }

    public boolean scheduleBuild(int quietPeriod, Cause c) {
        return getParameterizedJobMixIn().scheduleBuild(quietPeriod, c);
    }

    /**
     * Schedules a build.
     *
     * Important: the actions should be persistable without outside references
     * (e.g. don't store references to this project). To provide parameters for
     * a parameterized project, add a ParametersAction. If no ParametersAction
     * is provided for such a project, one will be created with the default
     * parameter values.
     *
     * @param quietPeriod the quiet period to observer
     * @param c the cause for this build which should be recorded
     * @param actions a list of Actions that will be added to the build
     * @return whether the build was actually scheduled
     */
    public boolean scheduleBuild(int quietPeriod, Cause c, Action... actions) {
        return scheduleBuild2(quietPeriod, c, actions) != null;
    }

    /**
     * Schedules a build of this project, and returns a {@link Future} object to
     * wait for the completion of the build.
     *
     * @param quietPeriod
     * @param c
     * @param actions For the convenience of the caller, this array can contain
     * null, and those will be silently ignored.
     * @return
     */
    @WithBridgeMethods(Future.class)
    public QueueTaskFuture<?> scheduleBuild2(int quietPeriod, Cause c, Action... actions) {
        return scheduleBuild2(quietPeriod, c, Arrays.asList(actions));
    }

    /**
     * Schedules a build of this project, and returns a {@link Future} object to
     * wait for the completion of the build.
     *
     * @param quietPeriod
     * @param actions For the convenience of the caller, this collection can
     * contain null, and those will be silently ignored.
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    @WithBridgeMethods(Future.class)
    public QueueTaskFuture<?> scheduleBuild2(int quietPeriod, Cause c, Collection<? extends Action> actions) {
        List<Action> queueActions = new ArrayList<>(actions);
        if (c != null) {
            queueActions.add(new CauseAction(c));
        }
        return getParameterizedJobMixIn().scheduleBuild2(quietPeriod, queueActions.toArray(new Action[queueActions.size()]));
    }

    /**
     * Returns the last build.
     *
     * @return
     * @see LazyBuildMixIn#getLastBuild
     */
    @Exported
    @QuickSilver
    public Run<?, ?> getLastBuild() {
        return this.job.getLastBuild();
    }

    /**
     * Returns true if a build of this project is in progress.
     *
     * @return
     */
    public boolean isBuilding() {
        Run<?, ?> b = getLastBuild();
        return b != null && b.isBuilding();
    }

    /**
     * Returns true if the build is in the queue.
     *
     * @return
     */
    public boolean isInQueue() {
        if (this.job instanceof AbstractProject) {
            return Jenkins.getActiveInstance().getQueue().contains((AbstractProject) this.job);
        } else if (this.job instanceof WorkflowJob) {
            return Jenkins.getActiveInstance().getQueue().contains((WorkflowJob) this.job);
        }
        return false;
    }

    public Queue.Task asTask() {
        if (this.job instanceof Queue.Task) {
            return (Queue.Task) this.job;
        }
        return null;
    }

    public boolean isDisabled() {
        if (this.job instanceof AbstractProject) {
            return ((AbstractProject) this.job).isDisabled();
        }
        return false;
    }

    public List<HealthReport> getBuildHealthReports() {
        return this.job.getBuildHealthReports();
    }

    /**
     * Get the current health report for a job.
     *
     * @return the health report. Never returns null
     */
    public HealthReport getBuildHealth() {
        List<HealthReport> reports = getBuildHealthReports();
        return reports.isEmpty() ? new HealthReport() : reports.get(0);
    }

    /**
     * Returns the last completed build, if any. Otherwise null.
     *
     * @return
     */
    @Exported
    @QuickSilver
    public Run<?, ?> getLastCompletedBuild() {
        Run<?, ?> r = getLastBuild();
        while (r != null && r.isBuilding()) {
            r = r.getPreviousBuild();
        }
        return r;
    }

    @Override
    public TopLevelItemDescriptor getDescriptor() {
        if (this.job instanceof TopLevelItem) {
            return ((TopLevelItem) (this.job)).getDescriptor();
        }
        return null;
    }
}
