import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class LexicalAnalyser {

    private enum State {
        START,
        NUMBER,
        ZERO,
        OPERATOR,
        DECIMAL,
        WHITESPACE_AFTER_NUMBER,
        WHITESPACE_AFTER_OPERATOR,
        ERROR
    };

    public static List <Token> analyse(String input) throws NumberException, ExpressionException {
        List <Token> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        State state = State.START;
        if (input.length() == 0) throw new ExpressionException("String cannot be empty");
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            switch (state) {
                case START:
                    if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                        state = State.NUMBER;
                        if (currentChar == '0') state = State.ZERO;
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        state = State.ERROR;
                        throw new ExpressionException("The first char cannot be an operator");
                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.ERROR;
                        throw new ExpressionException("The first char cannot be a whitespace");
                    } else if (currentChar == '.') {
                        state = State.ERROR;
                        throw new NumberException("The first char cannot be a dot");
                    } else {
                        state = State.ERROR;
                        throw new ExpressionException("Contain invalid char");
                    }
                    break;

                case NUMBER:
                    if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        tokens.add(new Token(Double.parseDouble(buffer.toString())));
                        tokens.add(new Token(Token.typeOf(currentChar)));
                        buffer.setLength(0);
                        state = State.OPERATOR;
                    } else if (Character.isWhitespace(currentChar)) {
                        tokens.add(new Token(Double.parseDouble(buffer.toString())));
                        buffer.setLength(0);
                        state = State.WHITESPACE_AFTER_NUMBER;
                    } else if (currentChar == '.') {
                        state = state.ERROR;
                        throw new NumberException("Decimal can only be smaller than 1");
                    } else {
                        state = State.ERROR;
                        throw new NumberException("Contain invalid char");
                    }
                    break;

                case ZERO:
                    if (Character.isDigit(currentChar)) {
                        throw new NumberException("There cannot be any digit after zero");
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        tokens.add(new Token(Double.parseDouble(buffer.toString())));
                        tokens.add(new Token(Token.typeOf(currentChar)));
                        buffer.setLength(0);
                        state = State.OPERATOR;
                    } else if (currentChar == '.') {
                        buffer.append(currentChar);
                        state = State.DECIMAL;
                    } else if (Character.isWhitespace(currentChar)) {
                        tokens.add(new Token(Double.parseDouble(buffer.toString())));
                        buffer.setLength(0);
                        state = State.WHITESPACE_AFTER_NUMBER;
                    } else {
                        state = State.ERROR;
                        throw new NumberException("Contain invalid char");
                    }
                    break;

                case OPERATOR:
                    if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                        state = State.NUMBER;
                        if (currentChar == '0') state = State.ZERO;
                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.WHITESPACE_AFTER_OPERATOR;
                    } else if (currentChar == '.') {
                        state = State.ERROR;
                        throw new NumberException("A number cannot start with dot");
                    } else {
                        state = State.ERROR;
                        throw new ExpressionException("Operator cannot be followed by an operator");
                    }
                    break;

                case DECIMAL:
                    if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                        state = State.NUMBER;
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        state = state.ERROR;
                        throw new ExpressionException("There cannot be an operator after a dot");
                    } else if (Character.isWhitespace(currentChar)) {
                        state = state.ERROR;
                        throw new ExpressionException("There cannot be a whitespace after a dot");
                    } else if (currentChar == '.') {
                        state = State.ERROR;
                        throw new ExpressionException("There cannot be a dot after a dot");
                    } else {
                        state = State.ERROR;
                        throw new NumberException("Contain invalid char");
                    }
                    break;

                case WHITESPACE_AFTER_NUMBER:
                    if (Character.isDigit(currentChar)) {
                        state = State.ERROR;
                        throw new ExpressionException("No whitespace between two digits");
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        tokens.add(new Token(Token.typeOf(currentChar)));
                        state = State.OPERATOR;

                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.WHITESPACE_AFTER_NUMBER;
                    } else if (currentChar == '.') {
                        state = State.ERROR;
                        throw new ExpressionException("No dot after whitespace");
                    } else {
                        state = State.ERROR;
                        throw new NumberException("Contain invalid char");
                    }
                    break;

                case WHITESPACE_AFTER_OPERATOR:
                    if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                        state = State.NUMBER;
						if (currentChar == '0') state = State.ZERO;
                    } else if (Token.typeOf(currentChar) != Token.TokenType.NONE) {
                        state = State.ERROR;
                        throw new ExpressionException("No two consecutive operators");
                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.WHITESPACE_AFTER_OPERATOR;
                    } else if (currentChar == '.') {
                        state = State.ERROR;
                        throw new NumberException("No dot after whitespace");
                    } else {
                        state = State.ERROR;
                        throw new NumberException("Contain invalid char");
                    }
                    break;

                case ERROR:
                    break;

                default:
                    state = State.ERROR;
                    throw new ExpressionException("Invalid state");
            }
        }

        if (state == State.DECIMAL && buffer.charAt(buffer.length() - 1) == '.') {
            throw new NumberException("Cannot end on a '.'");
        } else if (state == State.WHITESPACE_AFTER_NUMBER || state == State.WHITESPACE_AFTER_OPERATOR) {
            throw new ExpressionException("cannot end with whitespace");
        } else if (state == State.OPERATOR) {
            throw new ExpressionException("Expression cannot end with an operator");
        } else if (state == State.NUMBER || state == State.DECIMAL || state == State.ZERO) {
            tokens.add(new Token(Double.parseDouble(buffer.toString())));
        } else {
            throw new ExpressionException("Invalid expression");
        }
        return tokens;
    }
}
