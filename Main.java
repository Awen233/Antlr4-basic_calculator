import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main{
    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream(is);
        calculatorLexer lexer = new calculatorLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        calculatorParser parser = new calculatorParser(tokens);
        ParseTree tree = parser.block(); // parse
        EvalVisitor eval = new EvalVisitor();
        eval.visit(tree);
    }
}