package j8spec;

import java.util.List;
import java.util.function.Function;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.ExampleGroupDefinition.newExampleGroupDefinition;
import static java.util.function.Function.identity;

/**
 * J8Spec main facade.
 *
 * <p>
 *     <b>Note:</b> this class is thread-safe.
 * </p>
 *
 * @since 1.0.0
 */
public final class J8Spec {

    private static final ThreadLocal<Context<ExampleGroupDefinition>> contexts = new ThreadLocal<>();

    /**
     * Defines a new "describe" block.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 1.0.0
     */
    public static synchronized void describe(String description, SafeBlock block) {
        isValidContext("describe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #describe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 2.0.0
     */
    public static synchronized void context(String description, SafeBlock block) {
        isValidContext("context");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new ignored "describe" block.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xdescribe(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("xdescribe");
        isValidContext("xdescribe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #xdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xcontext(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("xcontext");
        isValidContext("xcontext");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new focused "describe" block.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fdescribe(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("fdescribe");
        isValidContext("fdescribe");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Alias for {@link #fdescribe(String, SafeBlock)}.
     *
     * @param description textual description of the new block
     * @param block code that defines inner blocks, like "describe", "it", etc - this code is executed
     *             immediately
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fcontext(String description, SafeBlock block) {
        notAllowedWhenCIModeEnabled("fcontext");
        isValidContext("fcontext");
        ExampleGroupConfiguration config = new ExampleGroupConfiguration.Builder()
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addGroup(config, block);
    }

    /**
     * Defines a new "before all" block.
     *
     * @param block code to be executed before all "it" blocks
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @since 2.0.0
     */
    public static synchronized void beforeAll(UnsafeBlock block) {
        isValidContext("beforeAll");
        contexts.get().current().addBeforeAll(block);
    }

    /**
     * Defines a new "before each" block.
     *
     * @param block code to be executed before each "it" block
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @since 1.0.0
     */
    public static synchronized void beforeEach(UnsafeBlock block) {
        isValidContext("beforeEach");
        contexts.get().current().addBeforeEach(block);
    }

    /**
     * Defines a new "it" block.
     *
     * @param description textual description of the new block
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 1.0.0
     */
    public static synchronized void it(String description, UnsafeBlock block) {
        it(description, identity(), block);
    }

    /**
     * Defines a new "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @since 2.0.0
     */
    public static synchronized void it(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        isValidContext("it");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(DEFAULT)
            .build();
        contexts.get().current().addExample(config, block);
    }

    /**
     * Defines a new ignored "it" block.
     *
     * @param description textual description of the new block
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(String description, UnsafeBlock block) {
        xit(description, identity(), block);
    }

    /**
     * Defines a new ignored "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void xit(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        notAllowedWhenCIModeEnabled("xit");
        isValidContext("xit");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(IGNORED)
            .build();
        contexts.get().current().addExample(config, block);
    }

    /**
     * Defines a new focused "it" block.
     *
     * @param description textual description of the new block
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(String description, UnsafeBlock block) {
        fit(description, identity(), block);
    }

    /**
     * Defines a new focused "it" block using custom configuration.
     *
     * @param description textual description of the new block
     * @param collector block configuration collector
     * @param block code to be executed
     * @throws IllegalContextException if called outside the context of the {@link #read(Class)} method
     * @throws BlockAlreadyDefinedException if another block with the same description in the same context has been
     * defined already
     * @throws CIModeEnabledException if the system property <code>j8spec.ci.mode</code> is <code>true</code>
     * @since 2.0.0
     */
    public static synchronized void fit(
        String description,
        Function<ExampleConfiguration.Builder, ExampleConfiguration.Builder> collector,
        UnsafeBlock block
    ) {
        notAllowedWhenCIModeEnabled("fit");
        isValidContext("fit");
        ExampleConfiguration config = collector.apply(new ExampleConfiguration.Builder())
            .description(description)
            .executionFlag(FOCUSED)
            .build();
        contexts.get().current().addExample(config, block);
    }

    private static void notAllowedWhenCIModeEnabled(final String methodName) {
        if (Boolean.valueOf(System.getProperty("j8spec.ci.mode", "false"))) {
            throw new CIModeEnabledException("'" + methodName + "' not allowed when j8spec.ci.mode enabled");
        }
    }

    private static void isValidContext(final String methodName) {
        if (contexts.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    /**
     * Uses the given spec class to build and populate a {@link ItBlock} objects.
     *
     * @param specClass class with a public default constructor that contains the spec definition
     * @return {@link ItBlock} objects that represent the spec definition
     * @throws SpecInitializationException if it is not possible to create an instance of <code>specClass</code>
     * @since 2.0.0
     */
    public static synchronized List<ItBlock> read(Class<?> specClass) {
        contexts.set(new Context<>());
        try {
            ExampleGroupDefinition exampleGroupDefinition = newExampleGroupDefinition(specClass, contexts.get());

            exampleGroupDefinition.accept(new DuplicatedBlockValidator());

            BlockExecutionStrategySelector strategySelector = new BlockExecutionStrategySelector();
            exampleGroupDefinition.accept(strategySelector);

            ExecutableSpecBuilder specBuilder = new ExecutableSpecBuilder(strategySelector.strategy());
            exampleGroupDefinition.accept(specBuilder);

            return specBuilder.build();
        } finally {
            contexts.set(null);
        }
    }

    private J8Spec() {}
}
