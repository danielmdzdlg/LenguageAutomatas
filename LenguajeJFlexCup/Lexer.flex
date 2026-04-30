%%

%class Lexer
%unicode
%cup

%%

// Palabras reservadas
"alto"      { return new Symbol(sym.ALTO); }
"grande"    { return new Symbol(sym.GRANDE); }

// Números
[0-9]+      { return new Symbol(sym.ENTERO, yytext()); }
[0-9]+"."[0-9]+ { return new Symbol(sym.DECIMAL, yytext()); }

// Identificadores
[a-zA-Z_][a-zA-Z0-9_]* { return new Symbol(sym.ID, yytext()); }

// Operadores
"+" { return new Symbol(sym.MAS); }
"-" { return new Symbol(sym.MENOS); }
"*" { return new Symbol(sym.POR); }
"/" { return new Symbol(sym.DIV); }
"=" { return new Symbol(sym.IGUAL); }

// Espacios
[ \t\r\n]+ { }

// Error
. { System.out.println("Caracter inválido: " + yytext()); }