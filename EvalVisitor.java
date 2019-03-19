import java.util.*;

public class EvalVisitor extends calculatorBaseVisitor<Value> {

    Map<String, Value> map = new HashMap<>();
    Map<String, calculatorParser.Method_defineContext> funcs = new HashMap<>();
    Deque<String> stack = new ArrayDeque<>();
    List<String> list = null;
    int counter = 0;

    @Override
    public Value visitBlock(calculatorParser.BlockContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitFor_sta(calculatorParser.For_staContext ctx) {
        return visit(ctx.for_stat());
    }

    @Override
    public Value visitIf_sta(calculatorParser.If_staContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitAss(calculatorParser.AssContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitExpr_sta(calculatorParser.Expr_staContext ctx) {
            Value cur = visit(ctx.expr());
            if(cur.isInt){
                System.out.println((int)cur.value);
            } else {
                System.out.println(cur.value);
            }

        return null;
    }

    @Override
    public Value visitMethod_sta(calculatorParser.Method_staContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitRela_sta(calculatorParser.Rela_staContext ctx) { return visitChildren(ctx); }

    @Override public Value visitLoop(calculatorParser.LoopContext ctx) { return visitChildren(ctx); }


    @Override
    public Value visitStat_block(calculatorParser.Stat_blockContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitMethod_define(calculatorParser.Method_defineContext ctx) {
        String methodName = ctx.methodName().getText();
        funcs.put(methodName, ctx);
        return null;
    }

    @Override
    public Value visitBlock_start(calculatorParser.Block_startContext ctx) {
        stack.offerFirst("check_point");
        counter++;
        return visitChildren(ctx);
    }

    @Override
    public Value visitBlock_end(calculatorParser.Block_endContext ctx) {
        while(stack.size() != 0 && stack.peekFirst().compareTo("check_point") != 0){
            String cur = stack.pollFirst();
            map.remove(cur);
        }
        String test = stack.pollFirst();

        counter--;
        return visitChildren(ctx);
    }


    @Override
    public Value visitMethod_call(calculatorParser.Method_callContext ctx) {
        calculatorParser.Method_defineContext execution = funcs.get(ctx.methodName().getText());
        if(execution == null){
            System.out.println("function not exist");
            return null;
        }
        int i = 0;
        list = new ArrayList<>();
        for(calculatorParser.ExprContext cur : execution.methodCallArguments().expr() ){
            String _id = cur.getText();
            Value v = visit(ctx.methodCallArguments().expr(i));
            i++;
            map.put(_id, v);
            list.add(_id);
        }
        visit(execution.stat_block());
        for(String s : list){
            map.remove(s);
        }
        return null;
    }


    @Override
    public Value visitMethodName(calculatorParser.MethodNameContext ctx) {
        return visitChildren(ctx);
    }


    @Override
    public Value visitMethodCallArguments(calculatorParser.MethodCallArgumentsContext ctx) { return visitChildren(ctx); }


    @Override
    public Value visitIf_stat(calculatorParser.If_statContext ctx) {
        List<calculatorParser.Condition_blockContext> list = ctx.condition_block();
        boolean flag = false;
        for(calculatorParser.Condition_blockContext cur : list){
            Value evaluated = visit(cur.relational() );
            boolean res = evaluated.value == 1 ? true : false ;
            if(res){
                flag = true;
                visit(cur.stat_block() );
                break;
            }
        }
        if(!flag && ctx.stat_block() != null){
            visit(ctx.stat_block() );
        }
        return null;
    }

    @Override
    public Value visitFor_stat(calculatorParser.For_statContext ctx) {
        if(ctx.assignment() != null){
            visit(ctx.assignment() );
        }
        while (visit(ctx.relational()).value != 0){
            visit(ctx.stat_block());
            visit(ctx.expr());
        }
        return null;
    }

    @Override
    public Value visitWhile(calculatorParser.WhileContext ctx) {
        while( visit(ctx.relational()).value != 0) {
            visit(ctx.stat_block());
        }
        return null;
    }

    @Override
    public Value visitCondition_block(calculatorParser.Condition_blockContext ctx) { return visitChildren(ctx); }


    @Override
    public Value visitAssignment(calculatorParser.AssignmentContext ctx) {
        String id = ctx.ID().getText();
        if(counter != 0 && !map.containsKey(id) ){
            stack.offerFirst(id);
        }
        Value value = visit(ctx.expr());
        map.put(id, value);
        return value;
    }

    @Override
    public Value visitAdd(calculatorParser.AddContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value + right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override
    public Value visitMinus(calculatorParser.MinusContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value - right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override public Value visitFromPP(calculatorParser.FromPPContext ctx) {
        Value res = visit(ctx.expr());
        map.put(ctx.expr().getText(), new Value(res.value + 1));
        res.value = res.value + 1;
        return res;
    }

    @Override
    public Value visitMod(calculatorParser.ModContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value % right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override
    public Value visitBrace(calculatorParser.BraceContext ctx) {
        return visit(ctx.expr());
    }

    @Override public Value visitModEqual(calculatorParser.ModEqualContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value % right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        String _id = ctx.expr(0).getText();
        if(counter != 0 && !map.containsKey(_id) ){
            stack.offerFirst(_id);
        }
        map.put(_id, res);

        return null;
    }

    @Override
    public Value visitMinusEqual(calculatorParser.MinusEqualContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value - right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        String _id = ctx.expr(0).getText();
        if(counter != 0 && !map.containsKey(_id) ){
            stack.offerFirst(_id);
        }
        map.put(_id, res);

        return null;
    }

    @Override
    public Value visitVisitfloat(calculatorParser.VisitfloatContext ctx) {
        Value res = new Value();
        res.value =  Double.valueOf(ctx.FLOAT().getText());
        res.isInt = false;
        return res;
    }

    @Override
    public Value visitVisitID(calculatorParser.VisitIDContext ctx) {
        Value x = new Value();
        Value res = map.get(ctx.ID().getText());
        if(res == null){
            res = x;
        }
        map.put(ctx.ID().getText(), res);
        if((int)res.value == res.value){
            res.isInt = true;
        }
        return res;
    }

    @Override
    public Value visitVisitInt(calculatorParser.VisitIntContext ctx) {
        Value res = new Value();
        res.value =  Integer.valueOf(ctx.INT().getText());
        res.isInt = true;
        return res;
    }

    @Override
    public Value visitTimeEqual(calculatorParser.TimeEqualContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value * right.value;
        Value res = new Value();
        res.value = x;
        if((int)x == x) res.isInt = true;

        String _id = ctx.expr(0).getText();
        if(counter != 0 && !map.containsKey(_id)){
            stack.offerFirst(_id);
        }
        map.put(_id, res);
        return null;
    }

    @Override
    public Value visitEndPP(calculatorParser.EndPPContext ctx) {
        Value res = visit(ctx.expr());
        map.put(ctx.expr().getText(),  new Value(res.value + 1) );
        return res;
    }

    @Override
    public Value visitEndMM(calculatorParser.EndMMContext ctx) {
        Value res = visit(ctx.expr());
        map.put(ctx.expr().getText(),  new Value(res.value - 1) );
        return res;
    }

    @Override
    public Value visitDivide(calculatorParser.DivideContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value / right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override
    public Value visitPower(calculatorParser.PowerContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = Math.pow(left.value, right.value);
        Value res = new Value();
        res.value =x;
        res.isInt = false;
        return res;
    }

    @Override
    public Value visitTime(calculatorParser.TimeContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value * right.value;
        Value res = new Value();
        res.value =x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override
    public Value visitFrontMM(calculatorParser.FrontMMContext ctx) {
        Value res = visit(ctx.expr());

        res.value = res.value - 1;
        map.put(ctx.expr().getText(), res );
        return res;
    }

    @Override
    public Value visitFlip(calculatorParser.FlipContext ctx) {
        Value res = visit(ctx.expr());
        double x = -res.value;
        res = new Value();
        res.value = x;
        if((int)x == x) res.isInt = true;
        return res;
    }

    @Override
    public Value visitPlusEqual(calculatorParser.PlusEqualContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value + right.value;
        Value res = new Value();
        res.value = x;
        if((int)x == x) res.isInt = true;
        String _id = ctx.expr(0).getText();
        if(counter != 0 && !map.containsKey(_id)){
            stack.offerFirst(_id);
        }
        map.put(_id, res);
        return null;
    }

    @Override
    public Value visitDivideEqual(calculatorParser.DivideEqualContext ctx) {
        Value left = visit(ctx.expr(0));
        Value right = visit(ctx.expr(1));
        double x = left.value/right.value;
        Value res = new Value();
        res.value = x;
        if((int)x == x) res.isInt = true;
        String _id = ctx.expr(0).getText();
        if(counter != 0 && !map.containsKey(_id)){
            stack.offerFirst(_id);
        }
        map.put(_id, res);

        return null;
    }


    @Override
    public Value visitCondition(calculatorParser.ConditionContext ctx) {
        double left = visit(ctx.expr(0)).value;
        double right = visit(ctx.expr(1)).value;
        String compare = ctx.compare().getText();
        int res = 0;
        switch (compare){
            case "==" : res = left == right ? 1 : 0; break;
            case ">=" : res = left >= right ? 1 : 0; break;
            case "<=" : res = left <= right ? 1 : 0; break;
            case "!=" : res = left != right ? 1 : 0; break;
            case ">"  : res = left > right ? 1 : 0; break;
            case "<"  : res = left < right ? 1 : 0; break;
        }
        Value x = new Value();
        x.value = res;
        x.isInt = true;
        return x;
    }

    @Override
    public Value visitFlipCondition(calculatorParser.FlipConditionContext ctx) {
        Value res = visit(ctx.expr());
        res.value = (res.value + 1)%2;
        return res;
    }

    @Override
    public Value visitCompare(calculatorParser.CompareContext ctx) {
        return visitChildren(ctx);
    }
}