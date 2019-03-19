grammar calculator;

block
    : stat*  NEWLINE?
    ;

stat
    : while_loop            # loop
    | expr                  # expr_sta
    | assignment            # ass
    | if_stat               # if_sta
    | for_stat              # for_sta
    | relational            # rela_sta
    | method                # method_sta
    ;

block_start
    : '{'
    ;

block_end
    : '}'
    ;

stat_block
    :  block_start  block  block_end  NEWLINE?
    ;

method
    : 'define'  methodName '(' methodCallArguments ')' stat_block # method_define
    | methodName '(' methodCallArguments ')'                      # method_call
    ;

methodName
    : ID
    ;

methodCallArguments
    : // No arguments
    | expr? (',' expr)*  // Some arguments
    ;


if_stat
    : 'if' condition_block  ('else' 'if' condition_block )* ('else' stat_block )?
    ;

condition_block
    : '(' relational ')'  stat_block
    ;

for_stat
    : 'for' '(' assignment ';' relational ';' expr ')' stat_block
    ;

while_loop: 'while' '('relational')' NEWLINE? stat_block   #while
    ;

assignment
 : ID '=' expr
 ;

expr  returns [double i]
    : '(' expr ')'                       # brace
    |  expr'+''+'                        # endPP
    |  expr'-''-'                        # endMM
    |  '+''+'expr                        # fromPP
    |  '-''-'expr                        # frontMM
    |  '-'e = expr                                   #flip
    |  <assoc=right> eLeft = expr '^' eRight = expr  # power
    |  eLeft = expr '*' eRight = expr     # time
    |  eLeft = expr '%' eRight = expr     # mod
    |  eLeft = expr '/' eRight = expr     # divide
    |  eLeft = expr '+' eRight = expr     # add
    |  eLeft = expr '-' eRight = expr     # minus
    |  expr '*=' e = expr                   # timeEqual
    |  expr '/=' e = expr                   # divideEqual
    |  expr '%=' e = expr                   # modEqual
    |  expr '+=' e = expr                   # plusEqual
    |  expr '-=' e = expr                   # minusEqual
    |  FLOAT                                # visitfloat
    |  INT                                  # visitInt
    |  ID                                   # visitID

    ;


    // i = 1
    // i %= 3;  1;

relational: expr compare expr    # condition
          | '!' expr        # flipCondition
          ;

compare: '>'
    | '=='
    | '>='
    | '<'
    | '<='
    | '!='
    | '||'
    |  '&&'
    |  '!'
    ;

fragment DIGIT : [0-9] ;

COM:  '/*' .*? '*/' -> skip;
ID : [_A-Za-z] [a-zA-Z_0-9]*;
INT: DIGIT+ ;
WS : [\t ]+ -> skip;
NEWLINE : [\r\n]*   -> skip ;

FLOAT: DIGIT+ '.' DIGIT*
     | '.' DIGIT+
     ;