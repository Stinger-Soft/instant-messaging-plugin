package hudson.plugins.im.bot;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hudson.model.Run;
import hudson.plugins.im.Sender;
import hudson.plugins.im.util.BuildableItemDelegator;

import java.io.IOException;

import org.junit.Test;

public class CommentCommandTest {

    @Test
    public void testSetComment() throws IOException, CommandException {
        @SuppressWarnings({"rawtypes"})
        BuildableItemDelegator project = mock(BuildableItemDelegator.class);
        Run build = mock(Run.class);
        when(project.getBuildByNumber(4711)).thenReturn(build);

        CommentCommand command = new CommentCommand();
        String result = command.getMessageForJob(project, new Sender("kutzi"),
                new String[]{"4711", "my comment"}).toString();
        assertEquals("Ok", result);

        verify(build).setDescription("my comment");
    }

    @Test(expected = CommandException.class)
    public void testMalformedBuildNumber() throws CommandException {
        BuildableItemDelegator project = mock(BuildableItemDelegator.class);

        CommentCommand command = new CommentCommand();
        command.getMessageForJob(project, new Sender("kutzi"),
                new String[]{"abc", "my comment"}).toString();
    }

    @Test(expected = CommandException.class)
    public void testUnknownBuildNumber() throws CommandException {
        @SuppressWarnings("rawtypes")
        BuildableItemDelegator project = mock(BuildableItemDelegator.class);
        Run build = mock(Run.class);
        when(project.getBuildByNumber(4711)).thenReturn(build);

        CommentCommand command = new CommentCommand();
        command.getMessageForJob(project, new Sender("kutzi"),
                new String[]{"4712", "my comment"}).toString();
    }
}
