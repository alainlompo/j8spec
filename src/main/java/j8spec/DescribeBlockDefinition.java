package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.DescribeBlock.newRootDescribeBlock;

final class DescribeBlockDefinition {

    private final Class<?> specClass;
    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final Context<DescribeBlockDefinition> context;

    private final List<Runnable> beforeAllBlocks = new LinkedList<>();
    private final List<Runnable> beforeEachBlocks = new LinkedList<>();
    private final Map<String, ItBlockDefinition> itBlockDefinitions = new HashMap<>();

    private final List<DescribeBlockDefinition> describeBlockDefinitions = new LinkedList<>();

    static DescribeBlockDefinition newDescribeBlockDefinition(Class<?> specClass, Context<DescribeBlockDefinition> context) {
        DescribeBlockDefinition block = new DescribeBlockDefinition(specClass, context);
        context.switchTo(block);

        try {
            specClass.newInstance();
        } catch (J8SpecException e) {
            throw e;
        } catch (Exception e) {
            throw new SpecInitializationException("Failed to create instance of " + specClass + ".", e);
        }

        return block;
    }

    private DescribeBlockDefinition(Class<?> specClass, Context<DescribeBlockDefinition> context) {
        this(specClass, specClass.getName(), DEFAULT, context);
    }

    private DescribeBlockDefinition(
        Class<?> specClass,
        String description,
        BlockExecutionFlag executionFlag,
        Context<DescribeBlockDefinition> context
    ) {
        this.specClass = specClass;
        this.description = description;
        this.executionFlag = executionFlag;
        this.context = context;
    }

    void describe(String description, Runnable body) {
        addDescribe(description, body, DEFAULT);
    }

    void xdescribe(String description, Runnable body) {
        addDescribe(description, body, IGNORED);
    }

    void fdescribe(String description, Runnable body) {
        addDescribe(description, body, FOCUSED);
    }

    private void addDescribe(String description, Runnable body, BlockExecutionFlag executionFlag) {
        ensureIsNotAlreadyDefined(
            description,
            describeBlockDefinitions.stream().anyMatch(d -> d.description.equals(description))
        );

        DescribeBlockDefinition block = new DescribeBlockDefinition(specClass, description, executionFlag, context);
        describeBlockDefinitions.add(block);

        context.switchTo(block);
        body.run();
        context.restore();
    }

    void beforeAll(Runnable beforeAllBlock) {
        this.beforeAllBlocks.add(beforeAllBlock);
    }

    void beforeEach(Runnable beforeEachBlock) {
        this.beforeEachBlocks.add(beforeEachBlock);
    }

    void it(String description, ItBlockDefinition itBlockDefinition) {
        ensureIsNotAlreadyDefined(description, itBlockDefinitions.containsKey(description));
        itBlockDefinitions.put(description, itBlockDefinition);
    }

    private void ensureIsNotAlreadyDefined(String blockName, boolean result) {
        if (result) {
            throw new BlockAlreadyDefinedException(blockName + " block already defined");
        }
    }

    DescribeBlock toDescribeBlock() {
        return toDescribeBlock(null);
    }

    private DescribeBlock toDescribeBlock(DescribeBlock parent) {
        DescribeBlock describeBlock = newDescribeBlock(parent);
        this.describeBlockDefinitions.stream().forEach(block -> block.toDescribeBlock(describeBlock));
        return describeBlock;
    }

    private DescribeBlock newDescribeBlock(DescribeBlock parent) {
        if (parent == null) {
            return newRootDescribeBlock(specClass, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }
        return addDescribeBlockTo(parent);
    }

    private DescribeBlock addDescribeBlockTo(DescribeBlock parent) {
        if (IGNORED.equals(executionFlag)) {
            return parent.addIgnoredDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }

        if (FOCUSED.equals(executionFlag)) {
            return parent.addFocusedDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }

        return parent.addDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
    }
}
