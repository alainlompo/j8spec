package j8spec.junit;

import j8spec.Example;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runners.model.Statement;

final class ExampleStatement extends Statement {

    private final Example example;

    public static Statement newStatement(Example example) {
        Statement statement = new ExampleStatement(example);

        if (example.isExpectedToThrowAnException()) {
            statement = new ExpectException(statement, example.expected());
        }

        return statement;
    }

    private ExampleStatement(Example example) {
        this.example = example;
    }

    @Override
    public void evaluate() throws Throwable {
        example.tryToExecute();
    }
}