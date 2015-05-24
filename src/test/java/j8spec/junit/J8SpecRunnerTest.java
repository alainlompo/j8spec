package j8spec.junit;

import j8spec.ItBlock;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class J8SpecRunnerTest {

    private static final Runnable BEFORE_EACH_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};

    private static final Runnable BEFORE_EACH_A_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_A1 = () -> {};
    private static final Runnable IT_BLOCK_A2 = () -> {};

    private static final Runnable BEFORE_EACH_AA_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_AA1 = () -> {};
    private static final Runnable IT_BLOCK_AA2 = () -> {};

    private static final Runnable BEFORE_EACH_B_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_B1 = () -> {};
    private static final Runnable IT_BLOCK_B2 = () -> {};

    public static class SampleSpec {{
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        describe("describe A", () -> {
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);

            describe("describe A.A", () -> {
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", IT_BLOCK_AA1);
                it("block A.A.2", IT_BLOCK_AA2);
            });
        });

        describe("describe B", () -> {
            beforeEach(BEFORE_EACH_B_BLOCK);

            it("block B.1", IT_BLOCK_B1);
            it("block B.2", IT_BLOCK_B2);
        });
    }}

    @Test
    public void buildsListOfChildDescriptions() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);

        List<ItBlock> itBlocks = runner.getChildren();

        assertThat(itBlocks.get(0).getDescription(), is("block 1"));
        assertThat(itBlocks.get(1).getDescription(), is("block 2"));
    }

    @Test
    public void describesEachChild() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        Description block1Description = runner.describeChild(itBlocks.get(0));

        assertThat(block1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block1Description.getMethodName(), is("block 1"));

        Description block2Description = runner.describeChild(itBlocks.get(1));

        assertThat(block2Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block2Description.getMethodName(), is("block 2"));

        Description blockA1Description = runner.describeChild(itBlocks.get(2));

        assertThat(blockA1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(blockA1Description.getMethodName(), is("describe A block A.1"));
    }

    @Test
    public void runsEachChild() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestStarted(eq(description));
        verify(runNotifier).fireTestFinished(eq(description));
    }
}