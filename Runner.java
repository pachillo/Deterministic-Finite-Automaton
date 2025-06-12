public class Runner {
    public static void main(String[] args) {
        try {
            System.out.println(LexicalAnalyser.analyse("9+0+8"));
        }
        catch (NumberException e) {
            System.out.println(e);
        }
        catch (ExpressionException e) {
            System.out.println(e);
        }
    }
}
