package hudson.plugins.im.bot;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Executor;
import hudson.model.Run;
import hudson.plugins.im.Sender;
import hudson.plugins.im.util.BuildableItemDelegator;
import hudson.security.Permission;

import java.util.Collection;
import java.util.Collections;
import jenkins.model.Jenkins;

/**
 * Abort a running job
 *
 * @author R. Tyler Ballance <tyler@slide.com>
 */
@Extension
public class AbortCommand extends AbstractSingleJobCommand {

    @Override
    public Collection<String> getCommandNames() {
        return Collections.singleton("abort");
    }

    @Override
    public String getHelp() {
        return " <job> - specify which job to abort";
    }

    @Override
    protected CharSequence getMessageForJob(BuildableItemDelegator project, Sender sender, String[] args) throws CommandException {
        if ((project.isInQueue() == false) && (project.isBuilding() == false)) {
            throw new CommandException(
                    sender + ": how do you intend to abort a build that isn't building?");
        }

        boolean aborted = false;
        if (project.isInQueue() && project.asTask() != null) {
            aborted = Jenkins.getActiveInstance().getQueue().cancel(project.asTask());
        }

        if (!aborted) {
            // must be already building
            Run<?, ?> build = project.getLastBuild();
            if (build == null) {
                // No builds?
                throw new CommandException(
                        sender.getNickname() + ": it appears this job has never been built");
            }

            // TODO: do build.doStop() instead of the following lines when moving to core 1.489!
            Executor ex = build.getExecutor();
            if (ex == null) {
                aborted = false; // how the hell does this happen o_O
            } else {
                ex.interrupt();
                aborted = true;
            }
        }

        if (aborted) {
            return project.getFullDisplayName() + " aborted, I hope you're happy!";
        } else {
            throw new CommandException(
                    sender.getNickname() + ": " + " couldn't abort " + project.getFullDisplayName() + ". I don't know why this happened.");
        }
    }

    @Override
    protected Permission getRequiredPermission() {
        return AbstractProject.ABORT;
    }
}
