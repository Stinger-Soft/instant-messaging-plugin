package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.plugins.im.Sender;
import hudson.plugins.im.bot.AbstractMultipleJobCommand.Mode;
import hudson.plugins.im.tools.Pair;
import hudson.plugins.im.util.BuildableItemDelegator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class AbstractMultipleJobCommandTest {

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testGetJobByName() throws CommandException {
        String projectName = "project name with spaces";

        BuildableItemDelegator project = mock(BuildableItemDelegator.class);

        JobProvider jobProvider = mock(JobProvider.class);
        when(jobProvider.getJobByNameOrDisplayName(projectName)).thenReturn(project);

        String[] projArgs = StringUtils.split(projectName);
        String[] args = new String[1 + projArgs.length];
        args[0] = "health";
        System.arraycopy(projArgs, 0, args, 1, projArgs.length);

        HealthCommand cmd = new HealthCommand();
        cmd.setJobProvider(jobProvider);

        List<BuildableItemDelegator> projects = new ArrayList<>();
        Pair<Mode, String> pair = cmd.getProjects(new Sender("sender"), args, projects);

        assertEquals(Mode.SINGLE, pair.getHead());
        assertNull(pair.getTail());

        assertEquals(1, projects.size());
        assertSame(project, projects.get(0));
    }

    @Test(expected = CommandException.class)
    public void testUnknownJobName() throws CommandException {
        JobProvider jobProvider = mock(JobProvider.class);

        String[] args = {"health", "doesnt-matter-jobname"};

        HealthCommand cmd = new HealthCommand();
        cmd.setJobProvider(jobProvider);

        List<BuildableItemDelegator> projects = new ArrayList<>();
        cmd.getProjects(new Sender("sender"), args, projects);
    }

    @Test
    public void testGetByView() throws Exception {
        BuildableItemDelegator project = mock(BuildableItemDelegator.class);

        View mockView = mock(View.class);
        Collection<TopLevelItem> projectsForView = new HashSet<>();
        projectsForView.add(project);
        when(mockView.getItems()).thenReturn(projectsForView);

        String viewName = "myView";

        JobProvider jobProvider = mock(JobProvider.class);
        when(jobProvider.getView(viewName)).thenReturn(mockView);

        String[] args = {"health", "-v", viewName};
        HealthCommand cmd = new HealthCommand();
        cmd.setJobProvider(jobProvider);

        List<BuildableItemDelegator> projects = new ArrayList<>();
        Pair<Mode, String> pair = cmd.getProjects(new Sender("sender"), args, projects);

        assertEquals(Mode.VIEW, pair.getHead());
        assertEquals(viewName, pair.getTail());

        assertEquals(1, projects.size());
        assertSame(project, projects.get(0));
    }
}
